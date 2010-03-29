package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AalAadLimitStrategy implements ILimitStrategy {

    private double aad = 0;
    private double aal = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.AALAAD;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(2);
        parameters.put("aad", aad);
        parameters.put("aal", aal);
        return parameters;
    }

    public double getAAD() { return aad; }
    public double getAAL() { return aal; }
}