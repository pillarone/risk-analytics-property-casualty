package org.pillarone.riskanalytics.domain.pc.company

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicCompany extends DynamicComposedComponent {

    PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim)
    PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim)
    PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo)

    Component createDefaultSubComponent() {
        new Company()
    }

    void wire() {
        replicateInChannels this, 'inClaimsGross'
        replicateInChannels this, 'inClaimsCeded'
        replicateInChannels this, 'inUnderwritingInfoGross'
        replicateInChannels this, 'inUnderwritingInfoCeded'
    }
}
