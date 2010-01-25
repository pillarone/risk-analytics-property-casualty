package org.pillarone.riskanalytics.domain.pc.creditrisk;

import org.pillarone.riskanalytics.domain.pc.constants.Reinsurer;
import org.pillarone.riskanalytics.core.packets.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class ReinsurersDefault extends Packet {

    public Map<Reinsurer, Integer> defaultOccured = new HashMap<Reinsurer, Integer>();

    public ReinsurersDefault() {
    }

    public Map<Reinsurer, Integer> getDefaultOccured() {
        return defaultOccured;
    }

    public void setDefaultOccured(Map<Reinsurer, Integer> defaultOccured) {
        this.defaultOccured = defaultOccured;
    }
}
