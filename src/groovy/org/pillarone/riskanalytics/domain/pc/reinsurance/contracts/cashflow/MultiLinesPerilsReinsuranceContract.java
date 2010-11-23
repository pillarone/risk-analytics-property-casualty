package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByFractionOfPeriod;
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments;
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType;
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.ICommissionStrategy;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.cover.AllCoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.cover.CoverAttributeStrategyType;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.*;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacketFactory;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * All periods have to be of equal length!
 * One contract covers a single duration, it may be longer than a period or overlapping.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLinesPerilsReinsuranceContract extends Component implements IReinsuranceContractMarker {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    /** claims whose source is a covered line and/or a covered peril */
    private PacketList<Claim> outFilteredClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> outFilteredUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<Claim> outClaimsGrossInCoveredPeriod = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outUncoveredClaims = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outCoveredClaims = new PacketList<Claim>(Claim.class);

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentGross = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentCeded = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentNet = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);

    private PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outCoverUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    /** Defines the kind of contract and parametrization */
    private IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractType.getTrivial();
    private ICommissionStrategy parmCommissionStrategy = CommissionStrategyType.getNoCommission();
    private ICoverAttributeStrategy parmCover = CoverAttributeStrategyType.getStrategy(
            CoverAttributeStrategyType.ALL, Collections.emptyMap());
    private ICoverPeriod parmCoverPeriod = new FullPeriodCoveredStrategy();
    private double parmCoveredByReinsurer = 1d;
    /**
     *  Defines the claim and underwriting info the contract will receive.
     *  Namely, the net after contracts with lower inuring priority.
     *
     *  Cave: Setting the inuring priority is not trivial. Make sure you have a
     *  correct understanding before 'playing around' with it.
     */
    private double parmInuringPriority = 0d;
    private ReinsuranceContractBase parmBasedOn = ReinsuranceContractBase.NET;

    private SimulationScope simulationScope;
    /** initialized during the first iteration, member variables reset before each iteration */
    private IReinsuranceContractStrategy contract;
    /** initialized during first iteration */
    private double coveredByReinsurer;
    /** initialized during first iteration */
    private List<CoverDuration> coverPerPeriod = new ArrayList<CoverDuration>();

    private int lastPeriodYear;

    public void doCalculation() {
        if (parmContractStrategy == null) throw new IllegalArgumentException("MultiLinesPerilsReinsuranceContract.missingContractStrategy");
        if (parmCover == null) throw new IllegalStateException("MultiLinesPerilsReinsuranceContract.missingCoverStrategy");

        IterationScope iterationScope = simulationScope.getIterationScope();
        PeriodScope periodScope = iterationScope.getPeriodScope();
        int currentPeriod = periodScope.getCurrentPeriod();
        if (iterationScope.isFirstIteration()) {
            if (periodScope.isFirstPeriod()) {
                initSimulation();
            }
            initDuringFirstIteration();
        }
        if (periodScope.isFirstPeriod()) {
            initIteration();
        }
        filterInChannels();

        CoverDuration coverOfCurrentPeriod = coverPerPeriod.get(currentPeriod);
        if (contract != null) { // && coverOfCurrentPeriod.isCovered()) {
//            if (!contract.exhausted()) {  // todo(sku): think if all following lines are really not necessary if a contract is exhausted
            filterClaimsInCoveredPeriod(currentPeriod, coverOfCurrentPeriod);

            if (periodScope.isFirstPeriod()) {
                contract.initBookKeepingFiguresForIteration(outClaimsGrossInCoveredPeriod, outFilteredUnderwritingInfo);
            }
            contract.initBookKeepingFigures(outClaimsGrossInCoveredPeriod, outFilteredUnderwritingInfo);
            if (periodScope.getCurrentPeriodStartDate().getYear() != lastPeriodYear) {
                contract.applyAnnualLimits();
                lastPeriodYear = periodScope.getCurrentPeriodStartDate().getYear();
            }
            contract.initBookKeepingFiguresOfPeriod(outClaimsGrossInCoveredPeriod, outFilteredUnderwritingInfo, parmCoveredByReinsurer);

            Collections.sort(outClaimsGrossInCoveredPeriod, SortClaimsByFractionOfPeriod.getInstance());
            if (isSenderWired(outUncoveredClaims)) {
                zeroPacket(outClaimsGrossInCoveredPeriod, inClaims.get(0).getClass());
                calculateClaims(outClaimsGrossInCoveredPeriod, outCoveredClaims, outUncoveredClaims, this);
            } else {
                zeroPacket(outClaimsGrossInCoveredPeriod, inClaims.get(0).getClass());
                calculateCededClaims(outClaimsGrossInCoveredPeriod, outCoveredClaims, this);
            }


            if (isSenderWired(outCoverUnderwritingInfo) || isSenderWired(outNetAfterCoverUnderwritingInfo)) {
                calculateCededUnderwritingInfos(outFilteredUnderwritingInfo, outCoverUnderwritingInfo);
            }
        }

        parmCommissionStrategy.calculateCommission(outCoveredClaims, outCoverUnderwritingInfo, periodScope.isFirstPeriod(), false);

        if (isSenderWired(outNetAfterCoverUnderwritingInfo)) {
            UnderwritingInfoUtilities.calculateNet(outFilteredUnderwritingInfo, outCoverUnderwritingInfo, outNetAfterCoverUnderwritingInfo);
        }


        if (isSenderWired(outClaimsDevelopmentGross)) {
            for (int i = 0; i < outClaimsGrossInCoveredPeriod.size(); i++) {
                outClaimsDevelopmentGross.add((ClaimDevelopmentPacket) outClaimsGrossInCoveredPeriod.get(i));
            }
        }
        if (isSenderWired(outClaimsDevelopmentCeded)) {
            for (int i = 0; i < outCoveredClaims.size(); i++) {
                outClaimsDevelopmentCeded.add((ClaimDevelopmentPacket) outCoveredClaims.get(i));
            }
        }
        if (isSenderWired(outClaimsDevelopmentNet)) {
            for (int i = 0; i < outUncoveredClaims.size(); i++) {
                outClaimsDevelopmentNet.add((ClaimDevelopmentPacket) outUncoveredClaims.get(i));
            }
        }
    }

    private void initSimulation() {
        lastPeriodYear = simulationScope.getIterationScope().getPeriodScope().getCurrentPeriodStartDate().getYear() - 1;  // force mismatch in first doCalculation run
    }

    protected void initDuringFirstIteration() {
        PeriodScope periodScope = simulationScope.getIterationScope().getPeriodScope();
        coverPerPeriod.add(parmCoverPeriod.getCoverDuration(
                periodScope.getCurrentPeriodStartDate(),
                periodScope.getNextPeriodStartDate()));
        if (!(parmContractStrategy instanceof TrivialContractStrategy)) {
            if (contract == null) {
                contract = parmContractStrategy;
                coveredByReinsurer = parmCoveredByReinsurer;
            }
            else if (contract.equals(parmContractStrategy)) {
                // same instance: may occur if one period parameterization is applied for several periods
            }
            else {
                throw new IllegalArgumentException("MultiLinesPerilsReinsuranceContract.invalidNoOfStrategies");
            }
        }
    }

    protected void initIteration() {
        if (contract != null) {
            contract.resetMemberInstances();
        }
    }

    protected void filterInChannels() {
        if (parmCover instanceof NoneCoverAttributeStrategy) {
            // leave outFiltered* lists void
        } else if (parmCover instanceof AllCoverAttributeStrategy) {
            outFilteredClaims.addAll(inClaims);
            outFilteredUnderwritingInfo.addAll(inUnderwritingInfo);
        }
        else {
            List<LobMarker> coveredLines = !(parmCover instanceof ILinesOfBusinessCoverAttributeStrategy) ? null :
                    (List<LobMarker>) (((ILinesOfBusinessCoverAttributeStrategy) parmCover).getLines().getValuesAsObjects());
            List<PerilMarker> coveredPerils = !(parmCover instanceof IPerilCoverAttributeStrategy) ? null :
                    (List<PerilMarker>) (((IPerilCoverAttributeStrategy) parmCover).getPerils().getValuesAsObjects());
            LogicArguments connection = !(parmCover instanceof ICombinedCoverAttributeStrategy) ? null :
                    ((ICombinedCoverAttributeStrategy) parmCover).getConnection();
            outFilteredClaims.addAll(ClaimFilterUtilities.filterClaimsByPerilLobReserve(inClaims, coveredPerils, coveredLines, null, connection));
            if (coveredLines == null || coveredLines.size() == 0) {
                coveredLines = ClaimFilterUtilities.getLinesOfBusiness(outFilteredClaims);
            }
            outFilteredUnderwritingInfo.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLob(inUnderwritingInfo, coveredLines));
        }

        /** commissions from prior reinsurance contracts are uncoupled from actual contracts,
         *  hence they are eliminated from incoming information, but considered on
         * program level in MultiLinesPerilsReinsuranceProgram
         */
        List<UnderwritingInfo> zeroCommissions = UnderwritingInfoUtilities.setCommissionZero(outFilteredUnderwritingInfo);
        outFilteredUnderwritingInfo.clear();
        outFilteredUnderwritingInfo.addAll(zeroCommissions);
    }

    private void filterClaimsInCoveredPeriod(int currentPeriod, CoverDuration coverOfCurrentPeriod) {
        for (Claim claim : outFilteredClaims) {
            if (claim.getClass().equals(ClaimDevelopmentPacket.class)) {
                int originalPeriod = ((ClaimDevelopmentPacket) claim).getOriginalPeriod();
                double fractionOfPeriod = claim.getFractionOfPeriod();
                boolean historicClaim = originalPeriod < 0;
                boolean originalPeriodCovered = !historicClaim && originalPeriod < coverPerPeriod.size()
                        && coverPerPeriod.get(originalPeriod).isCovered(fractionOfPeriod);
                if (originalPeriod == currentPeriod && coverOfCurrentPeriod.isCovered(fractionOfPeriod)
                        || originalPeriodCovered) {
                    outClaimsGrossInCoveredPeriod.add(claim);
                }
            }
            else if (claim.getClass().equals(Claim.class)) {
                double fractionOfPeriod = claim.getFractionOfPeriod();
                if (coverOfCurrentPeriod.isCovered(fractionOfPeriod)) {
                    outClaimsGrossInCoveredPeriod.add(claim);
                }
            }
            else {
                throw new NotImplementedException("['MultiLinesPerilsReinsuranceContract.notImplemented','"
                        +claim.getClass().toString()+"']");
            }
        }
    }

    public void calculateClaims(List<Claim> grossClaims, List<Claim> cededClaims, List<Claim> netClaims, Component origin) {
        for (Claim claim : grossClaims) {
            Claim cededClaim = getCoveredClaim(claim, origin);
            cededClaims.add(cededClaim);

            Claim claimNet = (Claim) claim.getNetClaim(cededClaim);
            setClaimReferences(claimNet, claim, origin);
            netClaims.add(claimNet);
        }
    }

    public void calculateCededClaims(List<Claim> grossClaims, List<Claim> cededClaims, Component origin) {
        for (Claim claim : grossClaims) {
            cededClaims.add(getCoveredClaim(claim, origin));
        }
    }

    protected Claim getCoveredClaim(Claim claim, Component origin) {
        Claim claimCeded = contract.calculateCededClaim(claim, coveredByReinsurer);
        setClaimReferences(claimCeded, claim, origin);
        return claimCeded;
    }

    private void setClaimReferences(Claim claim, Claim grossClaim, Component origin) {
        claim.origin = origin;
        claim.setReinsuranceContract(this);
        if (grossClaim.getOriginalClaim() != null) {
            claim.setOriginalClaim(grossClaim.getOriginalClaim());
        }
        else {
            claim.setOriginalClaim(grossClaim);
        }
    }

    protected void setOriginalUnderwritingInfo(UnderwritingInfo underwritingInfo, UnderwritingInfo derivedUnderwritingInfo) {
        if (underwritingInfo != null && underwritingInfo.originalUnderwritingInfo != null) {
            derivedUnderwritingInfo.originalUnderwritingInfo = underwritingInfo.originalUnderwritingInfo;
        }
        else {
            derivedUnderwritingInfo.originalUnderwritingInfo = underwritingInfo;
        }
    }

    protected void calculateCededUnderwritingInfos(List<UnderwritingInfo> grossUnderwritingInfos,
                                                   List<UnderwritingInfo> cededUnderwritingInfos) {
        for (UnderwritingInfo underwritingInfo : grossUnderwritingInfos) {
            UnderwritingInfo cededUnderwritingInfo = contract.calculateCoverUnderwritingInfo(underwritingInfo, coveredByReinsurer);
            setOriginalUnderwritingInfo(underwritingInfo, cededUnderwritingInfo);
            cededUnderwritingInfos.add(cededUnderwritingInfo);
        }
    }


     /**
     * zeroPackets are required to enable correct statistical keyfigure calculations. If no packet would be sent out
     * for a wired channel in an iteration and period, the resulting statistical keyfigures would be wrong as the total
     * number would not correspond to the number of iterations.
     * @param outList
     * @param clazz
     * @return if an list contains no packet a zero packet is added
     */
    private void zeroPacket(PacketList outList, Class clazz) {
        if (outList.size() > 0) return;
        Claim claim;
        if (clazz.equals(ClaimDevelopmentPacket.class)) {
            claim = ClaimDevelopmentPacketFactory.createPacket();
        }
        else {
            claim = ClaimPacketFactory.createPacket();
        }
        claim.setOrigin(this);
        claim.scale(0);
        outList.add(claim);
    }

    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }

    public double getParmCoveredByReinsurer() {
        return parmCoveredByReinsurer;
    }

    public void setParmCoveredByReinsurer(double parmCoveredByReinsurer) {
        this.parmCoveredByReinsurer = parmCoveredByReinsurer;
    }

    public double getParmInuringPriority() {
        return parmInuringPriority;
    }

    public void setParmInuringPriority(double parmInuringPriority) {
        this.parmInuringPriority = parmInuringPriority;
    }

    public PacketList<Claim> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<Claim> getOutUncoveredClaims() {
        return outUncoveredClaims;
    }

    public void setOutUncoveredClaims(PacketList<Claim> outUncoveredClaims) {
        this.outUncoveredClaims = outUncoveredClaims;
    }

    public PacketList<Claim> getOutCoveredClaims() {
        return outCoveredClaims;
    }

    public void setOutCoveredClaims(PacketList<Claim> outCoveredClaims) {
        this.outCoveredClaims = outCoveredClaims;
    }

    public PacketList<UnderwritingInfo> getOutNetAfterCoverUnderwritingInfo() {
        return outNetAfterCoverUnderwritingInfo;
    }

    public void setOutNetAfterCoverUnderwritingInfo(PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo) {
        this.outNetAfterCoverUnderwritingInfo = outNetAfterCoverUnderwritingInfo;
    }

    public PacketList<UnderwritingInfo> getOutCoverUnderwritingInfo() {
        return outCoverUnderwritingInfo;
    }

    public void setOutCoverUnderwritingInfo(PacketList<UnderwritingInfo> outCoverUnderwritingInfo) {
        this.outCoverUnderwritingInfo = outCoverUnderwritingInfo;
    }

    public ICoverPeriod getParmCoverPeriod() {
        return parmCoverPeriod;
    }

    public void setParmCoverPeriod(ICoverPeriod parmCoverPeriod) {
        this.parmCoverPeriod = parmCoverPeriod;
    }

    public PacketList<Claim> getOutClaimsGrossInCoveredPeriod() {
        return outClaimsGrossInCoveredPeriod;
    }

    public void setOutClaimsGrossInCoveredPeriod(PacketList<Claim> outClaimsGrossInCoveredPeriod) {
        this.outClaimsGrossInCoveredPeriod = outClaimsGrossInCoveredPeriod;
    }

    public ReinsuranceContractBase getParmBasedOn() {
        return parmBasedOn;
    }

    public void setParmBasedOn(ReinsuranceContractBase parmBasedOn) {
        this.parmBasedOn = parmBasedOn;
    }

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopmentGross() {
        return outClaimsDevelopmentGross;
    }

    public void setOutClaimsDevelopmentGross(PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentGross) {
        this.outClaimsDevelopmentGross = outClaimsDevelopmentGross;
    }

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopmentCeded() {
        return outClaimsDevelopmentCeded;
    }

    public void setOutClaimsDevelopmentCeded(PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentCeded) {
        this.outClaimsDevelopmentCeded = outClaimsDevelopmentCeded;
    }

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopmentNet() {
        return outClaimsDevelopmentNet;
    }

    public void setOutClaimsDevelopmentNet(PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentNet) {
        this.outClaimsDevelopmentNet = outClaimsDevelopmentNet;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public PacketList<Claim> getOutFilteredClaims() {
        return outFilteredClaims;
    }

    public void setOutFilteredClaims(PacketList<Claim> outFilteredClaims) {
        this.outFilteredClaims = outFilteredClaims;
    }

    public PacketList<UnderwritingInfo> getOutFilteredUnderwritingInfo() {
        return outFilteredUnderwritingInfo;
    }

    public void setOutFilteredUnderwritingInfo(PacketList<UnderwritingInfo> outFilteredUnderwritingInfo) {
        this.outFilteredUnderwritingInfo = outFilteredUnderwritingInfo;
    }

    public ICommissionStrategy getParmCommissionStrategy() {
        return parmCommissionStrategy;
    }

    public void setParmCommissionStrategy(ICommissionStrategy parmCommissionStrategy) {
        this.parmCommissionStrategy = parmCommissionStrategy;
    }

    public ICoverAttributeStrategy getParmCover() {
        return parmCover;
    }

    public void setParmCover(ICoverAttributeStrategy parmCover) {
        this.parmCover = parmCover;
    }
}