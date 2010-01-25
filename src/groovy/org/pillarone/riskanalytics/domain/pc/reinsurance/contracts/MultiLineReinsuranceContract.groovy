package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByDate
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingFilterUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 *  This component filters from the incoming claims and underwriting information
 *  the packets which line is listed in parameter parmCoveredLines and provides
 *  them in the corresponding out Packetlists.
 *  If the parameter contains no line at all, all packets are sent as-is to the
 *  next component. Packets are not modified.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class MultiLineReinsuranceContract extends ReinsuranceContract {

    SimulationScope simulationScope

    ComboBoxTableMultiDimensionalParameter parmCoveredLines = new ComboBoxTableMultiDimensionalParameter([''], ['Covered Lines'], LobMarker)
    ComboBoxTableMultiDimensionalParameter parmCoveredPerils = new ComboBoxTableMultiDimensionalParameter([''], ['perils'], PerilMarker)

    /** claims which source is a covered line         */
    PacketList<Claim> outFilteredClaims = new PacketList(Claim)
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanNet = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanGross = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanCeded = new PacketList(ClaimDevelopmentLeanPacket)


    PacketList<UnderwritingInfo> outFilteredUnderwritingInfo = new PacketList(UnderwritingInfo)

    public void doCalculation() {
        if (!parmContractStrategy) {
            throw new IllegalStateException("A contract strategy must be set")
        }
        filterInChannels()
        // initialize contract details
        parmContractStrategy.initBookKeepingFigures(outFilteredClaims, outFilteredUnderwritingInfo)

        Collections.sort(outFilteredClaims, SortClaimsByDate.getInstance())
        if (isSenderWired(outUncoveredClaims) || isSenderWired(outClaimsDevelopmentLeanNet)) {
            calculateClaims(outFilteredClaims, outCoveredClaims, outUncoveredClaims, this)
        }
        else {
            calculateCededClaims(outFilteredClaims, outCoveredClaims, this)
        }

        if (isSenderWired(outNetAfterCoverUnderwritingInfo)) {
            calculateUnderwritingInfos(outFilteredUnderwritingInfo, outCoverUnderwritingInfo, outNetAfterCoverUnderwritingInfo)
        }
        else if (isSenderWired(outCoverUnderwritingInfo)) {
            calculateCededUnderwritingInfos(outFilteredUnderwritingInfo, outCoverUnderwritingInfo)
        }
        if (inClaims.size() > 0 && inClaims.get(0) instanceof ClaimDevelopmentLeanPacket) {
            for (Claim claim : outFilteredClaims) {
                outClaimsDevelopmentLeanGross.add((ClaimDevelopmentLeanPacket) claim)
            }
        }
        if (outCoveredClaims.size() > 0 && outCoveredClaims.get(0) instanceof ClaimDevelopmentLeanPacket) {
            for (Claim claim : outUncoveredClaims) {
                outClaimsDevelopmentLeanNet.add((ClaimDevelopmentLeanPacket) claim)
            }
            for (Claim claim : outCoveredClaims) {
                outClaimsDevelopmentLeanCeded.add((ClaimDevelopmentLeanPacket) claim)
            }
        }
    }

    protected void filterInChannels() {
        List<LobMarker> coveredLines = parmCoveredLines.getValuesAsObjects(simulationScope.model)
        List<PerilMarker> coveredPerils = parmCoveredPerils.getValuesAsObjects(simulationScope.model)
        outFilteredClaims.addAll(ClaimFilterUtilities.filterClaimsByPerilAndLob(inClaims, coveredPerils, coveredLines))
        if (coveredLines.size() == 0) {
            coveredLines = ClaimFilterUtilities.getLineOfBusiness(outFilteredClaims)
        }
        outFilteredUnderwritingInfo.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLob(inUnderwritingInfo, coveredLines))
    }
}