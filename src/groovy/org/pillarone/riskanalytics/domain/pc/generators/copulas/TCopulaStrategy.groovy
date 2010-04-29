package org.pillarone.riskanalytics.domain.pc.generators.copulas

import cern.colt.matrix.DoubleMatrix2D
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory
import org.pillarone.riskanalytics.domain.utils.randomnumbers.DependencyType
import org.pillarone.riskanalytics.domain.utils.randomnumbers.IMultiRandomGenerator
import umontreal.iro.lecuyer.probdist.StudentDist

/**
 * @author Michael-Noe (at) Web (dot) de
 */
abstract class TCopulaStrategy extends AbstractCopulaStrategy {

    DoubleMatrix2D sigmaMatrix

    IMultiRandomGenerator generator

    AbstractMultiDimensionalParameter dependencyMatrix
    int degreesOfFreedom
    Number chisquareRandomNumber
    IRandomNumberGenerator generatorForChiSquare

    public List<Number> getRandomVector() {
        // todo: check is the matrix is symmetric, or input only for lower triangle
        int size = dependencyMatrix.valueRowCount
        generator = DependencyType.getStrategy(DependencyType.NORMAL, ["meanVector": new double[size], "sigmaMatrix": dependencyMatrix.values])
        generatorForChiSquare = RandomNumberGeneratorFactory.getGenerator(DistributionType.getStrategy(DistributionType.CHISQUAREDIST, ["n": degreesOfFreedom]))

        List<Number> randomVector = generator.nextVector()
        double factor = (double) degreesOfFreedom / generatorForChiSquare.nextValue()
        factor = Math.sqrt(factor)
        for (int i = 0; i < randomVector.size(); ++i) {
            randomVector[i] = StudentDist.cdf(degreesOfFreedom, randomVector[i] * factor)
        }
        return randomVector
    }

    public List<String> getTargetNames() {
        return dependencyMatrix.rowNames
    }

    public Map getParameters() {
        return ["dependencyMatrix": dependencyMatrix,
                "degreesOfFreedom": degreesOfFreedom]
    }
}