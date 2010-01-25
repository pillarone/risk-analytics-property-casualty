package org.pillarone.riskanalytics.domain.utils


class DistributionModifierFactory {

    static DistributionModified getModifier(DistributionModifier modifier, Map parameters) {
        DistributionModified distributionModified
        switch (modifier) {
            case DistributionModifier.NONE:
                distributionModified = new DistributionModified(type: DistributionModifier.NONE, parameters: [:])
                break
            case DistributionModifier.CENSORED:
                distributionModified = new DistributionModified(type: DistributionModifier.CENSORED, parameters: parameters)
                break
            case DistributionModifier.CENSOREDSHIFT:
                distributionModified = new DistributionModified(type: DistributionModifier.CENSOREDSHIFT, parameters: parameters)
                break
            case DistributionModifier.TRUNCATED:
                distributionModified = new DistributionModified(type: DistributionModifier.TRUNCATED, parameters: parameters)
                break
            case DistributionModifier.TRUNCATEDSHIFT:
                distributionModified = new DistributionModified(type: DistributionModifier.TRUNCATEDSHIFT, parameters: parameters)
                break
            case DistributionModifier.SHIFT:
                distributionModified = new DistributionModified(type: DistributionModifier.SHIFT, parameters: parameters)
                break
        }
        return distributionModified
    }
}