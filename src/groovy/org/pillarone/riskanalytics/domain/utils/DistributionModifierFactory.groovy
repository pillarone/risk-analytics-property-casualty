package org.pillarone.riskanalytics.domain.utils

/**
 * @deprecated use DistributionModifier.getStrategy
 */
@Deprecated
class DistributionModifierFactory {

    @Deprecated
    static DistributionModified getModifier(DistributionModifier modifier, Map parameters) {
        DistributionModifier.getStrategy(modifier, parameters)
    }

}