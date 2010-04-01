package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimSizeDistributionType extends DistributionType {

    protected ClaimSizeDistributionType(String typeName, Map parameters) {
        super(typeName, parameters)
    }

    protected ClaimSizeDistributionType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        [
                CHISQUAREDIST,
                CONSTANT,
                CONSTANTS,
                DISCRETEEMPIRICAL,
                DISCRETEEMPIRICALCUMULATIVE,
                LOGNORMAL,
                LOGNORMAL_MU_SIGMA,
                NORMAL,
                PARETO,
                PIECEWISELINEAREMPIRICAL,
                PIECEWISELINEAR,
                STUDENTDIST,
                TRIANGULARDIST,
                UNIFORM
        ]
    }
}