package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverity;

import java.util.List;

/**
 *  Each severity in the list severities corresponds belongs to the marginals
 *  with the same list index.
 *
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventDependenceStream extends Packet {
    // todo (sku): convert to a map; new name for marginals: target

    public List<EventSeverity> severities;
    public List<String> marginals;

    public List<EventSeverity> getSeverities() {
        return severities;
    }

    public void setSeverities(List<EventSeverity> severities) {
        this.severities = severities;
    }

    public List<String> getMarginals() {
        return marginals;
    }

    public void setMarginals(List<String> marginals) {
        this.marginals = marginals;
    }
}