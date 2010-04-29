package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ClaimsConverterTests extends GroovyTestCase{
    void testUsage() {
        TestComponent origin = new TestComponent()
        Claim claim = new Claim()
        Event event = new Event()


        ClaimsConverter claimsConverter = new ClaimsConverter()
        claimsConverter.inClaims << new ClaimDevelopmentLeanPacket(
                incurred:10,
                paid: 6,
                origin: origin,
                originalClaim: claim,
                event: event,
                fractionOfPeriod: 0.5)

        claimsConverter.doCalculation()

        assertEquals "# packets", claimsConverter.inClaims.size() , claimsConverter.outClaims.size()
        assertEquals "ultimate", 10, claimsConverter.outClaims[0].ultimate
        assertEquals "origin", origin, claimsConverter.outClaims[0].origin
        assertEquals "original claim", claim, claimsConverter.outClaims[0].originalClaim
        assertEquals "event", event, claimsConverter.outClaims[0].event
        assertEquals "fraction of period", 0.5, claimsConverter.outClaims[0].fractionOfPeriod
    }
}