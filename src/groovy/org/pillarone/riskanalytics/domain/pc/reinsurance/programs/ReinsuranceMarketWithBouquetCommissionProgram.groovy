package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.aggregators.UnderwritingInfoMerger
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.reinsurance.ContractFinancials
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.DynamicCommission
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.wiring.ITransmitter
import org.pillarone.riskanalytics.core.wiring.Transmitter
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceMarketWithBouquetCommissionProgram extends ComposedComponent {

    PacketList<ReinsurerDefault> inReinsurersDefault = new PacketList<ReinsurerDefault>(ReinsurerDefault)

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
    PacketList<CededUnderwritingInfo> outCoverUnderwritingInfo = new PacketList(CededUnderwritingInfo)
    PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials = new PacketList<ReinsuranceResultWithCommissionPacket>(ReinsuranceResultWithCommissionPacket.class);

    ReinsuranceMarket subContracts = new ReinsuranceMarket()
    DynamicCommission subCommissions = new DynamicCommission()

    UnderwritingInfoMerger underwritingInfoMerger = new UnderwritingInfoMerger()
    ContractFinancials financialsAggregator = new ContractFinancials()

    void wire() {
        if (subContracts.subComponentCount() == 0 && subCommissions.subComponentCount() == 0) {
            wireByPass()
        }
        else {
            subCommissions.subComponentCount() == 0 ? wireNoCommissions() : wireCommissions()
            WiringUtils.use(WireCategory) {
                financialsAggregator.inClaimsCeded = subContracts.outClaimsCeded
            }
            WiringUtils.use(PortReplicatorCategory) {
                subContracts.inReinsurersDefault = this.inReinsurersDefault
                subContracts.inClaims = this.inClaims
                subContracts.inUnderwritingInfo = this.inUnderwritingInfo
                this.outClaimsNet = subContracts.outClaimsNet
                this.outClaimsGross = subContracts.outClaimsGross
                this.outClaimsCeded = subContracts.outClaimsCeded
                this.outClaimsDevelopmentLeanNet = subContracts.outClaimsDevelopmentLeanNet
                this.outClaimsDevelopmentLeanGross = subContracts.outClaimsDevelopmentLeanGross
                this.outClaimsDevelopmentLeanCeded = subContracts.outClaimsDevelopmentLeanCeded
                this.outContractFinancials = financialsAggregator.outContractFinancials
            }
        }
    }

    private void wireByPass() {
        Map<Integer, ITransmitter> replaceTransmitters = new LinkedHashMap<Integer, ITransmitter>();
        for (int i = 0; i < allOutputTransmitter.size(); i++) {
            Transmitter transmitter = allOutputTransmitter.get(i);
            // checking equality on list instances won't work, as all lists with size 0 have 0 as hashCode
            if (transmitter.getSource().is(outClaimsGross)) {
                ITransmitter transmitterWithModifiedSource = new Transmitter(transmitter.getSender(), inClaims, transmitter.getReceiver(), transmitter.getTarget())
                replaceTransmitters.put(i, transmitterWithModifiedSource)
            }
            // checking equality on list instances won't work, as all lists with size 0 have 0 as hashCode
            else if (transmitter.getSource().is(outUnderwritingInfo)) {
                ITransmitter transmitterWithModifiedSource = new Transmitter(transmitter.getSender(), inUnderwritingInfo, transmitter.getReceiver(), transmitter.getTarget())
                replaceTransmitters.put(i, transmitterWithModifiedSource)
            }
            // checking equality on list instances won't work, as all lists with size 0 have 0 as hashCode
            else if (transmitter.getSource().is(inReinsurersDefault)) {
                allInputTransmitter.remove(transmitter)
            }
            // checking equality on list instances won't work, as all lists with size 0 have 0 as hashCode
            else if (transmitter.getSource().is(outContractFinancials)) {
                allOutputTransmitter.remove(transmitter)
            }
        }
        for (Map.Entry<Integer, ITransmitter> entry : replaceTransmitters) {
            allOutputTransmitter.set(entry.key, entry.value);
        }
        WiringUtils.use(WireCategory) {
            this.outClaimsGross = this.inClaims
            this.outUnderwritingInfo = this.inUnderwritingInfo
        }
    }

    private void wireNoCommissions() {
        WiringUtils.use(WireCategory) {
            financialsAggregator.inUnderwritingInfoCeded = subContracts.outCoverUnderwritingInfo
        }
        WiringUtils.use(PortReplicatorCategory) {
            this.outNetAfterCoverUnderwritingInfo = subContracts.outNetAfterCoverUnderwritingInfo
            this.outUnderwritingInfo = subContracts.outUnderwritingInfo
            this.outCoverUnderwritingInfo = subContracts.outCoverUnderwritingInfo
        }
    }

    private void wireCommissions() {
        WiringUtils.use(WireCategory) {
            subCommissions.inClaims = subContracts.outClaimsCeded
            subCommissions.inUnderwritingInfo = subContracts.outCoverUnderwritingInfo
            underwritingInfoMerger.inUnderwritingInfoCeded = subCommissions.outUnderwritingInfoModified
            underwritingInfoMerger.inUnderwritingInfoCeded = subCommissions.outUnderwritingInfoUnmodified
            financialsAggregator.inUnderwritingInfoCeded = underwritingInfoMerger.outUnderwritingInfoCeded
        }
        WiringUtils.use(PortReplicatorCategory) {
            underwritingInfoMerger.inUnderwritingInfoGross = this.inUnderwritingInfo
            this.outNetAfterCoverUnderwritingInfo = underwritingInfoMerger.outUnderwritingInfoNet
            this.outUnderwritingInfo = underwritingInfoMerger.outUnderwritingInfoGross
            this.outCoverUnderwritingInfo = underwritingInfoMerger.outUnderwritingInfoCeded
        }
    }
}