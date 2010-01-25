package org.pillarone.riskanalytics.domain.pc.severities

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity

/**
 *  This is a proof of concept component extracting from a PacketList of type
 *  DependenceStream all packets which marginal corresponds to the parmFilterCriteria.
 *
 *  Idea:   We will need a similar component setting the parmFilterCriteria equal
 *          to the name of the line of business this component belongs to.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 * @deprecated use ProbabilityExtractor instead
 */
@Deprecated
class SeverityExtractor extends Component {

    String parmFilterCriteria = ''

    PacketList<DependenceStream> inSeverities = new PacketList(DependenceStream)
    PacketList<Severity> outSeverities = new PacketList(Severity)

    public void doCalculation() {
        for (DependenceStream stream: inSeverities) {
            int index = stream.marginals.findIndexOf { it == parmFilterCriteria }
            outSeverities << new Severity(stream.probabilities[index])
        }
    }
}