package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType


public class RandomDistributionTests extends GroovyTestCase {

    void testEquals() {
        def r1 = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        def r2 = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        r2 = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 5])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
        r2 = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
        r2 = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.415])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

    }

}