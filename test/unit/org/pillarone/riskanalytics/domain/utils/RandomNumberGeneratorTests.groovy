package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.*
import umontreal.iro.lecuyer.randvar.*
import umontreal.iro.lecuyer.rng.F2NL607
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.ConstantVariateGenerator
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory
import org.pillarone.riskanalytics.domain.utils.PiecewiseLinearDistribution

class RandomNumberGeneratorTests extends GroovyTestCase {

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
        double[] obs= [0.0, 10.0, 20.0,25.0, 100.0]
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(
                RandomDistributionFactory.getDistribution(DistributionType.PIECEWISELINEAREMPIRICAL,
                        ["observations": new TableMultiDimensionalParameter([obs.toList()], ["observations"])]))
        List<Double> list1=[]
        for (int i=0;i<1000;i++) list1.add(generator.nextValue())
        //list1.sort()
        //for (int i=0;i<1000;i++) println list1[i] //visual assessment SZU&AM looks OK

    }

    void testRDFPiecewiseLinearDistributionGenerator() {
        double[] vals= [0.0, 10.0, 20.0, 25.0, 100.0]
        double[] probs=[0,    0.1, 0.3,   0.6, 1.0]
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
        double[] vals= [0.0, 10.0, 20.0, 25.0, 100.0]
        double[] probs=[0,    0.1, 0.3,   0.6, 1.0]
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

}