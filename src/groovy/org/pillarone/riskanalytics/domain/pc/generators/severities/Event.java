package org.pillarone.riskanalytics.domain.pc.generators.severities;

import org.pillarone.riskanalytics.core.packets.Packet;

public class Event extends Packet {
    public double date;

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }
}