package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareAADAALContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREAADAAL, ["quotaShare": 0.5, "commission": 0.0, "annualAggregateDeductible": 50d, "annualAggregateLimit": 200d, "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaim0() {
        Claim attrClaim550 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 550d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim550

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 550", 200, contract.outCoveredClaims[0].ultimate

        Claim attrClaim200 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 200d)

        contract = getContract0()
        contract.inClaims << attrClaim200

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 200", 75, contract.outCoveredClaims[0].ultimate

        Claim attrClaim30 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 30d)

        contract = getContract0()
        contract.inClaims << attrClaim30

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 30", 0, contract.outCoveredClaims[0].ultimate
    }

    static ReinsuranceContract getContract1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREAADAAL, ["quotaShare": 0.5, "commission": 0.0, "annualAggregateDeductible": 0d, "annualAggregateLimit": 200d, "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaim1() {
        Claim attrClaim500 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 500d)

        ReinsuranceContract contract = getContract1()
        contract.inClaims << attrClaim500

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 500", 200, contract.outCoveredClaims[0].ultimate

        Claim attrClaim180 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 180d)

        contract = getContract1()
        contract.inClaims << attrClaim180

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 180", 90, contract.outCoveredClaims[0].ultimate
    }

    static ReinsuranceContract getContract2() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREAADAAL, ["quotaShare": 0.5, "commission": 0.0, "annualAggregateDeductible": 200d, "annualAggregateLimit": 50d, "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaim2() {
        Claim attrClaim400 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 400d)

        ReinsuranceContract contract = getContract2()
        contract.inClaims << attrClaim400

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 400", 50, contract.outCoveredClaims[0].ultimate

        Claim attrClaim250 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 250d)

        contract = getContract2()
        contract.inClaims << attrClaim250

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 250", 25, contract.outCoveredClaims[0].ultimate
    }
}
