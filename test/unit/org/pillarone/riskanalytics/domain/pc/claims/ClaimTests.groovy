package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.utils.UnitTestUtilities

class ClaimTests extends GroovyTestCase {

    void testCopyConstructor() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)
        Claim copyAttrClaim100 = attrClaim100.copy()
        UnitTestUtilities.allPropertiesCloned copyAttrClaim100, attrClaim100
    }
}