package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class FrequencyDistributionType extends DistributionType {

    protected FrequencyDistributionType(String typeName, Map parameters) {
        super(typeName, parameters)
    }

    protected FrequencyDistributionType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        [
                CONSTANT,
                DISCRETEEMPIRICAL,
                DISCRETEEMPIRICALCUMULATIVE,
                NEGATIVEBINOMIAL,
                POISSON,
                EXPONENTIAL
        ]
    }
}