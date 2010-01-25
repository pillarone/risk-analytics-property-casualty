package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author Michael-Noe (at) web (dot) de
 */
class ClaimExtractorTests extends GroovyTestCase {

    private static PacketList<ClaimWithExposure> getClaimWithExposureData(int n) {
        // prepare mock list of exposure info
        PacketList<ClaimWithExposure> claims = []
        for (int i = 1; i <= n; ++i) {
            if (i % 2 == 0) {
                claims << new ClaimWithExposure(value: 100d * i, claimType: ClaimType.SINGLE)
            }
            else if (i % 3 == 0) {
                claims << new ClaimWithExposure(value: 100d * i, claimType: ClaimType.ATTRITIONAL)
            }
            else {
                claims << new ClaimWithExposure(value: 100d * i, claimType: ClaimType.EVENT)
            }
        }
        claims
    }


    void testDoCalculation() {
        PacketList<ClaimWithExposure> inClaims = []
        int n = 10
        inClaims = getClaimWithExposureData(n)
        ClaimExtractor extractor = new ClaimExtractor(inClaims: inClaims)
        extractor.doCalculation()

        assertEquals(extractor.outClaims.size(), n)
        assertEquals(extractor.outClaimsAttritional.size(), 2)
        assertEquals(extractor.outClaimsSingle.size(), 5)
    }
}