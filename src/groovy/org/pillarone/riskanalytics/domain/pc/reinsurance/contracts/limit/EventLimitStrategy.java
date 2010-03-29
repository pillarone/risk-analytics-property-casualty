package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventLimitStrategy implements ILimitStrategy {

    private double eventLimit = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.EVENTLIMIT;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("eventLimit", eventLimit);
        return parameters;
    }

    public double getEventLimit() { return eventLimit; }
}