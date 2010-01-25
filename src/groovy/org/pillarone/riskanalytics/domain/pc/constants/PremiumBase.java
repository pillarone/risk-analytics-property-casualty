package org.pillarone.riskanalytics.domain.pc.constants;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum PremiumBase {
    ABSOLUTE, GNPI, RATE_ON_LINE, NUMBER_OF_POLICIES;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}

