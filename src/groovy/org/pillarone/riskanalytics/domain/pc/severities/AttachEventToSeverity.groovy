package org.pillarone.riskanalytics.domain.pc.severities

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverity
import org.pillarone.riskanalytics.domain.utils.randomnumbers.UniformDoubleList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttachEventToSeverity extends Component {

    PacketList<DependenceStream> inProbabilities = new PacketList(DependenceStream)
    PacketList<EventDependenceStream> outEventSeverities = new PacketList(EventDependenceStream)

    public void doCalculation() {
        int numberOfEvents = inProbabilities.size()
        List<Double> dates = UniformDoubleList.getDoubles(numberOfEvents, true)
        int counter = 0
        for (DependenceStream stream: inProbabilities) {
            List<EventSeverity> eventSeverities = new ArrayList<EventSeverity>(stream.probabilities.size())
            for (Number number: stream.probabilities) {
                eventSeverities << new EventSeverity(value: number, event: new Event(date: dates[counter]))
            }
            counter++
            outEventSeverities << new EventDependenceStream(severities: eventSeverities, marginals: stream.marginals)
        }
    }

}