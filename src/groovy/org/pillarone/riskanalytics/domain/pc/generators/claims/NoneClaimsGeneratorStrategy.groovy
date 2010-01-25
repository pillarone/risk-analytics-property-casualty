package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.DistributionModified

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoneClaimsGeneratorStrategy implements IParameterObject, IClaimsGeneratorStrategy {

    public Object getType() {
        return ClaimsGeneratorType.NONE
    }

    public Map getParameters() {
        [:]
    }

    public RandomDistribution getClaimsSizeDistribution() {
        return null
    }

    public DistributionModified getClaimsSizeModification() {
        return null
    }

    public Exposure getClaimsSizeBase() {
        return null
    }
}
