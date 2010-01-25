package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareAADContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREAAD, ["quotaShare": 0.5, "commission": 0.0, "annualAggregateDeductible": 100d, "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaimsOnly() {
        Claim attrClaim50 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim attrClaim70 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 70d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim50 << attrClaim70 << largeClaim60

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 50", 0, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, attritional ceded claim 70", 10, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare1, large ceded claim", 30, contract.outCoveredClaims[2].ultimate
    }
}