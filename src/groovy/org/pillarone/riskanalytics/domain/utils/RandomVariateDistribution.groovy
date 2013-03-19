package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class RandomVariateDistribution extends AbstractParameterObject implements IRandomVariateDistribution {

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
