package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGeneratorStrategy implements IParameterObject, IClaimsGeneratorStrategy {

    Exposure claimsSizeBase = Exposure.ABSOLUTE
    RandomDistribution claimsSizeDistribution = RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ['constant': 0d])
    DistributionModified claimsSizeModification = DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])

    public Object getType() {
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
