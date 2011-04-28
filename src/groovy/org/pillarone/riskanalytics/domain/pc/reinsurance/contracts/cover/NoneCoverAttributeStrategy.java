package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoneCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
