package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class CopulaStrategyFactory {

    private static ICopulaStrategy getNormalCopula(CopulaType type, AbstractMultiDimensionalParameter dependencyMatrix) {
        if (type instanceof LobCopulaType) {
            return new LobNormalCopulaStrategy(dependencyMatrix: dependencyMatrix)
        }
        else {
            return new PerilNormalCopulaStrategy(dependencyMatrix: dependencyMatrix)
        }
    }

    private static ICopulaStrategy getIndependentCopula(CopulaType type, AbstractMultiDimensionalParameter targets) {
        if (type instanceof LobCopulaType) {
            return new LobIndependentCopulaStrategy(targets: targets)
        }
        else {
            return new PerilIndependentCopulaStrategy(targets: targets)
        }
    }

    private static ICopulaStrategy getFrechetUpperBoundCopula(CopulaType type, AbstractMultiDimensionalParameter targets) {
        if (type instanceof LobCopulaType) {
            return new LobFrechetUpperBoundCopulaStrategy(targets: targets)
        }
        else {
            return new PerilFrechetUpperBoundCopulaStrategy(targets: targets)
        }
    }

    private static ICopulaStrategy getTCopula(CopulaType type, AbstractMultiDimensionalParameter dependencyMatrix, int degreesOfFreedom) {
        if (type instanceof LobCopulaType) {
            return new LobTCopulaStrategy(dependencyMatrix: dependencyMatrix, degreesOfFreedom: degreesOfFreedom)
        }
        else {
            return new PerilTCopulaStrategy(dependencyMatrix: dependencyMatrix, degreesOfFreedom: degreesOfFreedom)
        }
    }

    private static ICopulaStrategy getGumbelCopula(CopulaType type, double lambda, int dimension, AbstractMultiDimensionalParameter targets) {
        if (type instanceof LobCopulaType) {
            return new LobGumbelCopulaStrategy(lambda: lambda, dimension: dimension, targets: targets)
        }
        else {
            return new PerilGumbelCopulaStrategy(lambda: lambda, dimension: dimension, targets: targets)
        }
    }

    static ICopulaStrategy getCopulaStrategy(CopulaType type, Map parameters) {
        ICopulaStrategy copula
        switch (type) {
            case LobCopulaType.NORMAL:
                copula = getNormalCopula(type, (AbstractMultiDimensionalParameter) parameters["dependencyMatrix"])
                break
            case LobCopulaType.INDEPENDENT:
                copula = getIndependentCopula(type, (AbstractMultiDimensionalParameter) parameters["targets"])
                break
            case LobCopulaType.FRECHETUPPERBOUND:
                copula = getFrechetUpperBoundCopula(type, (AbstractMultiDimensionalParameter) parameters["targets"])
                break
            case LobCopulaType.T:
                copula = getTCopula(type, (AbstractMultiDimensionalParameter) parameters["dependencyMatrix"], (int) parameters["degreesOfFreedom"])
                break
            case LobCopulaType.GUMBEL:
                copula = getGumbelCopula(type, (double) parameters["lambda"], (int) parameters["dimension"], (AbstractMultiDimensionalParameter) parameters["targets"])
                break
            case PerilCopulaType.NORMAL:
                copula = getNormalCopula(type, (AbstractMultiDimensionalParameter) parameters["dependencyMatrix"])
                break
            case PerilCopulaType.INDEPENDENT:
                copula = getIndependentCopula(type, (AbstractMultiDimensionalParameter) parameters["targets"])
                break
            case PerilCopulaType.FRECHETUPPERBOUND:
                copula = getFrechetUpperBoundCopula(type, (AbstractMultiDimensionalParameter) parameters["targets"])
                break
            case PerilCopulaType.T:
                copula = getTCopula(type, (AbstractMultiDimensionalParameter) parameters["dependencyMatrix"], (int) parameters["degreesOfFreedom"])
                break
            case PerilCopulaType.GUMBEL:
                copula = getGumbelCopula(type, (double) parameters["lambda"], (int) parameters["dimension"], (AbstractMultiDimensionalParameter) parameters["targets"])
                break
            default:
                throw new InvalidParameterException("PerilCopulaType $type not implemented")
        }
        return copula
    }
}