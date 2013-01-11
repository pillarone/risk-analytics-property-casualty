package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.randomnumbers.DependencyType
import org.pillarone.riskanalytics.domain.utils.randomnumbers.IMultiRandomGenerator
import umontreal.iro.lecuyer.probdist.NormalDist
import cern.colt.matrix.DoubleMatrix2D
import cern.colt.matrix.DoubleMatrix1D
import cern.colt.matrix.linalg.EigenvalueDecomposition
import cern.colt.matrix.impl.DenseDoubleMatrix2D


/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
abstract class NormalCopulaStrategy extends AbstractCopulaStrategy {

    //DoubleMatrix2D sigmaMatrix

    IMultiRandomGenerator generator
    AbstractMultiDimensionalParameter dependencyMatrix

    public List<Number> getRandomVector() {

        List<List<Double>> values = dependencyMatrix.getValues();
        List<Double> diag = new ArrayList<Double>();
        for (int i = 0; i < values.size(); i++) {
            diag.add(values.get(i).get(i))
        }
        if (!(diag.min() == 1d && diag.max() == 1d)) {
            throw new IllegalArgumentException("['NormalCopulaStratey.dependencyMatrixInvalidDiagonal']");
        }
        DenseDoubleMatrix2D SIGMA = new DenseDoubleMatrix2D((double[][]) values);
        DoubleMatrix2D SIGMAtranspose = SIGMA.viewDice();
        if (!SIGMAtranspose.equals(SIGMA)){
            throw new IllegalArgumentException("['NormalCopulaStratey.dependencyMatrixNonSymmetric']");
        }
        EigenvalueDecomposition eigenvalueDecomp = new EigenvalueDecomposition(SIGMA);
        DoubleMatrix1D eigenvalues = eigenvalueDecomp.getRealEigenvalues();
        eigenvalues.viewSorted();
        if (eigenvalues.get(0) <= 0){
            throw new IllegalArgumentException("['NormalCopulaStratey.dependencyMatrixNonPosDef']");
        }

        int size = dependencyMatrix.valueRowCount
        generator = DependencyType.getStrategy(DependencyType.NORMAL, ["meanVector": new double[size], "sigmaMatrix": dependencyMatrix.values])
        List<Number> randomVector = generator.nextVector()
        for (int j = 0; j < randomVector.size(); j++) {
            randomVector[j] = NormalDist.cdf(0, 1d, randomVector[j])
        }
        randomVector
    }

    public List<String> getTargetNames() {
        return dependencyMatrix.rowNames
    }

    public Map getParameters() {
        return ["dependencyMatrix": dependencyMatrix]
    }
}