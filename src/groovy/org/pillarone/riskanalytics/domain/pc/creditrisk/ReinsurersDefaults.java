package org.pillarone.riskanalytics.packets.pc;

import org.pillarone.riskanalytics.core.packets.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsurersDefaults extends Packet {

    public Map<String, Integer> defaultOccured = new HashMap<String, Integer>();

    public ReinsurersDefaults() {
    }

    public Map<String, Integer> getDefaultOccured() {
        return defaultOccured;
    }

    public void setDefaultOccured(Map<String, Integer> defaultOccured) {
        this.defaultOccured = defaultOccured;
    }
}