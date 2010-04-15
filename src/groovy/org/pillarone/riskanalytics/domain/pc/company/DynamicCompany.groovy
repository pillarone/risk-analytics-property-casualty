package org.pillarone.riskanalytics.domain.pc.company

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicCompany extends DynamicComposedComponent {

    PacketList<Claim> inClaims = new PacketList<Claim>(Claim)
    PacketList<Claim> outClaims = new PacketList<Claim>(Claim)

    void wire() {
        replicateInChannels this, 'inClaims'
        replicateOutChannels this, 'outClaims'
    }

    Component createDefaultSubComponent() {
        new Company()
    }
}
