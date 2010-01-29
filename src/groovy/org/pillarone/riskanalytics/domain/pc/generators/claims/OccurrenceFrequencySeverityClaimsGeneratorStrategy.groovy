package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType

/**
 * ben (dot) ginsberg (at) intuitive-collaboration (dot) com
 */
public class OccurrenceFrequencySeverityClaimsGeneratorStrategy implements IParameterObject, IOccurrenceFrequencyClaimsGeneratorStrategy {

    FrequencyBase frequencyBase = FrequencyBase.ABSOLUTE
    RandomDistribution frequencyDistribution = RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ['constant': 0d])
    DistributionModified frequencyModification = DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
    Exposure claimsSizeBase = Exposure.ABSOLUTE
    RandomDistribution claimsSizeDistribution = RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ['constant': 0d])
    RandomDistribution occurrenceDistribution = RandomDistributionFactory.getDistribution(DistributionType.UNIFORM, ['a': 0d, 'b': 1d])
    DistributionModified claimsSizeModification = DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
    FrequencySeverityClaimType produceClaim = FrequencySeverityClaimType.SINGLE

    public Object getType() {
        return ClaimsGeneratorType.FREQUENCY_SEVERITY
    }

    public Map getParameters() {
        ['frequencyBase': frequencyBase,
                'frequencyDistribution': frequencyDistribution,
                'frequencyModification': frequencyModification,
                'claimsSizeBase': claimsSizeBase,
                'claimsSizeDistribution': claimsSizeDistribution,
                'occurrenceDistribution': occurrenceDistribution,
                'claimsSizeModification': claimsSizeModification,
                'produceClaim': produceClaim]
    }

    public FrequencySeverityClaimType getProduceClaim() {
        produceClaim
    }
}
