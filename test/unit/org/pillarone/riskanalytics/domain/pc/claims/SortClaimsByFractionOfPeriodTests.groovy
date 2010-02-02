package org.pillarone.riskanalytics.domain.pc.claims


class SortClaimsByFractionOfPeriodTests extends GroovyTestCase {

    Claim claim0 = new Claim(fractionOfPeriod: 0d)
    Claim claim25 = new Claim(fractionOfPeriod: 0.25)
    Claim claim50 = new Claim(fractionOfPeriod: 0.5)
    Claim claim60 = new Claim(fractionOfPeriod: 0.6)

    void testOrder() {
        SortClaimsByFractionOfPeriod sorter = SortClaimsByFractionOfPeriod.getInstance()
        assertTrue 0 == sorter.compare(claim0, claim0)
        assertTrue(-1 == sorter.compare(claim0, claim25))
        assertTrue 1 == sorter.compare(claim50, claim0)
    }

    void testSort() {
        List claims = []
        claims  << claim25 << claim50 << claim60 << claim0
        Collections.sort(claims, SortClaimsByFractionOfPeriod.getInstance())

        assertEquals claims[0], claim0
        assertEquals claims[1], claim25
        assertEquals claims[2], claim50
        assertEquals claims[3], claim60
    }
}