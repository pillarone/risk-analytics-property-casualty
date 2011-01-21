package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.MultiPhaseDynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class DynamicConfigurableLobs extends MultiPhaseDynamicComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList(UnderwritingInfo.class);
    PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded = new PacketList(CededUnderwritingInfo.class);
    PacketList<Claim> inClaimsGross = new PacketList(Claim.class);
    PacketList<Claim> inClaimsCeded = new PacketList(Claim.class);

    PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList(UnderwritingInfo.class);
    PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList(UnderwritingInfo.class);
    PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded = new PacketList(CededUnderwritingInfo.class);
    PacketList<Claim> outClaimsNet = new PacketList(Claim.class);
    PacketList<Claim> outClaimsGross = new PacketList(Claim.class);
    PacketList<Claim> outClaimsCeded = new PacketList(Claim.class);

    public void wire() {
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
    }

    public ConfigurableLob createDefaultSubComponent() {
        ConfigurableLob lob = new ConfigurableLob()
        return lob;
    }

    public String getGenericSubComponentName() {
        return "lineOfBusiness"
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inUnderwritingInfoGross, MultiPhaseDynamicComposedComponent.PHASE_START)
        setTransmitterPhaseInput(inClaimsGross, MultiPhaseDynamicComposedComponent.PHASE_START)
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