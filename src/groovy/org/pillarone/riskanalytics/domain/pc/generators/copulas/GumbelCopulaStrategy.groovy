package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory

/**
 * @author Michael-Noe (at) Web (dot) de
 */
abstract class GumbelCopulaStrategy extends AbstractCopulaStrategy {

    RandomDistribution distribution
    IRandomNumberGenerator generatorUniform

    AbstractMultiDimensionalParameter targets
    double lambda
    int dimension

    public List<Number> getRandomVector() {
        List<Number> randomVector = new ArrayList()
        distribution = DistributionType.getStrategy(DistributionType.UNIFORM, ["a": 0d, "b": 1d])
        generatorUniform = RandomNumberGeneratorFactory.getGenerator(distribution)
        double s = (double) generatorUniform.nextValue()
        double q = (double) generatorUniform.nextValue()
        double t = newtonApproximation(q, lambda, 0.001, 10000)

        for (int j = 0; j < dimension - 1; j++) {
            randomVector[j] = Math.exp(-Math.pow(s * Math.pow((-Math.log(t)), lambda), 1 / lambda))
            if (j == dimension - 2) {
                randomVector[j + 1] = Math.exp(-Math.pow((double) (1 - s) * Math.pow((double) (-Math.log(t)), lambda), 1 / lambda))
            }
            else {
                s = Math.exp(-Math.pow((double) (1 - s) * Math.pow((double) (-Math.log(t)), lambda), 1 / lambda))
            }
        }
        return randomVector
    }

    public List<String> getTargetNames() {
        targets.values
    }

    public Map getParameters() {
        return ["lambda": lambda, "dimension": dimension, "targets": targets]
    }

    double newtonApproximation(double q, double theta, double epsilon, int maxSteps) {
        double xOld = q
        double xNew = 1 - q
        int step = 0
        while (Math.abs(xOld - xNew) > epsilon && step < maxSteps) {
            xOld = xNew
            xNew = xOld - (xOld - xOld / theta * Math.log(xOld) - q) / (1 - 1 / theta * (Math.log(xOld) + 1))

            ++step
        }
        return xNew
    }
}