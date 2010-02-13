package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicPremiumCalculation extends DynamicComposedComponent {

    PacketList<IndexPacket> inIndex = new PacketList<IndexPacket>(IndexPacket)
    PacketList<PremiumPacket> outPremium = new PacketList<PremiumPacket>(PremiumPacket)

    void wire() {
        replicateInChannels this, 'inIndex'
        replicateOutChannels this, 'outPremium'
    }

    Component createDefaultSubComponent() {
        return new PremiumCalculation()
    }
}
