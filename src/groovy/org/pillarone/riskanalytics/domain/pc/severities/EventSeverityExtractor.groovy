package org.pillarone.riskanalytics.domain.pc.severities

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverity
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker

/**
 *  This is a proof of concept component extracting from a PacketList of type
 *  DependenceStream all packets which marginal corresponds to the parmFilterCriteria.
 *
 *  Idea:   We will need a similar component setting the parmFilterCriteria equal
 *          to the name of the line of business this component belongs to.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class EventSeverityExtractor extends Component {

    ConstrainedString parmFilterCriteria = new ConstrainedString(LobMarker, "fire")

    PacketList<EventDependenceStream> inSeverities = new PacketList(EventDependenceStream)
    PacketList<EventSeverity> outSeverities = new PacketList(EventSeverity)

    public void doCalculation() {
        for (EventDependenceStream stream: inSeverities) {
            int index = stream.marginals.findIndexOf { it == parmFilterCriteria.stringValue }
            outSeverities << stream.severities[index]
        }
    }
}