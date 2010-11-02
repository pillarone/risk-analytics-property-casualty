package org.pillarone.riskanalytics.domain.pc.generators.severities;

import org.pillarone.riskanalytics.core.packets.Packet;

public class Event extends Packet {
    private double fractionOfPeriod;

    @Override
    public String toString() {
        return new StringBuilder().append("fractionOfPeriod (fraction of period): ").append(fractionOfPeriod).toString();
    }

    public double getFractionOfPeriod() {
        return fractionOfPeriod;
    }

    public void setFractionOfPeriod(double fractionOfPeriod) {
        this.fractionOfPeriod = fractionOfPeriod;
    }
}