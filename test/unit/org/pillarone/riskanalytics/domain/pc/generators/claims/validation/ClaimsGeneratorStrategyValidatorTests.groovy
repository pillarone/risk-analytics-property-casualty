package org.pillarone.riskanalytics.domain.pc.generators.claims.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType

import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.RandomDistribution

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

    void testAllStrategies() {

        RandomDistribution claimsSizeDistribution = DistributionType.getStrategy(DistributionType.LOGNORMAL_MU_SIGMA, ['mu': 0d, 'sigma': 1d])
        DistributionModified largeModification = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ['min': 1000, 'max': 1001])
        DistributionModified smallModification = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ['min': -1E3, 'max': 1001])

        ClaimsGeneratorType.all.asList().each {ClaimsGeneratorType claimsGeneratorType ->
            def errors = validator.validate(claimsGeneratorType, [
                    claimsSizeDistribution: claimsSizeDistribution,
                    claimsSizeModification: largeModification ])
            assertEquals claimsGeneratorType.toString()+" validator checks truncated support", 1, errors.size()

            errors = validator.validate(claimsGeneratorType, [
                    claimsSizeDistribution: claimsSizeDistribution,
                    claimsSizeModification: smallModification ])
            assertEquals claimsGeneratorType.toString()+" validator checks truncated support", 0, errors.size()
        }
    }
}

