package org.pillarone.riskanalytics.domain.pc.constants;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ClaimType {
    ATTRITIONAL, SINGLE, EVENT, AGGREGATED_ATTRITIONAL, AGGREGATED_SINGLE, AGGREGATED_EVENT, AGGREGATED_RESERVES, AGGREGATED;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
