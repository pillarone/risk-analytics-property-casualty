package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CoverDurationTests extends GroovyTestCase {

    void testUsage() {
        CoverDuration coverDuration = new CoverDuration(0.24, 0.6)
        assertFalse '0.2 is not in [0.24..0.6]', coverDuration.isCovered(0.2)
        assertTrue '0.24 is in [0.24..0.6]', coverDuration.isCovered(0.24)
        assertTrue '0.3 is in [0.24..0.6]', coverDuration.isCovered(0.3)
        assertTrue '0.6 is in [0.24..0.6]', coverDuration.isCovered(0.6)
        assertFalse '0.61 is in [0.24..0.6]', coverDuration.isCovered(0.61)
    }
}
