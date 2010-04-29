package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ClaimDevelopmentLeanPacketTests extends GroovyTestCase {

    static ClaimDevelopmentLeanPacket getClaimDevelopmentLeanPacket1000() {
        ClaimDevelopmentLeanPacket claim = new ClaimDevelopmentLeanPacket(
                incurred:1000,
                paid: 600,
                origin: new TestComponent(),
                event: new Event(),
                fractionOfPeriod: 0.4)
        claim.originalClaim = claim
        return claim
    }

    static ClaimDevelopmentLeanPacket getClaimDevelopmentLeanPacket800() {
        ClaimDevelopmentLeanPacket claim = new ClaimDevelopmentLeanPacket(
                incurred:800,
                paid: 700,
                origin: new TestComponent(),
                event: new Event(),
                fractionOfPeriod: 0.3)
        claim.originalClaim = claim
        return claim
    }

    static ClaimDevelopmentLeanPacket getClaimDevelopmentLeanPacket500() {
        ClaimDevelopmentLeanPacket claim = new ClaimDevelopmentLeanPacket(
                incurred:500,
                paid: 100,
                origin: new TestComponent(),
                event: new Event(),
                fractionOfPeriod: 0.2)
        claim.originalClaim = claim
        return claim
    }

    void testSet() {
        ClaimDevelopmentLeanPacket claim1 = getClaimDevelopmentLeanPacket1000()
        ClaimDevelopmentLeanPacket claim2 = new ClaimDevelopmentLeanPacket()
        claim2.set (claim1)

        assertEquals 'incurred 1000', 1000, claim2.incurred
        assertEquals 'paid 600', 600, claim2.paid
        assertEquals 'reserved 400', 400, claim2.reserved
        assertEquals 'fraction of period 0.4', 0.4, claim2.fractionOfPeriod
    }

    void testGetClaimPacket() {
        ClaimDevelopmentLeanPacket claimDev = getClaimDevelopmentLeanPacket1000()
        Claim claim = claimDev.getClaimPacket()
        assertEquals 'ultimate 1000', 1000, claim.ultimate
        claimDev.incurred = 1200
        assertEquals 'ultimate 1200', 1200, claimDev.getClaimPacket().ultimate
    }

    void testGetReserved() {
        ClaimDevelopmentLeanPacket claim = getClaimDevelopmentLeanPacket1000()
        assertEquals 'reserved 400', 400, claim.reserved
        claim.incurred = 800
        assertEquals 'reserved 200', 200, claim.reserved
    }

    void testValuesToSave() {
        Map map = getClaimDevelopmentLeanPacket1000().valuesToSave
        assertEquals 'incurred 1000', 1000, map['incurred']
        assertEquals 'paid 600', 600, map['paid']
        assertEquals 'reserved 400', 400, map['reserved']
    }

    void testPlus() {
        ClaimDevelopmentLeanPacket claim1 = getClaimDevelopmentLeanPacket1000()
        ClaimDevelopmentLeanPacket claim2 = getClaimDevelopmentLeanPacket1000()
        claim1.plus(claim2)
        assertEquals 'incurred 2000', claim2.incurred * 2, claim1.incurred
        assertEquals 'paid 1200', claim2.paid * 2, claim1.paid
        assertEquals 'reserved 800', claim2.reserved * 2, claim1.reserved
    }

    void testMinus() {
        ClaimDevelopmentLeanPacket claim1 = getClaimDevelopmentLeanPacket1000()
        ClaimDevelopmentLeanPacket claim2 = getClaimDevelopmentLeanPacket1000()
        claim1.minus(claim2)
        assertEquals 'incurred 0', 0, claim1.incurred
        assertEquals 'paid 0', 0, claim1.paid
        assertEquals 'reserved 0', 0, claim1.reserved
    }
}