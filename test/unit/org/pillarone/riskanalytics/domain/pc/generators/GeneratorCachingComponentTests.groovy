package org.pillarone.riskanalytics.domain.pc.generators

import org.pillarone.riskanalytics.domain.utils.*

public class GeneratorCachingComponentTests extends GroovyTestCase {

    void testCache() {
        CacheTestComponent component = new CacheTestComponent()
        RandomDistribution distribution = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 0d, "stDev": 1d])
        DistributionModified modification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        def generator = component.getGenerator(distribution, modification)
        assertNotNull generator
        def generator2 = component.getGenerator(distribution, modification)
        assertSame generator, generator2

        distribution = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 1d, "stDev": 1d])
        generator2 = component.getGenerator(distribution, modification)
        assertNotSame generator, generator2
    }

}

class CacheTestComponent extends GeneratorCachingComponent {

    public void doCalculation() {

    }

    public def getGenerator(distribution, modifier) {
        getCachedGenerator(distribution, modifier)
    }

}