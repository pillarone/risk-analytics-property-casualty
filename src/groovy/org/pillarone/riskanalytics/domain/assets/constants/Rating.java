package org.pillarone.riskanalytics.domain.assets.constants;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum Rating {
    AAA, AA, A, BBB, BB, B, CCC, CC, C, D;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}