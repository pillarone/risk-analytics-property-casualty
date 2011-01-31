package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class NoneCompanyCoverAttributeStrategy implements ICoverAttributeStrategy {

    public IParameterObjectClassifier getType() {
        return CompanyCoverAttributeStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
