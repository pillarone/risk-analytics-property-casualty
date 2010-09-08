package org.pillarone.riskanalytics.domain.pc.generators.severities;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

public class Severity extends SingleValuePacket {

    public Severity() {
        super();
    }

    public Severity(double value) {
        super();
        setValue(value);
    }

    public String getValueLabel() {
        return "severity";
    }
}