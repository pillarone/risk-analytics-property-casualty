package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacketTests

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ClaimDevelopmentLeanAggregatorTests extends GroovyTestCase {

    void testUsage() {
        ClaimDevelopmentLeanAggregator aggregator = new ClaimDevelopmentLeanAggregator()
        aggregator.inClaimsGross << ClaimDevelopmentLeanPacketTests.getClaimDevelopmentLeanPacket1000()
        aggregator.inClaimsGross << ClaimDevelopmentLeanPacketTests.getClaimDevelopmentLeanPacket500()
        aggregator.inClaimsCeded << ClaimDevelopmentLeanPacketTests.getClaimDevelopmentLeanPacket800()

        def probeGross = new TestProbe(aggregator, "outClaimsGross")    // needed in order to trigger the calculation of gross claims
        def probeCeded = new TestProbe(aggregator, "outClaimsCeded")    // needed in order to trigger the calculation of ceded claims
        def probeNet = new TestProbe(aggregator, "outClaimsNet")    // needed in order to trigger the calculation of net claims
        aggregator.doCalculation()

        assertEquals 'gross incurred 1000', 1500, aggregator.outClaimsGross[0].incurred
        assertEquals 'gross paid 600', 700, aggregator.outClaimsGross[0].paid
        assertEquals 'gross reserved 400', 800, aggregator.outClaimsGross[0].reserved
        assertEquals 'gross claim type', ClaimType.AGGREGATED,  aggregator.outClaimsGross[0].claimType

        assertEquals 'ceded incurred 800', 800, aggregator.outClaimsCeded[0].incurred
        assertEquals 'ceded paid 700', 700, aggregator.outClaimsCeded[0].paid
        assertEquals 'ceded reserved 100', 100, aggregator.outClaimsCeded[0].reserved
        assertEquals 'ceded claim type', ClaimType.AGGREGATED,  aggregator.outClaimsCeded[0].claimType

        assertEquals 'net incurred 200', 700, aggregator.outClaimsNet[0].incurred
        assertEquals 'net paid 100', 0, aggregator.outClaimsNet[0].paid
        assertEquals 'net reserved 100', 700, aggregator.outClaimsNet[0].reserved
        assertEquals 'net claim type', ClaimType.AGGREGATED,  aggregator.outClaimsNet[0].claimType
    }

    void testAggregateGross() {
        ClaimDevelopmentLeanAggregator aggregator = new ClaimDevelopmentLeanAggregator()
        aggregator.inClaimsGross << ClaimDevelopmentLeanPacketTests.getClaimDevelopmentLeanPacket1000()
        aggregator.inClaimsGross << ClaimDevelopmentLeanPacketTests.getClaimDevelopmentLeanPacket800()

        def probe = new TestProbe(aggregator, "outClaimsGross")    // needed in order to trigger the calculation of gross claims
        aggregator.doCalculation()

        assertEquals 'incurred 1800', 1800, aggregator.outClaimsGross[0].incurred
        assertEquals 'paid 1300', 1300, aggregator.outClaimsGross[0].paid
        assertEquals 'reserved 500', 500, aggregator.outClaimsGross[0].reserved

        assertEquals 'no ceded output', 0, aggregator.outClaimsCeded.size()
        assertEquals 'no net output', 0, aggregator.outClaimsNet.size()
    }

    void testCededOnly() {
        ClaimDevelopmentLeanAggregator aggregator = new ClaimDevelopmentLeanAggregator()
        aggregator.inClaimsCeded << ClaimDevelopmentLeanPacketTests.getClaimDevelopmentLeanPacket800()

        shouldFail(IllegalStateException, { aggregator.doCalculation() })
    }

}