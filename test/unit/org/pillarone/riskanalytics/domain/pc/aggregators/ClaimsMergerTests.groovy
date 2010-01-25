package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.Transmitter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

class ClaimsMergerTests extends GroovyTestCase {

    void testGrossClaimUnmodified() {
        Claim grossClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim grossClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        ClaimsMerger aggregator = new ClaimsMerger()
        TestClaimsContainer container = new TestClaimsContainer()
        aggregator.allOutputTransmitter << new Transmitter(aggregator, aggregator.outClaimsGross, container, container.outClaimsGross)

        aggregator.inClaimsGross << grossClaim1
        aggregator.inClaimsGross << grossClaim2
        aggregator.doCalculation()
        assertEquals(2, aggregator.outClaimsGross.size())
        assertSame(grossClaim1, aggregator.outClaimsGross[0])
        assertSame(grossClaim2, aggregator.outClaimsGross[1])
    }

    void testNoGrossClaimFound() {
        Claim cededClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim cededClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        ClaimsMerger aggregator = new ClaimsMerger()
        aggregator.inClaimsCeded << cededClaim1
        aggregator.inClaimsCeded << cededClaim2
        shouldFail(IllegalStateException, { aggregator.doCalculation() })
    }

    void testMerge() {
        Claim grossClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10)
        Claim grossClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20)
        Claim cededClaim1 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 10, originalClaim: grossClaim1)
        Claim cededClaim2 = new Claim(origin: null, claimType: ClaimType.SINGLE, ultimate: 20, originalClaim: grossClaim2)
        ClaimsMerger aggregator = new ClaimsMerger()
        TestClaimsContainer container = new TestClaimsContainer()
        aggregator.allOutputTransmitter << new Transmitter(aggregator, aggregator.outClaimsNet, container, container.outClaimsNet)

        aggregator.inClaimsGross << grossClaim1
        aggregator.inClaimsGross << grossClaim2
        aggregator.inClaimsCeded << cededClaim1
        aggregator.inClaimsCeded << cededClaim2
        aggregator.doCalculation()
        assertEquals(2, aggregator.outClaimsNet.size())
        assertEquals("outClaimNet[0]", 0, aggregator.outClaimsNet[0].ultimate)
        assertEquals("outClaimNet[1]", 0, aggregator.outClaimsNet[1].ultimate)
    }
}

class TestClaimsContainer extends Component {
    PacketList<Claim> outClaimsCeded = new PacketList(Claim)
    PacketList<Claim> outClaimsGross = new PacketList(Claim)
    PacketList<Claim> outClaimsNet = new PacketList(Claim)

    public void doCalculation() {
    }
}