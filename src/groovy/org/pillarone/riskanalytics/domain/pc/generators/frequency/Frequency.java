package org.pillarone.riskanalytics.domain.pc.generators.frequency;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

public class Frequency extends SingleValuePacket {

    public String getValueLabel() {
        return "count";
    }
}