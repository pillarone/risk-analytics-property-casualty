package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.MatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.RandomVariateDistribution
import org.pillarone.riskanalytics.domain.utils.RandomVariateDistributionFactory

/**
 *  For the Correlation test , you  need to uncomment the count and manipulate it manually. Also the tolerance
 *  variable "tol" and the statistical tests need to be manually
 *
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NormalCopulaStrategyTests extends GroovyTestCase {

    static Copula getCopula0() {
        return new LobCopula(
                parmCopulaStrategy: CopulaStrategyFactory.getCopulaStrategy(
                        LobCopulaType.NORMAL,
                        ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.0], [0.1, 1.0]],
                                ['fire', 'hull'], ['fire', 'hull'])]))
    }

    static Copula getCopula1() {
        return new LobCopula(
                parmCopulaStrategy: CopulaStrategyFactory.getCopulaStrategy(
                        LobCopulaType.NORMAL,
                        ["dependencyMatrix": new MatrixMultiDimensionalParameter([[4d, 2d], [2d, 4d]],
                                ['fire', 'hull'], ['fire', 'hull'])]))
    }



    void testDoCalculation() {
        LobCopula copula = getCopula0()
        copula.inNumber << new Frequency(value: 3)
        copula.doCalculation()
        copula.toString()
    }

    void testCorrelations() {
        def covariace = 2.0
        def sigma = [[4.0, covariace], [covariace, 4.0]]
        //int value = 10000
        int value = 2
        double tol = 0.1
        //double tol = 0.01

        List<Number> x = []
        List<Number> y = [] //Variables  for the statistical test
        List<String> lines = ["Fire", "Hull"]
        List<Number> varList = []

        Copula copula = getCopula1()

        copula.inNumber << new Frequency(value: value)
        copula.doCalculation()
        for (i in 0..<value) varList.add(copula.outProbabilities[i].probabilities)
        assertEquals value, varList.size()

        RandomVariateDistribution distribution = RandomVariateDistributionFactory.getDistribution(DistributionType.NORMAL, ["mean": 0d, "stDev": 2.0])
        for (i in 0..<value) {
            x.add(distribution.inverseDistributionFunction(varList[i][0]))
            y.add(distribution.inverseDistributionFunction(varList[i][1]))
        }
        assertEquals value, x.size()
        assertEquals value, y.size()
        /*
                assertEquals 0.0, x.sum() / x.size(), tol
                assertEquals 0.0, y.sum() / y.size(), tol
                def x2 = 0.0
                def y2 = 0.0
                x.each {current -> x2 += current * current }
                y.each {current -> y2 += current * current }
                assertEquals 4.0, x2 / x.size() - Math.pow(x.sum() / x.size(), 2.0), tol
                assertEquals 4.0, y2 / y.size() - Math.pow(y.sum() / y.size(), 2.0), tol
                def xyCov = 0
                y.eachWithIndex {cur, i -> xyCov += cur * x[i]  }
                assertEquals covariace, xyCov / y.size() - (y.sum() * x.sum()) / (x.size() * y.size()), tol
        */
    }

    void testGetParameters() {
        Copula copula = getCopula0()
        AbstractMultiDimensionalParameter matrix = ((NormalCopulaStrategy) copula.getParmCopulaStrategy()).getDependencyMatrix()
        assertEquals matrix, (((NormalCopulaStrategy) copula.getParmCopulaStrategy()).getParameters()).get("dependencyMatrix")
        assertEquals 1.0, matrix.getValueAt(1, 1)
        assertEquals 0.1, matrix.getValueAt(1, 2)
        assertEquals 0.0, matrix.getValueAt(2, 1)
        assertEquals 1.0, matrix.getValueAt(2, 2)
    }
}