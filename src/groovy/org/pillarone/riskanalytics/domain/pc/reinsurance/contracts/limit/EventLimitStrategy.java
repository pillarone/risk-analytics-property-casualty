package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventLimitStrategy implements ILimitStrategy {

    private double eventLimit = 0;

    public Object getType() {
        return LimitStrategyType.EVENTLIMIT;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("eventLimit", eventLimit);
        return parameters;
    }
}