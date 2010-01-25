package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareAALContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREAAL,
                        ["quotaShare": 0.5,
                         "commission": 0.0,
                         "annualAggregateLimit": 30,
                         "coveredByReinsurer": 1d]))
    }


    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, value: 60d)

        ReinsuranceContract quotaShareAAL = getContract0()
        quotaShareAAL.inClaims << attrClaim100 << largeClaim60

        quotaShareAAL.doCalculation()

        assertEquals "outClaims.size", 2, quotaShareAAL.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 30, quotaShareAAL.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, attritional ceded claim", 0, quotaShareAAL.outCoveredClaims[1].ultimate
    }
}