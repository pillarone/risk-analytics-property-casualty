package org.pillarone.riskanalytics.domain.pc.constants;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum Exposure {
    ABSOLUTE, PREMIUM_WRITTEN, NUMBER_OF_POLICIES, SUM_INSURED;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
