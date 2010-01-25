package org.pillarone.riskanalytics.domain.utils.randomnumbers

import org.pillarone.riskanalytics.core.util.MathUtils
import umontreal.iro.lecuyer.probdist.NormalDist
import umontreal.iro.lecuyer.randvar.NormalGen
import umontreal.iro.lecuyer.randvarmulti.MultinormalCholeskyGen

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DependentGeneratorFactory {
    private static IMultiRandomGenerator getNormalCopulaGenerator(double[] means, double[][] sigmaMatrix) {
        MultinormalCholeskyGen generator = new MultinormalCholeskyGen(
            new NormalGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE, new NormalDist(0d, 1d)),
            means,
            sigmaMatrix)
        return new MultiRandomNumberGenerator(generator: generator)
    }

    static IMultiRandomGenerator getGenerator(DependencyType type, Map parameters) {
        IMultiRandomGenerator generator
        switch (type) {
            case DependencyType.NORMAL:
                double[] means = new double[0]
                means = parameters["meanVector"] //.toArray(means)
                double[][] sigma = parameters["sigmaMatrix"]
                generator = getNormalCopulaGenerator(means, sigma)
                break
        }
        generator
    }
}