package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.randomnumbers.DependencyType
import org.pillarone.riskanalytics.domain.utils.randomnumbers.IMultiRandomGenerator
import umontreal.iro.lecuyer.probdist.NormalDist

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class NormalCopulaStrategy extends AbstractCopulaStrategy {

    //DoubleMatrix2D sigmaMatrix

    IMultiRandomGenerator generator
    AbstractMultiDimensionalParameter dependencyMatrix

    public List<Number> getRandomVector() {
        // todo: check if the matrix is symmetric, or input only for lower triangle
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