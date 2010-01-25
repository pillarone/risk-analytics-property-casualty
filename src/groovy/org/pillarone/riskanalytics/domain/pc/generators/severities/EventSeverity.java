package org.pillarone.riskanalytics.domain.pc.generators.severities;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

public class EventSeverity extends SingleValuePacket {
    public String valueLabel = "severity";

    public Event event;

    public String getValueLabel() {
        return "severity";
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
