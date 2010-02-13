package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexProvider extends Component {

    double parmIndex = 0d
    PacketList<IndexPacket> outIndex = new PacketList<IndexPacket>(IndexPacket)

    protected void doCalculation() {
        outIndex << new IndexPacket(value: parmIndex)
    }
}
