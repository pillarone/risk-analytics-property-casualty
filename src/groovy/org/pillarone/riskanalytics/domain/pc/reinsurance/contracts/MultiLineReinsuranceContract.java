package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByFractionOfPeriod;
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket;
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.*;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private ComboBoxTableMultiDimensionalParameter parmCoveredLines = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("Covered Lines"), LobMarker.class);
    private ComboBoxTableMultiDimensionalParameter parmCoveredPerils = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("perils"), PerilMarker.class);
    private ComboBoxTableMultiDimensionalParameter parmCoveredReserves = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("reserves"), IReserveMarker.class);

    /**
     * claims whose source is a covered line
     */
    private PacketList<Claim> outFilteredClaims = new PacketList<Claim>(Claim.class);

    private PacketList<UnderwritingInfo> outFilteredUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    public void doCalculation() {
        if (parmContractStrategy == null) {
            throw new IllegalStateException("MultiLineReinsuranceContract.missingContractStrategy");
        }

        filterInChannels();
        // initialize contract details
        parmContractStrategy.initBookkeepingFigures(outFilteredClaims, outFilteredUnderwritingInfo);

        initCoveredByReinsurer();
        Collections.sort(outFilteredClaims, SortClaimsByFractionOfPeriod.getInstance());
        if (isSenderWired(getOutUncoveredClaims()) || isSenderWired(getOutClaimsDevelopmentLeanNet())) {
            calculateClaims(outFilteredClaims, outCoveredClaims, outUncoveredClaims, this);
        }
        else {
            calculateCededClaims(outFilteredClaims, outCoveredClaims, this);
        }

        if (isSenderWired(outCoverUnderwritingInfo) || isSenderWired(outContractFinancials)) {
            calculateCededUnderwritingInfos(outFilteredUnderwritingInfo, outCoverUnderwritingInfo, outCoveredClaims);
        }

        parmCommissionStrategy.calculateCommission(outCoveredClaims, outCoverUnderwritingInfo, false, false);

        if (isSenderWired(outNetAfterCoverUnderwritingInfo)) {
            UnderwritingInfoUtilities.calculateNet(outFilteredUnderwritingInfo, outCoverUnderwritingInfo, outNetAfterCoverUnderwritingInfo);
        }


        if (inClaims.size() > 0 && inClaims.get(0) instanceof ClaimDevelopmentLeanPacket) {
            for (Claim claim : outFilteredClaims) {
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

    protected void filterInChannels() {
        List<LobMarker> coveredLines = parmCoveredLines.getValuesAsObjects();
        List<PerilMarker> coveredPerils = parmCoveredPerils.getValuesAsObjects();
        List<IReserveMarker> coveredReserves = parmCoveredReserves.getValuesAsObjects();
        outFilteredClaims.addAll(ClaimFilterUtilities.filterClaimsByPerilLobReserve(inClaims, coveredPerils, coveredLines, coveredReserves, LogicArguments.OR));
        if (coveredLines.size() == 0) {
            coveredLines = ClaimFilterUtilities.getLinesOfBusiness(outFilteredClaims);
        }
        outFilteredUnderwritingInfo.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLob(inUnderwritingInfo, coveredLines));
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

    public ComboBoxTableMultiDimensionalParameter getParmCoveredReserves() {
        return parmCoveredReserves;
    }

    public void setParmCoveredReserves(ComboBoxTableMultiDimensionalParameter parmCoveredReserves) {
        this.parmCoveredReserves = parmCoveredReserves;
    }
}