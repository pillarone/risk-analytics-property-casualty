package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 *
 */
class ReinsuranceContractTests extends GroovyTestCase {

    ReinsuranceContract quotaShare1
    ReinsuranceContract quotaShare2

    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract quotaShare1 = QuotaShareContractStrategyTests.getContract(0.5)

        quotaShare1.inClaims << attrClaim100 << largeClaim60

        quotaShare1.doCalculation()

        assertEquals "outClaimsNet.size", 0, quotaShare1.outUncoveredClaims.size()   // if the net port is not wired no net claims will be calculated
        assertEquals "outClaims.size", 2, quotaShare1.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 50, quotaShare1.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, large ceded claim", 30, quotaShare1.outCoveredClaims[1].ultimate
    }

    void testUsage() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        quotaShare1 = QuotaShareContractStrategyTests.getContract(0.5)
        quotaShare2 = QuotaShareContractStrategyTests.getContractAAL(0.5, 30)

        quotaShare1.inClaims << attrClaim100 << largeClaim60

        WiringUtils.use(WireCategory) {
            quotaShare2.inClaims = quotaShare1.outUncoveredClaims
        }

        def probeQS1net = new TestProbe(quotaShare1, "outUncoveredClaims")
        List qs1ClaimsNet = probeQS1net.result

        def probeQS2net = new TestProbe(quotaShare2, "outUncoveredClaims")
        List qs2ClaimsNet = probeQS2net.result

        def probeQS1ceded = new TestProbe(quotaShare1, "outCoveredClaims")
        List qs1ClaimsCeded = probeQS1ceded.result

        def probeQS2ceded = new TestProbe(quotaShare2, "outCoveredClaims")
        List qs2ClaimsCeded = probeQS2ceded.result

        quotaShare1.start()

        assertEquals "qs1ClaimsNet.size", 2, qs1ClaimsNet.size()
        assertEquals "qs2ClaimsNet.size", 2, qs2ClaimsNet.size()
        assertEquals "qs2ClaimsCeded.size", 2, qs2ClaimsCeded.size()
        assertEquals "quotaShare1, attritional net claim", 50, qs1ClaimsNet[0].ultimate
        assertEquals "quotaShare1, large net claim", 30, qs1ClaimsNet[1].ultimate
        assertEquals "quotaShare2, attritional net claim", 25, qs2ClaimsNet[0].ultimate
        assertEquals "quotaShare2, large net claim", 25, qs2ClaimsNet[1].ultimate
        assertEquals "quotaShare1, attritional ceded claim", 50, qs1ClaimsCeded[0].ultimate
        assertEquals "quotaShare1, large ceded claim", 30, qs1ClaimsCeded[1].ultimate
        assertEquals "quotaShare2, attritional ceded claim", 25, qs2ClaimsCeded[0].ultimate
        assertEquals "quotaShare2, large ceded claim", 5, qs2ClaimsCeded[1].ultimate
    }

    void testNoContractSet() {
        ReinsuranceContract quotaShare1 = new ReinsuranceContract(parmContractStrategy: null)
        shouldFail(IllegalStateException, {quotaShare1.doCalculation()})
    }
}