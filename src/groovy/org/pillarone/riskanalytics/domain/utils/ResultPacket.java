package org.pillarone.riskanalytics.domain.utils;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ResultPacket extends SingleValuePacket {
    public String getValueLabel() {
        return "result";
    }
}
