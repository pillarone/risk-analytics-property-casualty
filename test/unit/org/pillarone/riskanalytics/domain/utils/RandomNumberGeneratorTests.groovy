package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import umontreal.iro.lecuyer.rng.F2NL607
import umontreal.iro.lecuyer.probdist.*
import umontreal.iro.lecuyer.randvar.*

class RandomNumberGeneratorTests extends GroovyTestCase {

    /**
     * Creates a sequence from the "3n+1" problem: given a seed n, iteratively calculate n=f(n),
     * where f(n) = n/2 if n is even, or 3n+1 if n is odd, and terminate the sequence after n=1.
     * The initial seed n should be a positive integer.
     * 
     * Example: get3nPlusOneTrajectory(17, 13) generates the list:
     * [17, 52, 26, 13, 40, 20, 10, 5, 16, 8, 4, 2, 1] (as doubles),
     * without ever having to re-size the initially allocated list.
     */
    static List<Double> get3nPlusOneTrajectory(int start, int initialSize) {
        List<Double> list = new ArrayList<Double>(initialSize)
        for (int x = start; x > 0; x = (x <= 1) ? 0 : (x & 1) ? (3*x+1) : (x >> 1)) {
            list.add((double) x)
        }
        list
    }

    void testCreateNormalDistributionGenerator() {
        NormalGen generator0 = new NormalGen(new F2NL607(), new NormalDist(1d, 1d))
        IRandomNumberGenerator generator = new RandomNumberGenerator(generator: generator0)
        def value = generator.nextValue()
        assertNotNull value
        assertTrue(value instanceof Double)
    }

    void testCreateLognormalDistributionGenerator() {
        LognormalGen generator0 = new LognormalGen(new F2NL607(), new LognormalDist(1d, 1d))
        IRandomNumberGenerator generator = new RandomNumberGenerator(generator: generator0)
        def value = generator.nextValue()
        assertNotNull value
        assertTrue(value instanceof Double)
    }

    void testCreatePoissonDistributionGenerator() {
        PoissonGen generator0 = new PoissonGen(new F2NL607(), new PoissonDist(1d))
        IRandomNumberGenerator generator = new RandomNumberGenerator(generator: generator0)
        def value = generator.nextValue()
        assertNotNull value
        assertTrue(value instanceof Integer)
    }

    void testCreateUniformDistributionGenerator() {
        UniformGen generator0 = new UniformGen(new F2NL607(), new UniformDist(0d, 1d))
        IRandomNumberGenerator generator = new RandomNumberGenerator(generator: generator0)
        def value = generator.nextValue()
        assertNotNull value
        assertTrue(value instanceof Double)
    }

    void testCreateConstantDistributionGenerator() {
        IRandomNumberGenerator generator = new RandomNumberGenerator(generator: new ConstantVariateGenerator(constant: 1d))
        def value = generator.nextValue()
        assertEquals 1d, value
    }


    void testCreateDiscreteEmpiricalDistributionGenerator() {
        double[] obs= [0.5, 10.5, 20.5,25.5, 100.0]
        double[] vals= [0.0, 0.1, 0.4, 0.49, 0.01]
        double[] vals1= [0.0, 0.1, 0.4, 0.4, 0.01]
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(
                RandomDistributionFactory.getDistribution(DistributionType.DISCRETEEMPIRICAL,
                        ["discreteEmpiricalValues": new TableMultiDimensionalParameter([obs.toList(), vals.toList()], ["observations", "probabilities"])]))
        //["observations": obs, "probabilities": vals]))

        List<Double> list1=[]
        for (int i=0;i<1000;i++) list1.add(generator.nextValue())
        list1.sort()
        //for (int i=0;i<1000;i++) println list1[i]        //visual assessment SZU&AM looks OK
        shouldFail(AssertionError, {
            RandomNumberGeneratorFactory.getGenerator(
                    RandomDistributionFactory.getDistribution(DistributionType.DISCRETEEMPIRICAL,
                            ["discreteEmpiricalValues": new TableMultiDimensionalParameter([obs.toList(), vals1.toList()], ["observations", "probabilities"])]))
        })

    }
    void testCreategetPiecewiseLinearEmpiricalDistributionGenerator() {
        double[] obs = [0.0, 10.0, 20.0, 25.0, 100.0]
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(
                RandomDistributionFactory.getDistribution(DistributionType.PIECEWISELINEAREMPIRICAL,
                        ["observations": new TableMultiDimensionalParameter([obs.toList()], ["observations"])]))
        List<Double> list1=[]
        for (int i=0;i<1000;i++) list1.add(generator.nextValue())
        //list1.sort()
        //for (int i=0;i<1000;i++) println list1[i] //visual assessment SZU&AM looks OK

    }

    void testRDFPiecewiseLinearDistributionGenerator() {
        double[] vals  = [0.0, 10.0, 20.0, 25.0, 100.0]
        double[] probs = [0,    0.1,  0.3,  0.6,   1.0]
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(
                RandomDistributionFactory.getDistribution(DistributionType.PIECEWISELINEAR,
                        ["supportPoints": new TableMultiDimensionalParameter([vals.toList(), probs.toList()], ["values", "cummulative probabilities"])]))
        List<Double> list1=[]
        for (int i=0;i<1000;i++) list1.add(generator.nextValue())
        // list1.sort()
        //  for (int i=0;i<1000;i++) println list1[i]//visual assessment SZU&AM looks OK

    }

    void testPiecewiseLinearDistributionGenerator() {
        int sampleSize=10000
        double[] vals  = [0.0, 10.0, 20.0, 25.0, 100.0]
        double[] probs = [0,    0.1,  0.3,  0.6,   1.0]
        RandomVariateGen generator = new RandomVariateGen(new F2NL607(), new PiecewiseLinearDistribution(vals, probs))
        List<Double> list1=[]
        for (int i=0;i<sampleSize;i++) list1.add(generator.nextDouble())

        Distribution myDist=generator.getDistribution()
        double sampleSum=0
        list1.each {item-> sampleSum+=item}
        assert sampleSum/sampleSize-myDist.getMean() <2*myDist.getStandardDeviation()
        //list1.sort()
        //println "Theoretical Mean="+myDist.getMean()
        //println "Theoretical StdDev="+myDist.getStandardDeviation()
        //for (int i=0;i<sampleSize;i++) println list1[i]//checked in Excel
    }

    void testConstantsVariateGenerator() {
        double[] array = [1,7,7,7, 0,4, 3,0,  1,8,5,5, 0,2, 2,3] // use date digits from Gauss' lifespan
        ConstantsVariateGenerator generator = new ConstantsVariateGenerator(array as List<Double>)
        for (int i=0; i<1000; i++) {
            assertEquals "ConstantsVariateGenerator cycles correctly: ${i}", array[i & 15], generator.nextDouble()
        }

        List<Double> list = get3nPlusOneTrajectory(17, 13)
        generator = new ConstantsVariateGenerator(list)
        assertEquals "ConstantsVariateGenerator(3n+1,17) iterates correctly",
                list.collect {sprintf '%.0f', it}.join(" "),
                list.collect {sprintf '%.0f', generator.nextDouble()}.join(" ")
                // both strings should be: "17 52 26 13 40 20 10 5 16 8 4 2 1"
    }

    void testConstantsVariateGeneratorUsage() {
        List<Double> list = get3nPlusOneTrajectory(17, 13)
        RandomVariateGen generator = new ConstantsVariateGenerator(list)
        Distribution distribution = generator.getDistribution()
        int sampleSize = 1001 // note: 1001 = 7 * 11 * 13, and list.size() = 13 is a divisor of this!
        double sum = 0
        double squareSum = 0
        for (int i = 0; i < sampleSize; i++) {
            double x = generator.nextDouble()
            sum += x
            squareSum += x * x
        }
        double mean = sum / sampleSize
        double variance = squareSum / sampleSize - mean * mean
        double sdev = Math.sqrt(variance)

        assertEquals "ConstantsVariateGenerator(3n+1,17) mean", mean, distribution.getMean(), 1E-9
        assertEquals "ConstantsVariateGenerator(3n+1,17) sdev", sdev, distribution.getStandardDeviation(), 1E-9

    }
}