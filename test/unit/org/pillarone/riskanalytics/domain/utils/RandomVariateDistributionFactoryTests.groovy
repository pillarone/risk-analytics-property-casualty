package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.IRandomVariateDistribution
import org.pillarone.riskanalytics.domain.utils.RandomVariateDistributionFactory

class RandomVariateDistributionFactoryTests extends GroovyTestCase {

    void testCreateTypedDistribution() {
        DistributionType.all.each {
            Map params = [:]
            params["mean"] = 5
            params["stDev"] = 10
            params["lambda"] = 0.2
            params["alpha"] = 0.4
            params["beta"] = 0.7
            params["gamma"] = 0.5
            params["a"] = 0.3
            params["b"] = 0.9
            params["m"] = 0.9
            params["n"] = 1
            params["p"] = 0.5
            params["constant"] = 1
            params["observations"] = new TableMultiDimensionalParameter([10, 100, 1000], ['observations'])
            params["probabilities"] = [0.2, 0.3, 0.5]
            params["discreteEmpiricalValues"] = new TableMultiDimensionalParameter([[10, 100, 1000], [0.2, 0.3, 0.5]], ['observations', 'probabilities'])
            params["discreteEmpiricalCumulativeValues"] = new TableMultiDimensionalParameter([[10, 100, 1000], [0.2, 0.5, 1]], ['observations', 'cumulative probabilities'])
            params["supportPoints"] = new TableMultiDimensionalParameter([[0, 10, 100, 1000], [0, 0.2, 0.5, 1]], ['values', 'cummulative probabilities'])
            params["values"] = [0, 10, 100, 1000]
            params["cumulative probabilities"] = [0, 0.2, 0.5, 1.0]
            params["mu"] = 1
            params["sigma"] = 1
            params["constants"] = [0,1]

            //todo(bgi): re-enable BETA distribution once BetaDist constructor call is working (see RandomDistributionTests line 40 for more info)
            if (it == DistributionType.BETA) return
            IRandomVariateDistribution distribution = RandomVariateDistributionFactory.getDistribution(it, params)
            assertNotNull "${it}: ", distribution
            assertNotNull distribution.inverseDistributionFunction(0.5)
        }
    }
}