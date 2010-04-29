package org.pillarone.riskanalytics.domain.utils

/**
 * @deprecated use DistributionType.getStrategy
 */
@Deprecated
class RandomDistributionFactory {

    @Deprecated
    static RandomDistribution getDistribution(DistributionType type, Map parameters) {
        DistributionType.getStrategy(type, parameters)
    }
}