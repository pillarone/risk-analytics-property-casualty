package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity
import org.pillarone.riskanalytics.domain.pc.severities.SeverityExtractor

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SeverityExtractorTests extends GroovyTestCase {

    void testExtraction() {
        SeverityExtractor extractor = new SeverityExtractor(parmFilterCriteria: 'Fire')
        DependenceStream stream0 = new DependenceStream([0.0, 0.1, 0.3], ['Accident', 'Fire', 'Hull'])
        DependenceStream stream1 = new DependenceStream([0.0, 0.15, 0.3], ['Accident', 'Fire', 'Hull'])
        Severity severity0 = new Severity(0.1)
        Severity severity1 = new Severity(0.15)
        extractor.inSeverities << stream0 << stream1
        extractor.doCalculation()

        assertEquals "packet[0] content", severity0.value, extractor.outSeverities[0].value
        assertEquals "packet[1] content", severity1.value, extractor.outSeverities[1].value
    }
}