package org.pillarone.riskanalytics.domain.pc.severities

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity

/**
 *  This component extracts from a PacketList of type DependenceStream all packets
 *  which marginal corresponds to the line whitin the component is placed.
 *  If no corresponding packet is found, outProbabilities will remain void.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ProbabilityExtractor extends Component {

    SimulationScope simulationScope

    PacketList<DependenceStream> inProbabilities = new PacketList(DependenceStream)
    PacketList<Severity> outProbabilities = new PacketList(Severity)

    public void doCalculation() {
        String filterCriteria = simulationScope.getStructureInformation().getLine(this)
        for (DependenceStream stream: inProbabilities) {
            int index = stream.marginals.findIndexOf { it == filterCriteria }
            if (index > -1) {
                outProbabilities << new Severity(stream.probabilities[index])
            }
        }
    }
}