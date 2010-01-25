package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ClaimUtilitiesTests extends GroovyTestCase {

  void testMergeClaimsWithEqualOriginalClaim() {
        Claim claim0 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim claim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10, originalClaim: claim0)
        Claim claim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20, originalClaim: claim0)
        Claim claim3 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10, originalClaim: claim0)
        Claim claim4 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        List claims = new ArrayList()
        claims << claim0 << claim1 << claim2 << claim3 << claim4
        List mergedClaims = ClaimUtilities.mergeClaimsWithEqualOriginalClaim(claims, null)
        assertEquals("list size", 3, mergedClaims.size())
        assertEquals("10", 10, mergedClaims[0].ultimate)
        assertEquals("40", 40, mergedClaims[1].ultimate)
        assertEquals("20", 20, mergedClaims[2].ultimate)
        assertEquals("original claim0", mergedClaims[1].originalClaim, claim0)
    }

}