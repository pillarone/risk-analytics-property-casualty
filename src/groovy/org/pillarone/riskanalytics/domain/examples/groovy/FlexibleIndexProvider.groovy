package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FlexibleIndexProvider extends Component {

    PeriodScope periodScope

    IIndex parmIndex = IndexType.getStrategy(IndexType.ABSOLUTE, ['index': 1d])
    PacketList<IndexPacket> outIndex = new PacketList<IndexPacket>(IndexPacket)

    double priorIndex = 1d;

    protected void doCalculation() {
        if (periodScope.getCurrentPeriod() == 0) {
            initIteration()
        }
        if (parmIndex.getType().equals(IndexType.ABSOLUTE)) {
            outIndex << new IndexPacket(value: parmIndex.getIndex())
        }
        else if (parmIndex.getType().equals(IndexType.RELATIVEPRIORPERIOD)) {
            outIndex << new IndexPacket(value: parmIndex.getIndex() * priorIndex)
        }
        priorIndex = outIndex[0].value
    }

    private void initIteration() {
        priorIndex = 1d;
    }
}