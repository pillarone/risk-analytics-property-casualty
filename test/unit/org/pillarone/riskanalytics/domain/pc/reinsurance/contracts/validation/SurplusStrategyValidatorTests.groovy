package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.validation

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SurplusStrategyValidatorTests extends GroovyTestCase {

    void testGetTechnicalName() {
        assertEquals 'fire', 'subFire', SurplusStrategyValidator.getTechnicalName('fire', 'sub')
        assertEquals 'motor hull', 'subMotorHull', SurplusStrategyValidator.getTechnicalName('motor hull', 'sub')
    }
}