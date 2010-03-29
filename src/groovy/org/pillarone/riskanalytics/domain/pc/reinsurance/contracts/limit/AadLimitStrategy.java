package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AadLimitStrategy implements ILimitStrategy {

    private double aad = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.AAD;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("aad", aad);
        return parameters;
    }

    public double getAAD() { return aad; }
}