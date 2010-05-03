package org.pillarone.riskanalytics.domain.pc.constants;

import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public enum StopLossContractBase {
    ABSOLUTE, GNPI;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}