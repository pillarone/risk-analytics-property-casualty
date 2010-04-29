package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistribution

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGeneratorStrategy implements IParameterObject, IClaimsGeneratorStrategy {

    Exposure claimsSizeBase = Exposure.ABSOLUTE
    RandomDistribution claimsSizeDistribution = DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0d])
    DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.ATTRITIONAL
    }

    public Map getParameters() {
        ['claimsSizeBase': claimsSizeBase,
                'claimsSizeDistribution': claimsSizeDistribution,
                'claimsSizeModification': claimsSizeModification]
    }

    public RandomDistribution getClaimsSizeDistribution() {
        return claimsSizeDistribution
    }

    public DistributionModified getClaimsSizeModification() {
        return claimsSizeModification
    }

    public Exposure getClaimsSizeBase() {
        return claimsSizeBase
    }
}
