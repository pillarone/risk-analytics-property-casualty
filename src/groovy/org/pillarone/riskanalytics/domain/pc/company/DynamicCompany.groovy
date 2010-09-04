package org.pillarone.riskanalytics.domain.pc.company

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.creditrisk.DefaultProbabilities
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault
import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicCompany extends DynamicMultiPhaseComposedComponent {

    PacketList<DefaultProbabilities> inDefaultProbability = new PacketList<DefaultProbabilities>(DefaultProbabilities)

    PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim)
    PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim)
    PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    PacketList<Claim> inFinancialResults = new PacketList<Claim>(Claim)

    PacketList<ReinsurerDefault> outReinsurersDefault = new PacketList<ReinsurerDefault>(ReinsurerDefault)

    private static final String PHASE_DEFAULT = "Phase Default";
    private static final String PHASE_AGGREGATION = "Phase Aggregation";

    Component createDefaultSubComponent() {
        new Company()
    }

    void wire() {
        replicateInChannels this, inDefaultProbability
        replicateInChannels this, inClaimsGross
        replicateInChannels this, inClaimsCeded
        replicateInChannels this, inUnderwritingInfoGross
        replicateInChannels this, inUnderwritingInfoCeded
        replicateInChannels this, inFinancialResults
        replicateOutChannels this, outReinsurersDefault
    }

    void allocateChannelsToPhases() {
        setTransmitterPhaseInput inDefaultProbability, PHASE_DEFAULT
        setTransmitterPhaseOutput outReinsurersDefault, PHASE_DEFAULT

        setTransmitterPhaseInput inClaimsGross, PHASE_AGGREGATION
        setTransmitterPhaseInput inClaimsCeded, PHASE_AGGREGATION
        setTransmitterPhaseInput inUnderwritingInfoGross, PHASE_AGGREGATION
        setTransmitterPhaseInput inUnderwritingInfoCeded, PHASE_AGGREGATION
        setTransmitterPhaseInput inFinancialResults, PHASE_AGGREGATION
    }

    protected void doCalculation(String phase) {
        for (Component component : componentList) {
            ((MultiPhaseComponent) component).doCalculation phase
        }
    }
}
