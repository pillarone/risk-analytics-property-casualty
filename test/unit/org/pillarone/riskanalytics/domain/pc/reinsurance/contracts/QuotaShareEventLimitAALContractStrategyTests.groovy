package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareEventLimitAALContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREEVENTLIMITAAL, ["quotaShare": 0.5, "eventLimit": 500, "annualAggregateLimit": 2000, "commission": 0.0, "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaims() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim attrClaim800 = new Claim(claimType: ClaimType.EVENT, ultimate: 800d)
        Claim attrClaim1400a = new Claim(claimType: ClaimType.EVENT, ultimate: 1400d)
        Claim attrClaim1400b = new Claim(claimType: ClaimType.SINGLE, ultimate: 1400d)
        Claim attrClaim1000 = new Claim(claimType: ClaimType.EVENT, ultimate: 1000d)
        Claim attrClaim200 = new Claim(claimType: ClaimType.SINGLE, ultimate: 200d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim100 << attrClaim800 << attrClaim1400a << attrClaim1400b << attrClaim1000 << attrClaim200

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 6, contract.outCoveredClaims.size()
        assertEquals "quotaShare, ceded attritional claim", 50, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare, small ceded event claim", 400, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare, large ceded event claim", 500, contract.outCoveredClaims[2].ultimate
        assertEquals "quotaShare, large ceded single claim", 700, contract.outCoveredClaims[3].ultimate
        assertEquals "quotaShare, large ceded event claim", 350, contract.outCoveredClaims[4].ultimate
        assertEquals "quotaShare, small ceded single claim", 0, contract.outCoveredClaims[5].ultimate
    }
}