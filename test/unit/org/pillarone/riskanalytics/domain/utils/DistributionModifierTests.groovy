package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.domain.utils.DistributionModifier


class DistributionModifierTests extends GroovyTestCase {


    void testValueOf() {
        DistributionModifier.all.each {
            assertSame "${it.displayName}", it, DistributionModifier.valueOf(it.toString())
        }
    }
}