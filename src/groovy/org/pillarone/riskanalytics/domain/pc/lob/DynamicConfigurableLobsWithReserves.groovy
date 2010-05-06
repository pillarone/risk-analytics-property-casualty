package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.MultiPhaseDynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class DynamicConfigurableLobsWithReserves extends MultiPhaseDynamicComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList(UnderwritingInfo)
    PacketList<Claim> inClaimsGross = new PacketList(Claim)
    PacketList<Claim> inClaimsCeded = new PacketList(Claim)

    PacketList<Claim> outClaimsNet = new PacketList(Claim)
    PacketList<Claim> outClaimsGross = new PacketList(Claim)
    PacketList<Claim> outClaimsCeded = new PacketList(Claim)

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanNet = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanGross = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanCeded = new PacketList(ClaimDevelopmentLeanPacket)

    PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList(UnderwritingInfo)

    public void wire() {
        if (componentList.size() == 0) {
            throw new IllegalArgumentException("At least one line of business is required for a valid parameterization!")
        }
        replicateInChannels this, 'inUnderwritingInfoGross'
        replicateInChannels this, 'inUnderwritingInfoCeded'
        replicateInChannels this, 'inClaimsGross'
        replicateInChannels this, 'inClaimsCeded'
        replicateOutChannels this, 'outUnderwritingInfoGross'
        replicateOutChannels this, 'outUnderwritingInfoCeded'
        replicateOutChannels this, 'outUnderwritingInfoNet'
        replicateOutChannels this, 'outClaimsGross'
        replicateOutChannels this, 'outClaimsCeded'
        replicateOutChannels this, 'outClaimsNet'
        replicateOutChannels this, 'outClaimsDevelopmentLeanGross'
        replicateOutChannels this, 'outClaimsDevelopmentLeanCeded'
        replicateOutChannels this, 'outClaimsDevelopmentLeanNet'
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
        setTransmitterPhaseInput(inUnderwritingInfoCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseInput(inClaimsCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outUnderwritingInfoGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseOutput(outClaimsGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseOutput(outClaimsDevelopmentLeanGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outUnderwritingInfoNet, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outClaimsCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outClaimsDevelopmentLeanCeded, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outClaimsNet, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
        setTransmitterPhaseOutput(outClaimsDevelopmentLeanNet, MultiPhaseDynamicComposedComponent.PHASE_DO_CALCULATION)
    }
}