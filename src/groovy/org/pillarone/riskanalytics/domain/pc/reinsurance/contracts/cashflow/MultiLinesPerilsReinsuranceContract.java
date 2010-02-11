package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByFractionOfPeriod;
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * All periods have to be of equal length!
 * One contract covers a single duration, it may be longer than a period or overlapping.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLinesPerilsReinsuranceContract extends Component implements IReinsuranceContractMarker {

    private SimulationScope simulationScope;
    /**
     * initialized during the first iteration, member variables reset before each iteration
     */
    private IReinsuranceContractStrategy contract;
    /** initialized during first iteration */
    private double coveredByReinsurer;
    /** initialized during first iteration */
    private List<CoverDuration> coverPerPeriod = new ArrayList<CoverDuration>();

    private ComboBoxTableMultiDimensionalParameter parmCoveredLines = new ComboBoxTableMultiDimensionalParameter(
        Collections.emptyList(), Arrays.asList("Covered Lines"), LobMarker.class);
    private ComboBoxTableMultiDimensionalParameter parmCoveredPerils = new ComboBoxTableMultiDimensionalParameter(
        Collections.emptyList(), Arrays.asList("perils"), PerilMarker.class);

    /** Defines the kind of contract and parametrization      */
    private IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractType.getTrivial();
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

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    /** claims which source is a covered line         */
    private PacketList<Claim> outFilteredClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> outFilteredUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<Claim> outClaimsGrossInCoveredPeriod = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outUncoveredClaims = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outCoveredClaims = new PacketList<Claim>(Claim.class);

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentNet = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentGross = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentCeded = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);

    private PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outCoverUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    public void doCalculation() {
        if (parmContractStrategy == null) throw new IllegalArgumentException("A contract strategy must be set");

        IterationScope iterationScope = simulationScope.getIterationScope();
        PeriodScope periodScope = iterationScope.getPeriodScope();
        if (iterationScope.getCurrentIteration() == 0) {
            initDuringFirstIteration();
        }
        if (periodScope.getCurrentPeriod() == 0) {
            initIteration();
        }
        filterInChannels();

        CoverDuration coverOfCurrentPeriod = coverPerPeriod.get(periodScope.getCurrentPeriod());
        if (coverOfCurrentPeriod.isCovered()) {
//            if (!contract.exhausted()) {  // todo(sku): think if all following lines are really not necessary if a contract is exhausted
                filterClaimsInCoveredPeriod(periodScope.getCurrentPeriod(), coverOfCurrentPeriod);

                if (periodScope.getCurrentPeriod() == 0) {
                    contract.initBookKeepingFiguresForIteration(outClaimsGrossInCoveredPeriod, outFilteredUnderwritingInfo);
                }
                contract.initBookKeepingFigures(outClaimsGrossInCoveredPeriod, outFilteredUnderwritingInfo);
                contract.initBookKeepingFiguresOfPeriod(outClaimsGrossInCoveredPeriod, outFilteredUnderwritingInfo, parmCoveredByReinsurer);

                Collections.sort(outClaimsGrossInCoveredPeriod, SortClaimsByFractionOfPeriod.getInstance());
                if (isSenderWired(outUncoveredClaims)) {
                    calculateClaims(outClaimsGrossInCoveredPeriod, outCoveredClaims, outUncoveredClaims, this);
                } else {
                    calculateCededClaims(outClaimsGrossInCoveredPeriod, outCoveredClaims, this);
                }

                if (isSenderWired(outNetAfterCoverUnderwritingInfo)) {
                    calculateUnderwritingInfos(outFilteredUnderwritingInfo, outCoverUnderwritingInfo, outNetAfterCoverUnderwritingInfo);
                } else if (isSenderWired(outCoverUnderwritingInfo)) {
                    calculateCededUnderwritingInfos(outFilteredUnderwritingInfo, outCoverUnderwritingInfo);
                }
//            }
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

    protected void initDuringFirstIteration() {
        PeriodScope periodScope = simulationScope.getIterationScope().getPeriodScope();
        coverPerPeriod.add(parmCoverPeriod.getCoverDuration(
                periodScope.getCurrentPeriodStartDate(),
                periodScope.getNextPeriodStartDate()));
        if (!(parmContractStrategy instanceof TrivialContractStrategy)) {
            if (contract == null) {
                contract = parmContractStrategy.copy();
                coveredByReinsurer = parmCoveredByReinsurer;
            } else {
                throw new IllegalArgumentException("Only one nontrivial strategy per contract is allowed");
            }
        }
    }

    protected void initIteration() {
        contract.resetMemberInstances();
    }

    protected void filterInChannels() {
        List<LobMarker> coveredLines = parmCoveredLines.getValuesAsObjects(simulationScope.getModel());
        List<PerilMarker> coveredPerils = parmCoveredPerils.getValuesAsObjects(simulationScope.getModel());
        outFilteredClaims.addAll(ClaimFilterUtilities.filterClaimsByPerilAndLob(inClaims, coveredPerils, coveredLines));
        if (coveredLines.size() == 0) {
            coveredLines = ClaimFilterUtilities.getLineOfBusiness(outFilteredClaims);
        }
        outFilteredUnderwritingInfo.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLob(inUnderwritingInfo, coveredLines));
    }

    private void filterClaimsInCoveredPeriod(int currentPeriod, CoverDuration coverOfCurrentPeriod) {
        for (Claim claim : outFilteredClaims) {
            if (claim.getClass().equals(ClaimDevelopmentPacket.class)) {
                int originalPeriod = ((ClaimDevelopmentPacket) claim).getOriginalPeriod();
                double fractionOfPeriod = ((ClaimDevelopmentPacket) claim).getFractionOfPeriod();
                if (originalPeriod == currentPeriod && coverOfCurrentPeriod.isCovered(fractionOfPeriod)) {  // todo(sku): currentPeriod or 0?
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
                throw new NotImplementedException(claim.getClass().toString());
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

    protected void calculateUnderwritingInfos(List<UnderwritingInfo> grossUnderwritingInfos,
                                              List<UnderwritingInfo> cededUnderwritingInfos,
                                              List<UnderwritingInfo> netUnderwritingInfos) {
        for (UnderwritingInfo underwritingInfo : grossUnderwritingInfos) {
            UnderwritingInfo cededUnderwritingInfo = contract.calculateCoverUnderwritingInfo(underwritingInfo, coveredByReinsurer);
            setOriginalUnderwritingInfo(underwritingInfo, cededUnderwritingInfo);
            cededUnderwritingInfos.add(cededUnderwritingInfo);
            UnderwritingInfo netUnderwritingInfo = UnderwritingInfoUtilities.calculateNet(underwritingInfo, cededUnderwritingInfo);
            setOriginalUnderwritingInfo(underwritingInfo, netUnderwritingInfo);
            netUnderwritingInfos.add(netUnderwritingInfo);
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

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopmentNet() {
        return outClaimsDevelopmentNet;
    }

    public void setOutClaimsDevelopmentNet(PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentNet) {
        this.outClaimsDevelopmentNet = outClaimsDevelopmentNet;
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

    public ComboBoxTableMultiDimensionalParameter getParmCoveredLines() {
        return parmCoveredLines;
    }

    public void setParmCoveredLines(ComboBoxTableMultiDimensionalParameter parmCoveredLines) {
        this.parmCoveredLines = parmCoveredLines;
    }

    public ComboBoxTableMultiDimensionalParameter getParmCoveredPerils() {
        return parmCoveredPerils;
    }

    public void setParmCoveredPerils(ComboBoxTableMultiDimensionalParameter parmCoveredPerils) {
        this.parmCoveredPerils = parmCoveredPerils;
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
}