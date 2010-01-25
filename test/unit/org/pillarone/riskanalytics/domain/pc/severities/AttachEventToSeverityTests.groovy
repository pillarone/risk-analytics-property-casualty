package org.pillarone.riskanalytics.domain.pc.severities

import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream

/**
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttachEventToSeverityTests extends GroovyTestCase {
    void testUsage() {
        AttachEventToSeverity eventAttacher = new AttachEventToSeverity()

        List<String> marginals = ["fire", "hull"]
        DependenceStream ds1 = new DependenceStream([0.2d, 0.3d], marginals)
        DependenceStream ds2 = new DependenceStream([0.21d, 0.31d], marginals)
        eventAttacher.inProbabilities << ds1 << ds2

        eventAttacher.doCalculation()

        assertEquals "number of event severities", 2, eventAttacher.outEventSeverities.size()
        assertSame "no modifications in marginals", marginals, eventAttacher.outEventSeverities[0].marginals
        assertSame "no modifications in marginals", marginals, eventAttacher.outEventSeverities[1].marginals
        assertEquals "no modifications in severities", ds1.probabilities, eventAttacher.outEventSeverities[0].severities.value
        assertEquals "no modifications in severities", ds2.probabilities, eventAttacher.outEventSeverities[1].severities.value
    }
}