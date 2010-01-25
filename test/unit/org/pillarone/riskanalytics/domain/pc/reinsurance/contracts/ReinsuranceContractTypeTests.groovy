package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

class ReinsuranceContractTypeTests extends GroovyTestCase {

    void testToString() {
        assertEquals "quota share", ReinsuranceContractType.QUOTASHARE.toString()
    }

    void testValueOf() {
        assertSame(ReinsuranceContractType.QUOTASHARE, ReinsuranceContractType.valueOf("quota share"))
    }
}