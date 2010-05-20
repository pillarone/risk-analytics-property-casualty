package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.validation.DistributionTypeValidator
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService

/**
 * @author: dierk.koenig at canoo.com
 */
class DistributionTypeTests extends GroovyTestCase {

    AbstractParameterValidationService validator = new DistributionTypeValidator().validationService

    void testDefaultUniformValidator() {
        def defaultUniform = DistributionType.UNIFORM
        assertEquals 0, validator.validate(defaultUniform, ["a": 0d, "b": 1d]).size()
    }

    void testPoissonValidator() {
        def validPoisson = DistributionType.POISSON
        assertEquals 0, validator.validate(validPoisson, ["lambda": 0d]).size()
    }

    void testFailingPoissonValidator() {
        def badPoisson = DistributionType.POISSON
        def result = validator.validate(badPoisson, ['lambda': -1d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        //assertEquals -1d, result[0].args[0]
    }

    void testNegativeBinomialValidator() {
        def validNegBinomial = DistributionType.NEGATIVEBINOMIAL
        assertEquals 0, validator.validate(validNegBinomial, ['gamma': 1, 'p': 0]).size()
    }

    void testFailingNegativeBinomialValidator() {
        def badNegBinomial = DistributionType.NEGATIVEBINOMIAL
        def result = validator.validate(badNegBinomial, ['gamma': 0, 'p': 2])
        assertNotNull result
        assertEquals 'two error messages', 2, result.size()
        assert result[0].msg instanceof String
        assert result[1].msg instanceof String
        assertEquals 0d, result[0].args[0]
        assertEquals 2d, result[1].args[0]
    }

    void testNormalValidator() {
        def validDistribution = DistributionType.NORMAL
        assertEquals 0, validator.validate(validDistribution, ['mean': 0d, 'stDev': 1d]).size()
    }

    void testFailingNormalValidator() {
        def badPoisson = DistributionType.NORMAL
        def result = validator.validate(badPoisson, ['mean': -1d, 'stDev': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testLogNormalValidator() {
        def validDistribution = DistributionType.LOGNORMAL
        assertEquals 0, validator.validate(validDistribution, ['mean': 0d, 'stDev': 1d]).size()
    }

    void testFailingLogNormalValidator() {
        def badPoisson = DistributionType.LOGNORMAL
        def result = validator.validate(badPoisson, ['mean': -1d, 'stDev': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testLogNormalMuSigmaValidator() {
        def validDistribution = DistributionType.LOGNORMAL_MU_SIGMA
        assertEquals 0, validator.validate(validDistribution, ['mu': 0d, 'sigma': 1d]).size()
    }

    void testFailingLogNormalMuSigmaValidator() {
        def badPoisson = DistributionType.LOGNORMAL_MU_SIGMA
        def result = validator.validate(badPoisson, ['mu': -1d, 'sigma': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testParetoValidator() {
        def validDistribution = DistributionType.PARETO
        assertEquals 0, validator.validate(validDistribution, ['alpha': 1d, 'beta': 1d]).size()
    }

    // todo(sku): extend with additional failing msg

    void testFailingParetoValidator() {
        def badDistribution = DistributionType.PARETO
        def result = validator.validate(badDistribution, ['alpha': -1d, 'beta': 1d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        //assertEquals -1, result[0].args[0]
    }

    void testFailingUniformValidator() {
        def badUniform = DistributionType.UNIFORM
        def result = validator.validate(badUniform, ["a": 1d, "b": 0d])
        assertNotNull result
        assertEquals 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 1d, result[0].args[0]
        assertEquals 0d, result[0].args[1]
    }

    void testPieceWiseLinearValidator() {
        def defaultPieceWiseLinear = DistributionType.PIECEWISELINEAR
        assertEquals 0, validator.validate(defaultPieceWiseLinear, [supportPoints: new TableMultiDimensionalParameter(
                [[0.0, 1.0], [0.0, 1.0]], ["values", "cummulative probabilities"])]).size()
    }

    void testFailingPieceWiseLinearValidator() {
        def pieceWiseLinearValuesNotIncr = DistributionType.PIECEWISELINEAR
        def result = validator.validate(pieceWiseLinearValuesNotIncr, [supportPoints: new TableMultiDimensionalParameter(
                [[2.0, 1.0], [0.0, 1.0]], ["values", "cummulative probabilities"])])
        assertNotNull result
        assertEquals 1, result.size()
        assert result[0].msg instanceof String
        assertEquals "value 1 smaller than 2, index: ", 1, result[0].args[0]
        assertEquals "value 1 smaller than 2, value[i-1]: ", 2, result[0].args[1]
        assertEquals "value 1 smaller than 2, value[i]: ", 1, result[0].args[2]
    }

    void testTriangularValidator() {
        def validDistribution = DistributionType.TRIANGULARDIST
        assertEquals 0, validator.validate(validDistribution, ['a': 1d, 'b': 1d, 'm': 1d]).size()
    }

    void testFailingTriangularValidator() {
        def badDistribution = DistributionType.TRIANGULARDIST
        def result = validator.validate(badDistribution, ['a': 2d, 'b': 1d, 'm': 1d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 2, result[0].args[0]
    }

    void testChiSquareValidator() {
        def validDistribution = DistributionType.CHISQUAREDIST
        assertEquals 0, validator.validate(validDistribution, ['n': 1d]).size()
    }

    void testFailingChiSquareValidator() {
        def badDistribution = DistributionType.CHISQUAREDIST
        def result = validator.validate(badDistribution, ['n': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testStudentValidator() {
        def validDistribution = DistributionType.STUDENTDIST
        assertEquals 0, validator.validate(validDistribution, ['n': 1d]).size()
    }

    void testFailingStudentValidator() {
        def badDistribution = DistributionType.STUDENTDIST
        def result = validator.validate(badDistribution, ['n': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testBinomialValidator() {
        def validNegBinomial = DistributionType.BINOMIALDIST
        assertEquals 0, validator.validate(validNegBinomial, ['n': 1, 'p': 0]).size()
    }

    void testFailingBinomialValidator() {
        def badNegBinomial = DistributionType.BINOMIALDIST
        def result = validator.validate(badNegBinomial, ['n': 0, 'p': 2])
        assertNotNull result
        assertEquals 'two error messages', 2, result.size()
        assert result[0].msg instanceof String
        assertEquals 2d, result[0].args[0]
        assertEquals 0d, result[1].args[0]
    }

    void testInvGaussianValidator() {
        def validDistribution = DistributionType.INVERSEGAUSSIANDIST
        assertEquals 0, validator.validate(validDistribution, ['mu': 1d, 'lambda': 1d]).size()
    }

    void testFailingInvGaussianValidator() {
        def badDistribution = DistributionType.INVERSEGAUSSIANDIST
        def result = validator.validate(badDistribution, ['mu': 0d, 'lambda': 0d])
        assertNotNull result
        assertEquals 'two error messages', 2, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
        assertEquals 0, result[1].args[0]
    }

    void testValueOf() {
        DistributionType.all.each {
            assertSame "${it.displayName}", it, DistributionType.valueOf(it.toString())
        }
    }
}