package org.pillarone.riskanalytics.domain.pc.constants;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum LPTPremiumBase {
    ABSOLUTE, RELATIVE_TO_CEDED_RESERVES_VOLUME;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}