package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoneLimitStrategy implements ILimitStrategy {

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}