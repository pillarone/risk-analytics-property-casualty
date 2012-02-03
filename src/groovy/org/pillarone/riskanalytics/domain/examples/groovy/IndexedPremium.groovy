package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexedPremium extends ComposedComponent {

    IndexProvider subIndexProvider = new IndexProvider()
    PremiumCalculationWithIndex subPremiumCalculation = new PremiumCalculationWithIndex()

    PacketList<PremiumPacket> outPremium = new PacketList<PremiumPacket>(PremiumPacket)

    void wire() {
        WiringUtils.use(WireCategory) {
            subPremiumCalculation.inIndex = subIndexProvider.outIndex
        }
        WiringUtils.use(PortReplicatorCategory) {
            this.outPremium = subPremiumCalculation.outPremium
        }
    }
}
