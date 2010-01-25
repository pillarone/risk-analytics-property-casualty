package org.pillarone.riskanalytics.domain.assets.constants;

import java.util.Map;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public enum InstallmentPeriodType {

    YEARLY, QUARTERLY, SEMI_ANNUAL;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
