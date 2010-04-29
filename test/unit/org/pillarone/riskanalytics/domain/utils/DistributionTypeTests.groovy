package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author: dierk.koenig at canoo.com
 */
class DistributionTypeTests extends GroovyTestCase {

    void testDefaultUniformValidator() {
        def defaultUniform = DistributionType.UNIFORM
        assertNull defaultUniform.validate(["a": 0d, "b": 1d])
    }

    void testPoissonValidator() {
        def validPoisson = DistributionType.POISSON
        assertNull validPoisson.validate(["lambda": 0d])
    }

    void testFailingPoissonValidator() {
        def badPoisson = DistributionType.POISSON
        def result = badPoisson.validate(['lambda': -1d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        //assertEquals -1d, result[0].args[0]
    }

    void testNegativeBinomialValidator() {
        def validNegBinomial = DistributionType.NEGATIVEBINOMIAL
        assertNull validNegBinomial.validate(['gamma': 1, 'p': 0])
    }

    void testFailingNegativeBinomialValidator() {
        def badNegBinomial = DistributionType.NEGATIVEBINOMIAL
        def result = badNegBinomial.validate(['gamma': 0, 'p': 2])
        assertNotNull result
        assertEquals 'two error messages', 2, result.size()
        assert result[0].msg instanceof String
        assert result[1].msg instanceof String
        assertEquals 0d, result[0].args[0]
        assertEquals 2d, result[1].args[0]
    }

    void testNormalValidator() {
        def validDistribution = DistributionType.NORMAL
        assertNull validDistribution.validate(['mean': 0d, 'stDev': 1d])
    }

    void testFailingNormalValidator() {
        def badPoisson = DistributionType.NORMAL
        def result = badPoisson.validate(['mean': -1d, 'stDev': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testLogNormalValidator() {
        def validDistribution = DistributionType.LOGNORMAL
        assertNull validDistribution.validate(['mean': 0d, 'stDev': 1d])
    }

    void testFailingLogNormalValidator() {
        def badPoisson = DistributionType.LOGNORMAL
        def result = badPoisson.validate(['mean': -1d, 'stDev': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testLogNormalMuSigmaValidator() {
        def validDistribution = DistributionType.LOGNORMAL_MU_SIGMA
        assertNull validDistribution.validate(['mu': 0d, 'sigma': 1d])
    }

    void testFailingLogNormalMuSigmaValidator() {
        def badPoisson = DistributionType.LOGNORMAL_MU_SIGMA
        def result = badPoisson.validate(['mu': -1d, 'sigma': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testParetoValidator() {
        def validDistribution = DistributionType.PARETO
        assertNull validDistribution.validate(['alpha': 1d, 'beta': 1d])
    }

    // todo(sku): extend with additional failing msg
    void testFailingParetoValidator() {
        def badDistribution = DistributionType.PARETO
        def result = badDistribution.validate(['alpha': -1d, 'beta': 1d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        //assertEquals -1, result[0].args[0]
    }

    void testFailingUniformValidator() {
        def badUniform = DistributionType.UNIFORM
        def result = badUniform.validate(["a": 1d, "b": 0d])
        assertNotNull result
        assertEquals 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 1d, result[0].args[0]
        assertEquals 0d, result[0].args[1]
    }

    void testPieceWiseLinearValidator() {
        def defaultPieceWiseLinear = DistributionType.PIECEWISELINEAR
        assertNull defaultPieceWiseLinear.validate([supportPoints: new TableMultiDimensionalParameter(
                [[0.0, 1.0], [0.0, 1.0]], ["values", "cummulative probabilities"])])
    }

    void testFailingPieceWiseLinearValidator() {
        def pieceWiseLinearValuesNotIncr = DistributionType.PIECEWISELINEAR
        def result = pieceWiseLinearValuesNotIncr.validate([supportPoints: new TableMultiDimensionalParameter(
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
        assertNull validDistribution.validate(['a': 1d, 'b': 1d, 'm': 1d])
    }

    void testFailingTriangularValidator() {
        def badDistribution = DistributionType.TRIANGULARDIST
        def result = badDistribution.validate(['a': 2d, 'b': 1d, 'm': 1d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 2, result[0].args[0]
    }

    void testChiSquareValidator() {
        def validDistribution = DistributionType.CHISQUAREDIST
        assertNull validDistribution.validate(['n': 1d])
    }

    void testFailingChiSquareValidator() {
        def badDistribution = DistributionType.CHISQUAREDIST
        def result = badDistribution.validate(['n': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testStudentValidator() {
        def validDistribution = DistributionType.STUDENTDIST
        assertNull validDistribution.validate(['n': 1d])
    }

    void testFailingStudentValidator() {
        def badDistribution = DistributionType.STUDENTDIST
        def result = badDistribution.validate(['n': 0d])
        assertNotNull result
        assertEquals 'one error message', 1, result.size()
        assert result[0].msg instanceof String
        assertEquals 0, result[0].args[0]
    }

    void testBinomialValidator() {
        def validNegBinomial = DistributionType.BINOMIALDIST
        assertNull validNegBinomial.validate(['n': 1, 'p': 0])
    }

    void testFailingBinomialValidator() {
        def badNegBinomial = DistributionType.BINOMIALDIST
        def result = badNegBinomial.validate(['n': 0, 'p': 2])
        assertNotNull result
        assertEquals 'two error messages', 2, result.size()
        assert result[0].msg instanceof String
        assertEquals 2d, result[0].args[0]
        assertEquals 0d, result[1].args[0]
    }

    void testInvGaussianValidator() {
        def validDistribution = DistributionType.INVERSEGAUSSIANDIST
        assertNull validDistribution.validate(['mu': 1d, 'lambda': 1d])
    }

    void testFailingInvGaussianValidator() {
        def badDistribution = DistributionType.INVERSEGAUSSIANDIST
        def result = badDistribution.validate(['mu': 0d, 'lambda': 0d])
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