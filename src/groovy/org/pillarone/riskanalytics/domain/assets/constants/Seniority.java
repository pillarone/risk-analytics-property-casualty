package org.pillarone.riskanalytics.domain.assets.constants;

import java.util.Map;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public enum Seniority {
    SENIOR_SECURED, SENIOR_UNSECURED, SENIOR_UNSUBORDINATED, SUBORDINATED, JUNIOR_SUBORDINATED;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}