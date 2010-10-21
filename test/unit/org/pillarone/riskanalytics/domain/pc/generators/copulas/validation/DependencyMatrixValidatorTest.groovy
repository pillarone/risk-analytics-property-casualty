package org.pillarone.riskanalytics.domain.pc.generators.copulas.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType
import org.pillarone.riskanalytics.core.parameterization.MatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopulaType

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class DependencyMatrixValidatorTest extends GroovyTestCase {


    AbstractParameterValidationService validator = new DependencyMatrixValidator().validationService

    void testDefaultDependencyMatrixValidator() {
        def defaultPerilCopulaT = PerilCopulaType.T
        assertEquals 0, validator.validate(defaultPerilCopulaT,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [0.0, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull']),
                        "degreesOfFreedom": 10]).size()

        def defaultLobCopulaT = LobCopulaType.T
        assertEquals 0, validator.validate(defaultLobCopulaT,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [0.0, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull']),
                        "degreesOfFreedom": 10]).size()

        def defaultPerilCopulaNormal = PerilCopulaType.NORMAL
        assertEquals 0, validator.validate(defaultPerilCopulaNormal,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [0.0, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull'])]).size()

        def defaultLobCopulaNormal = LobCopulaType.NORMAL
        assertEquals 0, validator.validate(defaultLobCopulaNormal,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [0.0, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull'])]).size()
    }

    void testDefaultDependencyMatrixValidatorInvalidDiagonalNonPosDef() {
        def defaultPerilCopulaT = PerilCopulaType.T
        def errors1 = validator.validate(defaultPerilCopulaT,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[-2.0, 0.0], [0.0, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull']),
                        "degreesOfFreedom": 10])
        assertEquals 'two error messages', 2, errors1.size()
        assertEquals 'diagonal not 1 everywhere', 't.copula.strategy.dependency.matrix.invalid.diagonal', errors1[1].msg
        assertEquals 'not positive definite', 't.copula.strategy.dependency.matrix.non.positive.definite', errors1[0].msg

        def defaultLobCopulaT = LobCopulaType.T
        def errors2 = validator.validate(defaultLobCopulaT,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [0.0, -2.0]],
                        ['fire', 'hull'], ['fire', 'hull']),
                        "degreesOfFreedom": 10])
        assertEquals 'two error messages', 2, errors2.size()
        assertEquals 'diagonal not 1 everywhere', 't.copula.strategy.dependency.matrix.invalid.diagonal', errors2[1].msg
        assertEquals 'not positive definite', 't.copula.strategy.dependency.matrix.non.positive.definite', errors2[0].msg

        def defaultPerilCopulaNormal = PerilCopulaType.NORMAL
        def errors3 = validator.validate(defaultPerilCopulaNormal,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[-1.0, 0.0], [0.0, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull'])])
        assertEquals 'two error messages', 2, errors3.size()
        assertEquals 'diagonal not 1 everywhere', 'normal.copula.strategy.dependency.matrix.invalid.diagonal', errors3[1].msg
        assertEquals 'not positive definite', 'normal.copula.strategy.dependency.matrix.non.positive.definite', errors3[0].msg

        def defaultLobCopulaNormal = LobCopulaType.NORMAL
        def errors4 = validator.validate(defaultLobCopulaNormal,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [0.0, -1.0]],
                        ['fire', 'hull'], ['fire', 'hull'])])
        assertEquals 'two error messages', 2, errors4.size()
        assertEquals 'diagonal not 1 everywhere', 'normal.copula.strategy.dependency.matrix.invalid.diagonal', errors4[1].msg
        assertEquals 'not positive definite', 'normal.copula.strategy.dependency.matrix.non.positive.definite', errors4[0].msg

    }

    void testDefaultDependencyMatrixValidatorNonSymmetric() {

        def defaultPerilCopulaT = PerilCopulaType.T
        def errors1 = validator.validate(defaultPerilCopulaT,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[-2.0, 1.0], [0.0, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull']),
                        "degreesOfFreedom": 10])
        assertEquals 'two error messages', 2, errors1.size()
        assertEquals 'diagonal not 1 everywhere', 't.copula.strategy.dependency.matrix.invalid.diagonal', errors1[1].msg
        assertEquals 'not symmetric', 't.copula.strategy.dependency.matrix.non.symmetric', errors1[0].msg

        def defaultLobCopulaT = LobCopulaType.T
        def errors2 = validator.validate(defaultLobCopulaT,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [5.0, -2.0]],
                        ['fire', 'hull'], ['fire', 'hull']),
                        "degreesOfFreedom": 10])
        assertEquals 'two error messages', 2, errors2.size()
        assertEquals 'diagonal not 1 everywhere', 't.copula.strategy.dependency.matrix.invalid.diagonal', errors2[1].msg
        assertEquals 'not symmetric', 't.copula.strategy.dependency.matrix.non.symmetric', errors2[0].msg

        def defaultPerilCopulaNormal = PerilCopulaType.NORMAL
        def errors3 = validator.validate(defaultPerilCopulaNormal,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [-0.1, 1.0]],
                        ['fire', 'hull'], ['fire', 'hull'])])
        assertEquals 'one error message', 1, errors3.size()
        assertEquals 'not symmetric', 'normal.copula.strategy.dependency.matrix.non.symmetric', errors3[0].msg

        def defaultLobCopulaNormal = LobCopulaType.NORMAL
        def errors4 = validator.validate(defaultLobCopulaNormal,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, -1.0], [0.0, -1.0]],
                        ['fire', 'hull'], ['fire', 'hull'])])
        assertEquals 'two error message', 2, errors4.size()
        assertEquals 'diagonal not 1 everywhere', 'normal.copula.strategy.dependency.matrix.invalid.diagonal', errors4[1].msg
        assertEquals 'not symmetric', 'normal.copula.strategy.dependency.matrix.non.symmetric', errors4[0].msg

    }

}