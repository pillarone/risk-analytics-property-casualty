package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoneClaimsGeneratorStrategy extends AbstractParameterObject implements IClaimsGeneratorStrategy {

    public IParameterObjectClassifier getType() {
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
