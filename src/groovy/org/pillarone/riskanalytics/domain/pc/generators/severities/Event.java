package org.pillarone.riskanalytics.domain.pc.generators.severities;

import org.pillarone.riskanalytics.core.packets.Packet;

public class Event extends Packet {
    private double date;

    @Override
    public String toString() {
        return new StringBuilder().append("date (fraction of period): ").append(date).toString();
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }
}