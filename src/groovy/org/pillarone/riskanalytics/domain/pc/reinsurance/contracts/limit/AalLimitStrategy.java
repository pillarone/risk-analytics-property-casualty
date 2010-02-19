package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AalLimitStrategy implements ILimitStrategy {

    private double aal = 0;

    public Object getType() {
        return LimitStrategyType.AAL;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("aal", aal);
        return parameters;
    }
}