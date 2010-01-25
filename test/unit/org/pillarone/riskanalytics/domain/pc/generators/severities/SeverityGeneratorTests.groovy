package org.pillarone.riskanalytics.domain.pc.generators.severities

import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency

class SeverityGeneratorTests extends GroovyTestCase {

    void testGenerateLognormalClaims() {
        generateSeverities(new SeverityGenerator())
    }

    private void generateSeverities(SeverityGenerator generator) {
        generator.inClaimCount << new Frequency(value: 5)

        assertTrue generator.outSeverities.isEmpty()
        generator.doCalculation()
        assertEquals(5, generator.outSeverities.size())
        generator.reset()
        assertTrue generator.outSeverities.isEmpty()
    }
}