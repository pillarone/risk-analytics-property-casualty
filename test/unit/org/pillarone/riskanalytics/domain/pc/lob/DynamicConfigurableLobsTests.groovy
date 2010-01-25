package org.pillarone.riskanalytics.domain.pc.lob

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DynamicConfigurableLobsTests extends GroovyTestCase {

    void testAddComponent() {
        DynamicConfigurableLobs lobs = new DynamicConfigurableLobs()
        ConfigurableLob lob = lobs.createDefaultSubComponent()
        lob.name = "subLob"
        lobs.addSubComponent lob

        assertNotNull lob.subClaimsAggregator.name
        assertNotNull lob.subClaimsFilter.name
        assertNotNull lob.subClaimsFilterCeded.name
        assertNotNull lob.subUnderwritingFilter.name
        assertNotNull lob.subUnderwritingInfoAggregator.name
        assertNotNull lob.subUnderwritingInfoFilterCeded.name
    }
}
