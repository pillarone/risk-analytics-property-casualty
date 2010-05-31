package org.pillarone.riskanalytics.domain.pc.generators.claims.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType

import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo: extend with additional tests
class ClaimsGeneratorStrategyValidatorTests extends GroovyTestCase {

    AbstractParameterValidationService validator = new ClaimsGeneratorStrategyValidator().validationService

    void testDefaultAttritional() {
        def defaultAttritionalClaimsModel = ClaimsGeneratorType.ATTRITIONAL
        def errors = validator.validate(defaultAttritionalClaimsModel,
            [claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0d]),
             claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:])])
        assertEquals 0, errors.size()
    }

    void testLognormalAttritional() {
        def defaultAttritionalClaimsModel = ClaimsGeneratorType.ATTRITIONAL
        def errors = validator.validate(defaultAttritionalClaimsModel,
            [claimsSizeDistribution: DistributionType.getStrategy(DistributionType.LOGNORMAL_MU_SIGMA, ['mu': 0d, 'sigma': 1d]),
             claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ['min': 1000, 'max': 1001])])
        assertEquals 1, errors.size()
    }
}

