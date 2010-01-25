package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RandomVariateDistribution implements IRandomVariateDistribution, IParameterObject {

    private RandomDistribution distribution
    DistributionType type
    Map parameters

    public double inverseDistributionFunction(double u) {
        assert distribution != null
        distribution.distribution.inverseF(u)
    }

    public double cdf(double x) {
        assert distribution != null
        distribution.distribution.cdf(x)
    }

    public DistributionType getType() {
        type
    }

}