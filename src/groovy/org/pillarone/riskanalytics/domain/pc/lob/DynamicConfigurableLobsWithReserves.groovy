package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.MultiPhaseDynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['SEGMENT','RESERVES'])
public class DynamicConfigurableLobsWithReserves extends MultiPhaseDynamicComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList(UnderwritingInfo)
    PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded = new PacketList(CededUnderwritingInfo)
    PacketList<Claim> inClaimsGross = new PacketList(Claim)
    PacketList<Claim> inClaimsCeded = new PacketList(Claim)
    PacketList<SingleValuePacket> inInitialReserves = new PacketList<SingleValuePacket>(SingleValuePacket)

    PacketList<Claim> outClaimsNet = new PacketList(Claim)
    PacketList<Claim> outClaimsGross = new PacketList(Claim)
    PacketList<Claim> outClaimsCeded = new PacketList(Claim)

    PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList(UnderwritingInfo)
    PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded = new PacketList(CededUnderwritingInfo)

    public void wire() {
        replicateInChannels this, inUnderwritingInfoGross
        replicateInChannels this, inUnderwritingInfoCeded
        replicateInChannels this, inClaimsGross
        replicateInChannels this, inClaimsCeded
        replicateInChannels this, inInitialReserves
        replicateOutChannels this, outUnderwritingInfoGross
        replicateOutChannels this, outUnderwritingInfoCeded
        replicateOutChannels this, outUnderwritingInfoNet
        replicateOutChannels this, outClaimsGross
        replicateOutChannels this, outClaimsCeded
        replicateOutChannels this, outClaimsNet
    }

    public ConfigurableLobWithReserves createDefaultSubComponent() {
        new ConfigurableLobWithReserves()
    }

    public String getGenericSubComponentName() {
        'lineOfBusiness'
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inUnderwritingInfoGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseInput(inClaimsGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseInput(inInitialReserves, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseInput(inUnderwritingInfoCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseInput(inClaimsCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outUnderwritingInfoGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseOutput(outClaimsGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outUnderwritingInfoNet, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outClaimsCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outClaimsNet, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
    }
}