package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByFractionOfPeriod;
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker;
import org.pillarone.riskanalytics.domain.pc.constants.IncludeType;
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments;
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.pc.constraints.CompanyPortion;
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault;
import org.pillarone.riskanalytics.domain.pc.filter.FilterUtils;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.*;
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.*;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.*;

/**
 * This reinsurance contract allows to specify the portions covered by each reinsurer. Furthermore it considers if
 * a specific reinsurer has gone default and reduces the cover correspondingly.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiCompanyCoverAttributeReinsuranceContract extends ReinsuranceContract {

    private SimulationScope simulationScope;
    private PeriodStore periodStore;

    private ReinsuranceContractBase parmBasedOn = ReinsuranceContractBase.NET;
    private ICoverAttributeStrategy parmCover = CompanyCoverAttributeStrategyType.getStrategy(
            CompanyCoverAttributeStrategyType.ALL, ArrayUtils.toMap(new Object[][]{{"reserves", IncludeType.NOTINCLUDED}}));

    /** required for correct allocation of ceded premium */
    private List<Claim> allInClaims = new PacketList<Claim>(Claim.class);
    /** required for correct allocation of ceded premium */
    private List<UnderwritingInfo> allInUnderwritingInfos = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<ReinsurerDefault> inReinsurersDefault = new PacketList<ReinsurerDefault>(ReinsurerDefault.class);

    private ConstrainedMultiDimensionalParameter parmReinsurers = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{"", 1d}),
            Arrays.asList(REINSURER, PORTION),
            ConstraintsFactory.getConstraints(CompanyPortion.IDENTIFIER));

    private static final String REINSURER = "Reinsurer";
    private static final String PORTION = "Covered Portion";

    /**
     * Evaluate the received default information and adjust the coveredByReinsurer factor accordingly
     * before applying the contract.
     */
    @Override
    public void doCalculation() {
        if (parmContractStrategy == null) {
            throw new IllegalStateException("MultiCompanyCoverAttributeReinsuranceContract.missingContractStrategy");
        }
        if (parmCover == null) {
            throw new IllegalStateException("MultiCompanyCoverAttributeReinsuranceContract.missingCoverStrategy");
        }

        Map<String, Double> reinsurersDefault = new HashMap<String, Double>(inReinsurersDefault.size());
        for (ReinsurerDefault reinsurerDefault : inReinsurersDefault) {
            reinsurersDefault.put(reinsurerDefault.getReinsurer(), reinsurerDefault.isDefaultOccurred() ? 0d : 1d);
        }
        double coveredByReinsurers = 0d;
        for (int row = parmReinsurers.getTitleRowCount(); row < parmReinsurers.getRowCount(); row++) {
            double portion = InputFormatConverter.getDouble(parmReinsurers.getValueAt(row, parmReinsurers.getColumnIndex(PORTION)));
            String reinsurer = InputFormatConverter.getString(parmReinsurers.getValueAt(row, parmReinsurers.getColumnIndex(REINSURER)));
            double reinsurerDefault = reinsurersDefault.get(reinsurer) == null ? 1d : reinsurersDefault.get(reinsurer);
            coveredByReinsurers += portion * reinsurerDefault;
        }
        parmContractStrategy.adjustCovered(coveredByReinsurers);

        initCoveredByReinsurer();
        // initialize contract details
        parmContractStrategy.initBookkeepingFigures(inClaims, inUnderwritingInfo);

        Collections.sort(inClaims, SortClaimsByFractionOfPeriod.getInstance());
        if (isSenderWired(getOutUncoveredClaims()) || isSenderWired(getOutClaimsDevelopmentLeanNet())) {
            calculateClaims(inClaims, outCoveredClaims, outUncoveredClaims, this);
        }
        else {
            calculateCededClaims(inClaims, outCoveredClaims, this);
        }

        if (isSenderWired(outCoverUnderwritingInfo) || isSenderWired(outContractFinancials) || isSenderWired(outNetAfterCoverUnderwritingInfo)) {
            calculateCededUnderwritingInfos(inUnderwritingInfo, outCoverUnderwritingInfo, outCoveredClaims);
        }
        parmCommissionStrategy.calculateCommission(outCoveredClaims, outCoverUnderwritingInfo, false, false);
        if (isSenderWired(outNetAfterCoverUnderwritingInfo)) {
            calculateNetUnderwritingInfos(UnderwritingFilterUtilities.filterUnderwritingInfoByLobWithoutScaling(
                    allInUnderwritingInfos, ClaimFilterUtilities.getLinesOfBusiness(inClaims)),
                    outCoverUnderwritingInfo, outNetAfterCoverUnderwritingInfo, outCoveredClaims);
        }
        if (inClaims.size() > 0 && inClaims.get(0) instanceof ClaimDevelopmentLeanPacket) {
            for (Claim claim : inClaims) {
                getOutClaimsDevelopmentLeanGross().add((ClaimDevelopmentLeanPacket) claim);
            }
        }
        if (outCoveredClaims.size() > 0 && outCoveredClaims.get(0) instanceof ClaimDevelopmentLeanPacket) {
            for (Claim claim : outUncoveredClaims) {
                getOutClaimsDevelopmentLeanNet().add((ClaimDevelopmentLeanPacket) claim);
            }
            for (Claim claim : outCoveredClaims) {
                getOutClaimsDevelopmentLeanCeded().add((ClaimDevelopmentLeanPacket) claim);
            }
        }
        if (isSenderWired(outContractFinancials)) {
            ReinsuranceResultWithCommissionPacket result = new ReinsuranceResultWithCommissionPacket();
            CededUnderwritingInfo underwritingInfo = CededUnderwritingInfoUtilities.aggregate(outCoverUnderwritingInfo);
            if (underwritingInfo != null) {
                result.setCededPremium(-underwritingInfo.getPremium());
                result.setCededCommission(-underwritingInfo.getCommission());
            }
            if (outCoveredClaims.size() > 0) {
                result.setCededClaim(ClaimUtilities.aggregateClaims(outCoveredClaims, this).getUltimate());
            }
            if (result.getCededPremium() != 0) {
                result.setCededLossRatio(result.getCededClaim() / -result.getCededPremium());
            }
            outContractFinancials.add(result);
        }

        for (UnderwritingInfo outCoverUnderwritingInfoPacket : outCoverUnderwritingInfo) {
            outCoverUnderwritingInfoPacket.setReinsuranceContract(this);
        }

        parmContractStrategy.resetCovered();
    }

    protected void filterInChannels(PacketList inChannel, PacketList source) {
        if (inChannel == inClaims) {
            allInClaims.addAll(source);
        }
        if (inChannel == inUnderwritingInfo) {
            allInUnderwritingInfos.addAll(source);
        }
        if (parmCover instanceof NoneCompanyCoverAttributeStrategy) {
            // leave outFiltered* lists void
        }
        else if (parmCover instanceof AllCompanyCoverAttributeStrategy) {
            super.filterInChannel(inChannel, source);
        }
        else if (parmCover instanceof CompaniesCompanyCoverAttributeStrategy && inChannel == inClaims) {
            List<ICompanyMarker> coveredCompanies = (List<ICompanyMarker>) (((CompaniesCompanyCoverAttributeStrategy) parmCover).getCompanies().getValuesAsObjects());
            inClaims.addAll(ClaimFilterUtilities.filterClaimsByCompanies(source, coveredCompanies, false));
        }
        else if (parmCover instanceof CompaniesCompanyCoverAttributeStrategy && inChannel == inUnderwritingInfo) {
            List<LobMarker> coveredLines = ClaimFilterUtilities.getLinesOfBusiness(inClaims);
            inUnderwritingInfo.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLobWithoutScaling(source, coveredLines));
        }
        else if (inChannel == inClaims) {
            List<LobMarker> coveredLines = FilterUtils.getCoveredLines(parmCover, periodStore);
            List<PerilMarker> coveredPerils = FilterUtils.getCoveredPerils(parmCover, periodStore);
            List<IReserveMarker> coveredReserves = FilterUtils.getCoveredReserves(parmCover, periodStore);
            LogicArguments connection = parmCover instanceof ICombinedCoverAttributeStrategy
                    ? ((ICombinedCoverAttributeStrategy) parmCover).getConnection() : null;
            inClaims.addAll(ClaimFilterUtilities.filterClaimsByPerilLobReserve(source, coveredPerils, coveredLines, coveredReserves, connection));
        }
        else if (inChannel == inUnderwritingInfo) {
            // extend coveredLines such that they additionally consist of the segments which are associated with the selected perils
            List<LobMarker> coveredLines = ClaimFilterUtilities.getLinesOfBusiness(inClaims);
            List<PerilMarker> coveredPerils = FilterUtils.getCoveredPerils(parmCover, periodStore);
            inUnderwritingInfo.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLobAndScaleByPerilsInLob(
                    source, coveredLines, allInClaims, coveredPerils));
        }
        else {
            super.filterInChannel(inChannel, source);
        }
    }

    public ConstrainedMultiDimensionalParameter getParmReinsurers() {
        return parmReinsurers;
    }

    public void setParmReinsurers(ConstrainedMultiDimensionalParameter parmReinsurers) {
        this.parmReinsurers = parmReinsurers;
    }

    public PacketList<ReinsurerDefault> getInReinsurersDefault() {
        return inReinsurersDefault;
    }

    public void setInReinsurersDefault(PacketList<ReinsurerDefault> inReinsurersDefault) {
        this.inReinsurersDefault = inReinsurersDefault;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public ReinsuranceContractBase getParmBasedOn() {
        return parmBasedOn;
    }

    public void setParmBasedOn(ReinsuranceContractBase parmBasedOn) {
        this.parmBasedOn = parmBasedOn;
    }

    public ICoverAttributeStrategy getParmCover() {
        return parmCover;
    }

    public void setParmCover(ICoverAttributeStrategy parmCover) {
        this.parmCover = parmCover;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }
}
