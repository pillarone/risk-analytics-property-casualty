package org.pillarone.riskanalytics.domain.utils

public class DistributionModifiedTests extends GroovyTestCase {

    void testEquals() {
        def r1 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1000000.0, "max": 100000000.0])
        def r2 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1000000.0, "max": 100000000.0])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1000000.0, "max": 100000000.0])
        r2 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1000000.0, "max": 200000000.0])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        r2 = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

    }

}