package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

public class RandomDistributionTests extends GroovyTestCase {

    void testEquals() {
        def r1 = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        def r2 = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        r2 = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 5])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionType.getStrategy(FrequencyDistributionType.EXPONENTIAL, ["lambda": 4.874])
        r2 = DistributionType.getStrategy(FrequencyDistributionType.EXPONENTIAL, ["lambda": 4.874])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionType.getStrategy(FrequencyDistributionType.EXPONENTIAL, ["lambda": 4.874])
        r2 = DistributionType.getStrategy(FrequencyDistributionType.EXPONENTIAL, ["lambda": 5])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.415])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.BETA, ["alpha": 0.4, "beta": 0.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.BETA, ["alpha": 0.4, "beta": 0.7])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.BETA, ["alpha": 0.4, "beta": 0.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.BETA, ["alpha": 0.4, "beta": 0.7001])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.GAMMA, ["alpha": 2.4, "lambda": 2.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.GAMMA, ["alpha": 2.4, "lambda": 2.7])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.GAMMA, ["alpha": 2.4, "lambda": 2.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.GAMMA, ["alpha": 2.4, "lambda": 2.7001])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.GUMBEL, ["beta": 0.4, "delta": 0.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.GUMBEL, ["beta": 0.4, "delta": 0.7])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.GUMBEL, ["beta": 0.4, "delta": 0.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.GUMBEL, ["beta": 0.4, "delta": 0.7001])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.LOGLOGISTIC, ["alpha": 2.4, "beta": 1.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.LOGLOGISTIC, ["alpha": 2.4, "beta": 1.7])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionType.getStrategy(ClaimSizeDistributionType.LOGLOGISTIC, ["alpha": 2.4, "beta": 1.7])
        r2 = DistributionType.getStrategy(ClaimSizeDistributionType.LOGLOGISTIC, ["alpha": 2.4, "beta": 1.7001])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())
    }

    void testConstantsDistribution() {
        List<Double> values = []
        RandomDistribution rd
        for (int i=1; i<=10; i++) {
            values.add(i)
            rd = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANTS, ["constants": new TableMultiDimensionalParameter(values, ["constants"])])
            assertEquals "U[1..${i}] mean", 0.5*(1 + i), rd.distribution.getMean()
            assertEquals "U[1..${i}] sdev", Math.sqrt((i*i - 1)/12), rd.distribution.getStandardDeviation(), 1E-9
            // U[1..3] sdev expected:<0.8164965809481384> but was:<0.8164965809277257>
            for (int j=0; j<31; j++) {
                double x = 1 + (i - 1) * j / 31
                assertEquals "U[1..${i}] cdf(${x})", Math.floor(x) / i, rd.distribution.cdf(x), 1E-9
                double z = j / 31
                assertEquals "U[1..${i}] inverseF(${z})", 1 + Math.floor(z * i), rd.distribution.inverseF(z), 1E-9
            }
        }
        assertEquals "U[1..10] inverseF(.5)", 5, rd.distribution.inverseF(0.5)

        values.add(11)
        rd = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANTS, ["constants": new TableMultiDimensionalParameter(values, ["constants"])])
        assertEquals "U[1..11] inverseF(.5)", 6, rd.distribution.inverseF(0.5)

        values.add(6)
        rd = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANTS, ["constants": new TableMultiDimensionalParameter(values, ["constants"])])
        assertEquals "U[1..11,6] inverseF(.5)", 6, rd.distribution.inverseF(0.5)

        values.add(11)
        rd = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANTS, ["constants": new TableMultiDimensionalParameter(values, ["constants"])])
        assertEquals "U[1..11,6,11] inverseF(.5)", 6, rd.distribution.inverseF(0.5)

        values.add(1)
        rd = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANTS, ["constants": new TableMultiDimensionalParameter(values, ["constants"])])
        assertEquals "U[1..11,1,6,11] inverseF(.5)", 6, rd.distribution.inverseF(0.5)

        values.add(5.9)
        rd = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANTS, ["constants": new TableMultiDimensionalParameter(values, ["constants"])])
        assertEquals "U[1..11,1,6,11,5.9] inverseF(.5)", 6, rd.distribution.inverseF(0.5)

        values.add(2)
        rd = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANTS, ["constants": new TableMultiDimensionalParameter(values, ["constants"])])
        assertEquals "U[1..11,1,2,5.9,6,11] inverseF(.5)", 5.9, rd.distribution.inverseF(0.5)
    }
}