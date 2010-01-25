package org.pillarone.riskanalytics.domain.assets.constants;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum BondType {
    GOVERNMENT_BOND, CORPORATE_BOND, CONVERTIBLE_BOND;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}