package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author ben (dot) ginsberg (at) intuitive-collaboration (dot) com
 */
public class OccurrenceAttritionalClaimsGeneratorStrategy extends AttritionalClaimsGeneratorStrategy implements IParameterObject, IOccurrenceClaimsGeneratorStrategy {

    RandomDistribution occurrenceDistribution = RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ['constant': 0.5d])

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.ATTRITIONAL_WITH_DATE
    }

    public Map getParameters() {
        ['claimsSizeBase': claimsSizeBase,
                'claimsSizeDistribution': claimsSizeDistribution,
                'occurrenceDistribution': occurrenceDistribution,
                'claimsSizeModification': claimsSizeModification]
    }

    public RandomDistribution getOccurrenceDistribution() {
        return occurrenceDistribution
    }
}
