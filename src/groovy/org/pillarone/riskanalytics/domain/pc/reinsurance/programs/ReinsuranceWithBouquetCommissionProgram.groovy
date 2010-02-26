package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.aggregators.UnderwritingInfoMerger
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.DynamicCommission
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket
import org.pillarone.riskanalytics.domain.pc.reinsurance.ContractFinancials

/**
 * @author shartmann (at) munichre (dot) com
 */
class ReinsuranceWithBouquetCommissionProgram extends ComposedComponent {

    PacketList<Claim> inClaims = new PacketList(Claim)

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    PacketList<Claim> outClaimsNet = new PacketList(Claim)
    PacketList<Claim> outClaimsGross = new PacketList(Claim)
    PacketList<Claim> outClaimsCeded = new PacketList(Claim)

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanNet = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanGross = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanCeded = new PacketList(ClaimDevelopmentLeanPacket)

    PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outCoverUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials = new PacketList<ReinsuranceResultWithCommissionPacket>(ReinsuranceResultWithCommissionPacket.class);

    DynamicMultiCoverAttributeReinsuranceProgram subContracts = new DynamicMultiCoverAttributeReinsuranceProgram()
    DynamicCommission subCommissions = new DynamicCommission()

    UnderwritingInfoMerger underwritingInfoMerger = new UnderwritingInfoMerger()
    ContractFinancials financialsAggregator = new ContractFinancials()

    void wire() {
        WiringUtils.use(WireCategory) {
            subCommissions.inClaims = subContracts.outClaimsCeded
            subCommissions.inUnderwritingInfo = subContracts.outCoverUnderwritingInfo
            underwritingInfoMerger.inUnderwritingInfoGross = subContracts.outUnderwritingInfo
            underwritingInfoMerger.inUnderwritingInfoCeded = subCommissions.outUnderwritingInfo
            financialsAggregator.inClaimsCeded = subContracts.outClaimsCeded
            financialsAggregator.inUnderwritingInfoCeded = subCommissions.outUnderwritingInfo
        }
        WiringUtils.use(PortReplicatorCategory) {
            subContracts.inClaims = this.inClaims
            subContracts.inUnderwritingInfo = this.inUnderwritingInfo
            this.outClaimsNet = subContracts.outClaimsNet
            this.outClaimsGross = subContracts.outClaimsGross
            this.outClaimsCeded = subContracts.outClaimsCeded
            this.outClaimsDevelopmentLeanNet = subContracts.outClaimsDevelopmentLeanNet
            this.outClaimsDevelopmentLeanGross = subContracts.outClaimsDevelopmentLeanGross
            this.outClaimsDevelopmentLeanCeded = subContracts.outClaimsDevelopmentLeanCeded

            this.outNetAfterCoverUnderwritingInfo = underwritingInfoMerger.outUnderwritingInfoNet
            this.outUnderwritingInfo = underwritingInfoMerger.outUnderwritingInfoGross
            this.outCoverUnderwritingInfo = underwritingInfoMerger.outUnderwritingInfoCeded
            this.outContractFinancials = financialsAggregator.outContractFinancials
        }
    }
}