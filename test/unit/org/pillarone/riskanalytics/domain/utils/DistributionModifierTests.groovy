package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

class DistributionModifierTests extends GroovyTestCase {

    void testValueOf() {
        DistributionModifier.all.each {
            assertSame "${it.displayName}", it, DistributionModifier.valueOf(it.toString())
        }
    }

    void testNotImplemented() {
        Map params = [dilationFactor: Math.sqrt(2)]
        // use DistributionModifier's (and implicitly) AbstractParameterObjectClassifier's constructor to
        // create a modifier type that isn't implemented, then try using it to create a modified distribution
        DistributionModifier modifier = new DistributionModifier("Dilation", "DILATION", params)
        DistributionModified modified
        shouldFail InvalidParameterException, {
            modified = DistributionModifier.getStrategy(modifier, [:])
        }
    }
}