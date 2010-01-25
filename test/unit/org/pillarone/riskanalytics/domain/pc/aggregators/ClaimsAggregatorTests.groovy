package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.core.wiring.Transmitter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

class ClaimsAggregatorTests extends GroovyTestCase {

    void testNoGrossClaimFound() {
        Claim cededClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim cededClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        ClaimsAggregator aggregator = new ClaimsAggregator()
        aggregator.inClaimsCeded << cededClaim1
        aggregator.inClaimsCeded << cededClaim2
        shouldFail(IllegalStateException, { aggregator.doCalculation() })
    }

    void testAggregation() {
        Claim grossClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim grossClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        Claim grossClaim3 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        Claim cededClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim cededClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        ClaimsAggregator aggregator = new ClaimsAggregator()
        TestClaimsContainer container = new TestClaimsContainer()
        aggregator.allOutputTransmitter << new Transmitter(aggregator, aggregator.outClaimsNet, container, container.outClaimsNet)

        aggregator.inClaimsGross << grossClaim1
        aggregator.inClaimsGross << grossClaim2
        aggregator.inClaimsGross << grossClaim3
        aggregator.inClaimsCeded << cededClaim1
        aggregator.inClaimsCeded << cededClaim2
        aggregator.doCalculation()
        assertEquals "# packets", 1, aggregator.outClaimsNet.size()
        assertEquals "aggr net claims", 20, aggregator.outClaimsNet[0].ultimate
        assertEquals "aggr gross claims", 50, aggregator.outClaimsGross[0].ultimate
        assertEquals "aggr ceded claims", 30, aggregator.outClaimsCeded[0].ultimate
    }

    void testAggregationGross() {
        Claim grossClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim grossClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        ClaimsAggregator aggregator = new ClaimsAggregator()
        TestClaimsContainer container = new TestClaimsContainer()
        aggregator.allOutputTransmitter << new Transmitter(aggregator, aggregator.outClaimsNet, container, container.outClaimsNet)

        aggregator.inClaimsGross << grossClaim1
        aggregator.inClaimsGross << grossClaim2
        aggregator.doCalculation()
        assertEquals(1, aggregator.outClaimsNet.size())
        assertEquals(0, aggregator.outClaimsCeded.size())
        assertEquals(30, aggregator.outClaimsGross[0].ultimate)
        assertEquals(30, aggregator.outClaimsNet[0].ultimate)
    }
}