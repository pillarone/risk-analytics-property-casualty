package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author Michael-Noe (at) web (dot) de
 */
class ClaimExtractorTests extends GroovyTestCase {

    // prepare mock list of exposure info
    private static PacketList<Claim> getClaimData(int n) {
        PacketList<Claim> claims = []
        for (int i = 1; i <= n; i++) {
            Claim claim = new Claim()
            claim.value = 100d * i
            claim.claimType =
                (i % 2 == 0) ? ClaimType.SINGLE : // 0, 2 & 4 (mod 6); avg freq = 1/2
                (i % 3 == 0) ? ClaimType.ATTRITIONAL : // 3 (mod 6); avg freq = 1/6
                               ClaimType.EVENT
            claims << claim
        }
        claims
    }


    void testDoCalculation() {
        PacketList<Claim> inClaims = []
        int n = 10
        inClaims = getClaimData(n)
        ClaimExtractor extractor = new ClaimExtractor(inClaims: inClaims)
        extractor.doCalculation()

        assertEquals(extractor.outClaims.size(), n)
        assertEquals(extractor.outClaimsAttritional.size(), Math.floor((n+3)/6))
        assertEquals(extractor.outClaimsSingle.size(), Math.floor(n/2))
    }
}