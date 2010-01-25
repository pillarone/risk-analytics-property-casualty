package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareEventLimitContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREEVENTLIMIT, ["quotaShare": 0.5, "eventLimit": 500, "commission": 0.0, "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim attrClaim800 = new Claim(claimType: ClaimType.EVENT, ultimate: 800d)
        Claim attrClaim1400a = new Claim(claimType: ClaimType.EVENT, ultimate: 1400d)
        Claim attrClaim1400b = new Claim(claimType: ClaimType.SINGLE, ultimate: 1400d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim100 << attrClaim800 << attrClaim1400a << attrClaim1400b

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 4, contract.outCoveredClaims.size()
        assertEquals "quotaShare, ceded attritional claim", 50, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare, small ceded event claim", 400, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare, large ceded event claim", 500, contract.outCoveredClaims[2].ultimate
        assertEquals "quotaShare, large ceded single claim", 700, contract.outCoveredClaims[3].ultimate
    }
}