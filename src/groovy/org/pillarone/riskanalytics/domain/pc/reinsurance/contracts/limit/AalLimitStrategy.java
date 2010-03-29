package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AalLimitStrategy implements ILimitStrategy {

    private double aal = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.AAL;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("aal", aal);
        return parameters;
    }

    public double getAAL() { return aal; }
}