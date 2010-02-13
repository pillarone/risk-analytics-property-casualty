package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.wiring.ITransmitter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AdvancedIndexedPremium extends IndexedPremium {

    PacketList<IndexPacket> inIndex = new PacketList<IndexPacket>(IndexPacket)

    protected void doCalculation() {
        if (isReceiverWired(inIndex)) {
            // corresponds to super.super.doCalculation() which is not possible
            for (ITransmitter transmitter : allInputReplicationTransmitter) {
                transmitter.transmit()
            }
        }
        subIndexProvider.start()
    }

    void wire() {
        super.wire()
        if (isReceiverWired(inIndex)) {
            WiringUtils.use(PortReplicatorCategory) {
                subPremiumCalculation.inIndex = this.inIndex
            }
        }
    }
}