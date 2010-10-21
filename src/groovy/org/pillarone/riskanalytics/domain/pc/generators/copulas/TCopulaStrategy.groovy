package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory
import org.pillarone.riskanalytics.domain.utils.randomnumbers.DependencyType
import org.pillarone.riskanalytics.domain.utils.randomnumbers.IMultiRandomGenerator
import umontreal.iro.lecuyer.probdist.StudentDist
import cern.colt.matrix.DoubleMatrix2D
import cern.colt.matrix.DoubleMatrix1D
import cern.colt.matrix.linalg.EigenvalueDecomposition
import cern.colt.matrix.impl.DenseDoubleMatrix2D

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
        List<List<Double>> values = dependencyMatrix.getValues();
        DenseDoubleMatrix2D SIGMA = new DenseDoubleMatrix2D((double[][]) values);
        DoubleMatrix2D SIGMAtranspose = SIGMA.viewDice();
        if (!SIGMAtranspose.equals(SIGMA)) {
            throw new IllegalArgumentException("TCopulaStratey.dependencyMatrixNonSymmetric");
        }
        EigenvalueDecomposition eigenvalueDecomp = new EigenvalueDecomposition(SIGMA);
        DoubleMatrix1D eigenvalues = eigenvalueDecomp.getRealEigenvalues();
        eigenvalues.viewSorted();
        if (eigenvalues.get(0) <= 0) {
            throw new IllegalArgumentException("TCopulaStratey.dependencyMatrixNonPosDef");
        }

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