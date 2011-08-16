package org.pillarone.riskanalytics.domain.utils

import org.apache.commons.math.stat.StatUtils
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

class RandomNumberGeneratorFactoryTests extends GroovyTestCase {

    void testCreateTypedGenerator() {
        DistributionType.all.each {
            Map params = [:]
            params["mean"] = 5
            params["stDev"] = 10
            params["lambda"] = 0.2
            params["alpha"] = 0.4
            params["beta"] = 0.7
            params["gamma"] = 0.5
            params["delta"] = 0.3
            params["a"] = 0.3
            params["b"] = 0.9
            params["m"] = 0.9
            params["n"] = 1
            params["p"] = 0.5
            params["constant"] = 1
            params["observations"] = new TableMultiDimensionalParameter([10, 100, 1000], ['observations'])
            params["probabilities"] = new SimpleMultiDimensionalParameter([0.2, 0.3, 0.5])
            params["discreteEmpiricalValues"] = new TableMultiDimensionalParameter([[10, 100, 1000], [0.2, 0.3, 0.5]], ['observations', 'probabilities'])
            params["discreteEmpiricalCumulativeValues"] = new TableMultiDimensionalParameter([[10, 100, 1000], [0.2, 0.5, 1]], ['observations', 'cumulative probabilities'])
            params["supportPoints"] = new TableMultiDimensionalParameter([[0, 10, 100, 1000], [0, 0.2, 0.5, 1]], ['values', 'cumulative probabilities'])
            params["values"] = [0, 10, 100, 1000]
            params["cumulative probabilities"] = [0, 0.2, 0.5, 1.0]
            params["mu"] = 1
            params["sigma"] = 1
            params["CV"] = 1
            params["constants"] = new TableMultiDimensionalParameter([0,1], ['constants'])
            params["xi"] = 1
            params["tau"] = 1


            IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(
                    DistributionType.getStrategy(it, params))
            assertNotNull generator
            assertSame it, generator.type
            assertEquals params, generator.parameters
        }
    }


    /* Tests whether generated lognormal series has really the desired mean and stdDev
     * Thus 2 things are tested: 1) the conversion from the (Mean, StdDev) to (mu, sigma)
     *                           2) the lognormal generator from the library
    */
    void testlogNormalDistribution() {
        IRandomNumberGenerator generator1 = RandomNumberGeneratorFactory.getGenerator(
                DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["mean": 2, "stDev": 1]))
        List<Double> curList = []
        for (int i = 0; i < 20000; i++) curList.add(generator1.nextValue())
        Collections.sort(curList)
        double[] temp = curList.toArray(); //StatUtils operate only on array of double, hence the conversion               
        double empiricalMean = StatUtils.mean(temp) //works only with array of double
        double empiricalVar = StatUtils.variance(temp)
        assertEquals 2d, empiricalMean, 0.02
        assertEquals 1d, empiricalVar, 0.05
    }

    void testCensoredShiftDistribution() {
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(
                DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 0, "stDev": 1]),
                DistributionModifier.getStrategy(DistributionModifier.CENSOREDSHIFT, ["min": -1, "max": 1, "shift": 1])
        )
        List<Double> randomNumbers = []
        for (int i = 0; i < 100; i++) randomNumbers.add(generator.nextValue())
        Collections.sort randomNumbers
        assertEquals "correct min", 0, randomNumbers[0]
        assertEquals "correct max", 2, randomNumbers[-1]
    }

    void testTruncatedShiftDistribution() {
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(
                DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 0, "stDev": 1]),
                DistributionModifier.getStrategy(DistributionModifier.TRUNCATEDSHIFT, ["min": -1, "max": 1, "shift": 1])
        )
        List<Double> randomNumbers = []
        for (int i = 0; i < 100; i++) randomNumbers.add(generator.nextValue())
        Collections.sort randomNumbers
        assertTrue "correct min", 0 <= randomNumbers[0]
        assertTrue "correct max", 2 >= randomNumbers[-1]
    }
}
