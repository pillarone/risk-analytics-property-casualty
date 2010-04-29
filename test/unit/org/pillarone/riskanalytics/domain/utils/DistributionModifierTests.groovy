package org.pillarone.riskanalytics.domain.utils

class DistributionModifierTests extends GroovyTestCase {


    void testValueOf() {
        DistributionModifier.all.each {
            assertSame "${it.displayName}", it, DistributionModifier.valueOf(it.toString())
        }
    }
}