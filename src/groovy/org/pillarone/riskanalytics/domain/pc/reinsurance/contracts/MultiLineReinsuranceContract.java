package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByFractionOfPeriod;
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments;
import org.pillarone.riskanalytics.domain.pc.filter.FilterUtils;
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.*;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.Arrays;
import java.util.Collections;

/**
 * This component filters from the incoming claims and underwriting information
 * the packets whose line is listed in parameter parmCoveredLines and provides
 * them in the corresponding out Packetlists.
 * If the parameter contains no line at all, all packets are sent as is to the
 * next component. Packets are not modified.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 * @deprecated use MultiCoverAttributeReinsuranceContract instead
 */
@Deprecated
public class MultiLineReinsuranceContract extends ReinsuranceContract {

    private SimulationScope simulationScope;
    private PeriodStore periodStore;

    private ComboBoxTableMultiDimensionalParameter parmCoveredLines = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("Covered Segments"), ISegmentMarker.class);
    private ComboBoxTableMultiDimensionalParameter parmCoveredPerils = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("perils"), IPerilMarker.class);
    private ComboBoxTableMultiDimensionalParameter parmCoveredReserves = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("reserves"), IReserveMarker.class);

    public void doCalculation() {
        if (parmContractStrategy == null) {
            throw new IllegalStateException("MultiLineReinsuranceContract.missingContractStrategy");
        }

        // initialize contract details
        parmContractStrategy.initBookkeepingFigures(inClaims, inUnderwritingInfo);

        initCoveredByReinsurer();
        Collections.sort(inClaims, SortClaimsByFractionOfPeriod.getInstance());
        if (isSenderWired(getOutUncoveredClaims()) || isSenderWired(getOutClaimsDevelopmentLeanNet())) {
            calculateClaims(inClaims, outCoveredClaims, outUncoveredClaims, this);
        }
        else {
            calculateCededClaims(inClaims, outCoveredClaims, this);
        }

        if (isSenderWired(outCoverUnderwritingInfo) || isSenderWired(outContractFinancials)) {
            calculateCededUnderwritingInfos(inUnderwritingInfo, outCoverUnderwritingInfo, outCoveredClaims);
        }
        boolean isFirstPeriod = simulationScope.getIterationScope().getPeriodScope().isFirstPeriod();
        parmCommissionStrategy.calculateCommission(outCoveredClaims, outCoverUnderwritingInfo, isFirstPeriod, false);

        if (isSenderWired(outNetAfterCoverUnderwritingInfo)) {
            UnderwritingInfoUtilities.calculateNet(inUnderwritingInfo, outCoverUnderwritingInfo, outNetAfterCoverUnderwritingInfo);
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
                result.setCededCommission(underwritingInfo.getCommission());
            }
            Claim aggregateClaim = ClaimUtilities.aggregateClaims(outCoveredClaims, this);
            if (aggregateClaim != null) {
                result.setCededClaim(aggregateClaim.getUltimate());
            }
            outContractFinancials.add(result);
        }
    }

    // todo(sku): critical as it works fine only if inClaims is filtered first! -> COVERED_LINES_DERIVED
    public void filterInChannel(PacketList inChannel, PacketList source) {
        if (inChannel == inClaims) {
            inChannel.addAll(ClaimFilterUtilities.filterClaimsByPerilLobReserve(source,
                    FilterUtils.getCoveredPerils(parmCoveredPerils, periodStore),
                    FilterUtils.getCoveredLines(parmCoveredLines, periodStore),
                    FilterUtils.getCoveredReserves(parmCoveredReserves, periodStore), LogicArguments.OR));
        }
        else if (inChannel == inUnderwritingInfo) {
            inChannel.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLob(source,
                    FilterUtils.getCoveredLines(parmCoveredPerils, inChannel, periodStore)));
        }
        else {
            super.filterInChannel(inChannel, source);
        }
    }


    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
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

    public PacketList<Claim> getinClaims() {
        return inClaims;
    }

    public void setinClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<UnderwritingInfo> getinUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setinUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public ComboBoxTableMultiDimensionalParameter getParmCoveredReserves() {
        return parmCoveredReserves;
    }

    public void setParmCoveredReserves(ComboBoxTableMultiDimensionalParameter parmCoveredReserves) {
        this.parmCoveredReserves = parmCoveredReserves;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }
}