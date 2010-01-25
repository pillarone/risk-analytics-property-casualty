package org.pillarone.riskanalytics.domain.pc.generators.severities

import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency

class EventSeverityGeneratorTests extends GroovyTestCase {

    void testGenerateLognormalClaims() {
        generateSeverities(new EventSeverityGenerator())
    }

    private void generateSeverities(EventSeverityGenerator generator) {
        generator.inSeverityCount << new Frequency(value: 5)

        assertTrue generator.outSeverities.isEmpty()
        generator.validateParameterization()
        generator.doCalculation()
        assertEquals(5, generator.outSeverities.size())
        generator.reset()
        assertTrue generator.outSeverities.isEmpty()
    }
}