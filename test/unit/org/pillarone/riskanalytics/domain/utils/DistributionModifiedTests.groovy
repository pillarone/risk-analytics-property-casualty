package org.pillarone.riskanalytics.domain.utils

public class DistributionModifiedTests extends GroovyTestCase {
    
    DistributionModified r1
    DistributionModified r2

    void testEquals() {
        r1 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1E6, "max": 1E8])
        r2 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1E6, "max": 1E8])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        r1 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1E6, "max": 1E8])
        r2 = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1E6, "max": 2E8])
        assertFalse r1.equals(r2)
        assertFalse r1.hashCode().equals(r2.hashCode())

        r1 = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        r2 = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()
    }

    void testEqualsAllNontrivial() {
        r1 = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        r2 = DistributionModifier.getStrategy(DistributionModifier.NONE, [shift:2d])
        assertTrue r1.equals(r2)
        assertEquals r1.hashCode(), r2.hashCode()

        DistributionModifier.all.asList().each {
            if (!it.equals(DistributionModifier.NONE)) {
                r1 = DistributionModifier.getStrategy(it, [min:1d, max:2d, shift:3d])
                r2 = DistributionModifier.getStrategy(it, [min:1d, max:2d, shift:3d])
                assertTrue r1.equals(r2)
                assertEquals r1.hashCode(), r2.hashCode()
            }
        }
    }

}