package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SortClaimDevelopmentPacketTests extends GroovyTestCase {

    Claim claim0 = new Claim(fractionOfPeriod: 0d)
    Claim claim25 = new Claim(fractionOfPeriod: 0.25)
    Claim claim50 = new Claim(fractionOfPeriod: 0.5)
    Claim claim60 = new Claim(fractionOfPeriod: 0.6)

    ClaimDevelopmentPacket claimA = new ClaimDevelopmentPacket(fractionOfPeriod: 0d, originalPeriod: 1d)
    ClaimDevelopmentPacket claimB = new ClaimDevelopmentPacket(fractionOfPeriod: 0.25d, originalPeriod: 1d)
    ClaimDevelopmentPacket claimC = new ClaimDevelopmentPacket(fractionOfPeriod: 0d, originalPeriod: 0d)
    ClaimDevelopmentPacket claimD = new ClaimDevelopmentPacket(fractionOfPeriod: 0.7d, originalPeriod: 0d)

    void testOrderClaim() {
        SortClaimDevelopmentPacket sorter = SortClaimDevelopmentPacket.getInstance()
        assertTrue 0 == sorter.compare(claim0, claim0)
        assertTrue(-1 == sorter.compare(claim0, claim25))
        assertTrue 1 == sorter.compare(claim50, claim0)
    }

    void testSortClaim() {
        List claims = []
        claims  << claim25 << claim50 << claim60 << claim0
        Collections.sort(claims, SortClaimDevelopmentPacket.getInstance())

        assertEquals claims[0], claim0
        assertEquals claims[1], claim25
        assertEquals claims[2], claim50
        assertEquals claims[3], claim60
    }

    void testOrderClaimDevelopmentPacket() {
        SortClaimDevelopmentPacket sorter = SortClaimDevelopmentPacket.getInstance()
        assertTrue '0 for equal properties', 0 == sorter.compare(claimA, claimA)
        assertTrue 'claimC before claimA', -1 == sorter.compare(claimC, claimA)
        assertTrue 'claimB after claimC', 1 == sorter.compare(claimB, claimC)
    }

    void testSortClaimDevelopmentPacket() {
        List claims = []
        claims << claimA << claimB << claimC << claimD
        Collections.sort(claims, SortClaimDevelopmentPacket.getInstance())

        assertEquals 'claimC', claimC, claims[0]
        assertEquals 'claimD', claimD, claims[1]
        assertEquals 'claimA', claimA, claims[2]
        assertEquals 'claimB', claimB, claims[3]
    }
}
