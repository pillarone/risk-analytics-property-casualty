package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class ClaimSizeDistributionType extends DistributionType {

    protected ClaimSizeDistributionType(String typeName, Map parameters) {
        super(typeName, parameters)
    }

    protected ClaimSizeDistributionType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        [
                BETA,
                CHISQUAREDIST,
                CONSTANT,
                CONSTANTS,
                DISCRETEEMPIRICAL,
                DISCRETEEMPIRICALCUMULATIVE,
                EXPONENTIAL,
                GAMMA,
                GPD,
                GUMBEL,
                LOGLOGISTIC,
                LOGNORMAL,
                LOGNORMAL_MU_SIGMA,
                LOGNORMALPARETO,
                LOGNORMALTYPEIIPARETO,
                NORMAL,
                PARETO,
                PIECEWISELINEAREMPIRICAL,
                PIECEWISELINEAR,
                STUDENTDIST,
                TRIANGULARDIST,
                TYPEIIPARETO,
                UNIFORM
        ]
    }
}