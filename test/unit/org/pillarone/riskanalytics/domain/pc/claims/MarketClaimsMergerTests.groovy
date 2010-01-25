package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.core.util.TestProbe

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MarketClaimsMergerTests extends GroovyTestCase {

    ClaimDevelopmentLeanPacket marketClaim1000
    ClaimDevelopmentLeanPacket marketClaim900
    ClaimDevelopmentLeanPacket marketClaim700
    ClaimDevelopmentLeanPacket grossClaim1000
    ClaimDevelopmentLeanPacket grossClaim900
    ClaimDevelopmentLeanPacket grossClaim700
    ClaimDevelopmentLeanPacket cededClaim1000
    ClaimDevelopmentLeanPacket cededClaim900A
    ClaimDevelopmentLeanPacket cededClaim900B
    ClaimDevelopmentLeanPacket cededClaim700

    void setUp() {
        marketClaim1000 = new ClaimDevelopmentLeanPacket(
                incurred:1000,
                paid: 600,
                fractionOfPeriod: 0.5
        )
        marketClaim900 = new ClaimDevelopmentLeanPacket(
                incurred:900,
                paid: 650,
                fractionOfPeriod: 0.4
        )
        marketClaim700 = new ClaimDevelopmentLeanPacket(
                incurred:700,
                paid: 660,
                fractionOfPeriod: 0.25
        )
        grossClaim1000 = new ClaimDevelopmentLeanPacket(
                incurred:500,
                paid: 300,
                fractionOfPeriod: 0.5,
                originalClaim: marketClaim1000
        )
        grossClaim900 = new ClaimDevelopmentLeanPacket(
                incurred:600,
                paid: 400,
                fractionOfPeriod: 0.4,
                originalClaim: marketClaim900
        )
        grossClaim700 = new ClaimDevelopmentLeanPacket(
                incurred:500,
                paid: 40,
                fractionOfPeriod: 0.25,
                originalClaim: marketClaim700
        )
        cededClaim1000 = new ClaimDevelopmentLeanPacket(
                incurred:200,
                paid: 100,
                fractionOfPeriod: 0.5,
                originalClaim: marketClaim1000
        )
        cededClaim900A = new ClaimDevelopmentLeanPacket(
                incurred:50,
                paid: 10,
                fractionOfPeriod: 0.4,
                originalClaim: marketClaim900
        )
        cededClaim900B = new ClaimDevelopmentLeanPacket(
                incurred:300,
                paid: 300,
                fractionOfPeriod: 0.4,
                originalClaim: marketClaim900
        )
    }

    void testUsage() {
        MarketClaimsMerger merger = new MarketClaimsMerger()
        merger.inClaimsGross << grossClaim1000 << grossClaim900 << grossClaim700
        merger.inClaimsCeded << cededClaim1000 << cededClaim900A << cededClaim900B

        def probe = new TestProbe(merger, "outClaimsCeded")    // needed in order to trigger the calculation of ceded and net claims
        merger.doCalculation()

        assertEquals "# gross claims", merger.inClaimsGross.size(), merger.outClaimsGross.size()
        assertEquals "# ceded claims", merger.inClaimsGross.size(), merger.outClaimsCeded.size()
        assertEquals "# net claims", merger.inClaimsGross.size(), merger.outClaimsNet.size()

        // no gross claim should get lost and the order should be kept
        merger.inClaimsGross.eachWithIndex { claim, idx ->
            assertEquals "gross claim mismatch $idx", claim, merger.outClaimsGross[idx]
        }

        assertEquals "cededClaim1000 same, incurred", cededClaim1000.incurred, ((ClaimDevelopmentLeanPacket) merger.outClaimsCeded[0]).incurred
        assertEquals "cededClaim1000 same, paid", cededClaim1000.paid, merger.outClaimsCeded[0].paid
        assertEquals "cededClaim900 merged, incurred", cededClaim900A.incurred + cededClaim900B.incurred, merger.outClaimsCeded[1].incurred
        assertEquals "cededClaim900 merged, paid", cededClaim900A.paid + cededClaim900B.paid, merger.outClaimsCeded[1].paid
        assertEquals "cededClaim700 null, incurred", 0, merger.outClaimsCeded[2].incurred
        assertEquals "cededClaim700 null, paid", 0, merger.outClaimsCeded[2].paid
        assertEquals "cededClaim700 null, originalClaim", marketClaim700, merger.outClaimsCeded[2].originalClaim

        merger.outClaimsNet.eachWithIndex { claimNet, idx ->
            assertEquals "net claim $idx incurred", merger.outClaimsGross[idx].incurred - merger.outClaimsCeded[idx].incurred, claimNet.incurred
            assertEquals "net claim $idx paid", merger.outClaimsGross[idx].paid - merger.outClaimsCeded[idx].paid, claimNet.paid
            assertEquals "net claim $idx reserved", merger.outClaimsGross[idx].reserved - merger.outClaimsCeded[idx].reserved, claimNet.reserved
            assertEquals "net claim $idx fractionOfPeriod", merger.outClaimsGross[idx].fractionOfPeriod, claimNet.fractionOfPeriod
            assertEquals "net claim $idx originalClaim", merger.outClaimsGross[idx].originalClaim, claimNet.originalClaim
        }
    }

    void testMultipleGrossClaimsWithSameOriginalClaim() {
        MarketClaimsMerger merger = new MarketClaimsMerger()
        merger.inClaimsGross << grossClaim1000 << grossClaim1000
        def probe = new TestProbe(merger, "outClaimsGross")    // needed in order to trigger the calculation of ceded and net claims

        shouldFail(IllegalArgumentException, { merger.doCalculation() })
    }
}