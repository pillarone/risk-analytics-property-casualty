package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract(double quotaShare) {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": quotaShare, "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContractAAD(double quotaShare, double aad) {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": quotaShare, "coveredByReinsurer": 1d,
                            "limit": LimitStrategyType.getStrategy(LimitStrategyType.AAD, ['aad': aad])]))
    }

    static ReinsuranceContract getContractAAL(double quotaShare, double aal) {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": quotaShare, "coveredByReinsurer": 1d,
                            "limit": LimitStrategyType.getStrategy(LimitStrategyType.AAL, ['aal': aal])]))
    }

    static ReinsuranceContract getContract(double quotaShare, double aad, double aal) {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": quotaShare, "coveredByReinsurer": 1d,
                            "limit": LimitStrategyType.getStrategy(LimitStrategyType.AALAAD, ['aal': aal, 'aad': aad])]))
    }

    static ReinsuranceContract getContractEventLimit(double quotaShare, double eventLimit) {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": quotaShare, "coveredByReinsurer": 1d,
                            "limit": LimitStrategyType.getStrategy(LimitStrategyType.EVENTLIMIT, ['eventLimit': eventLimit])]))
    }

    static ReinsuranceContract getContractEventLimitAAL(double quotaShare, double eventLimit, double aal) {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": quotaShare, "coveredByReinsurer": 1d,
                            "limit": LimitStrategyType.getStrategy(LimitStrategyType.EVENTLIMITAAL, ['eventLimit': eventLimit, 'aal': aal])]))
    }

    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContract(0.5)
        contract.inClaims << attrClaim100 << largeClaim60

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 50, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, large ceded claim", 30, contract.outCoveredClaims[1].ultimate
    }

    void testCalculateCededClaimAALAAD() {
        Claim attrClaim550 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 550d)

        ReinsuranceContract contract = getContract(0.5, 50, 200)
        contract.inClaims << attrClaim550

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 550", 200, contract.outCoveredClaims[0].ultimate

        Claim attrClaim200 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 200d)

        contract = getContract(0.5, 50, 200)
        contract.inClaims << attrClaim200

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 200", 75, contract.outCoveredClaims[0].ultimate

        Claim attrClaim30 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 30d)

        contract = getContract(0.5, 50, 200)
        contract.inClaims << attrClaim30

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 30", 0, contract.outCoveredClaims[0].ultimate
    }

    void testCalculateCededClaimAALAADTrivial() {
        Claim attrClaim500 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 500d)

        ReinsuranceContract contract = getContract(0.5, 0, 200)
        contract.inClaims << attrClaim500

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 500", 200, contract.outCoveredClaims[0].ultimate

        Claim attrClaim180 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 180d)

        contract = getContract(0.5, 0, 200)
        contract.inClaims << attrClaim180

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 180", 90, contract.outCoveredClaims[0].ultimate
    }

    void testCalculateCededClaimAALAADHigher() {
        Claim attrClaim400 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 400d)

        ReinsuranceContract contract = getContract(0.5, 200, 50)
        contract.inClaims << attrClaim400

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 400", 50, contract.outCoveredClaims[0].ultimate

        Claim attrClaim250 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 250d)

        contract = getContract(0.5, 200, 50)
        contract.inClaims << attrClaim250

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 250", 25, contract.outCoveredClaims[0].ultimate

    }

    void testCalculateCededClaimsAAD() {
        Claim attrClaim50 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim attrClaim70 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 70d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContractAAD(0.5, 100)
        contract.inClaims << attrClaim50 << attrClaim70 << largeClaim60

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 50", 0, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, attritional ceded claim 70", 10, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare1, large ceded claim", 30, contract.outCoveredClaims[2].ultimate
    }

    void testCalculateCededClaimsAAL() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, value: 60d)

        ReinsuranceContract quotaShareAAL = getContractAAL(0.5, 30)
        quotaShareAAL.inClaims << attrClaim100 << largeClaim60

        quotaShareAAL.doCalculation()

        assertEquals "outClaims.size", 2, quotaShareAAL.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 30, quotaShareAAL.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, attritional ceded claim", 0, quotaShareAAL.outCoveredClaims[1].ultimate
    }

    void testCalculateCededClaimsEventLimit() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim attrClaim800 = new Claim(claimType: ClaimType.EVENT, ultimate: 800d)
        Claim attrClaim1400a = new Claim(claimType: ClaimType.EVENT, ultimate: 1400d)
        Claim attrClaim1400b = new Claim(claimType: ClaimType.SINGLE, ultimate: 1400d)

        ReinsuranceContract contract = getContractEventLimit(0.5, 500)
        contract.inClaims << attrClaim100 << attrClaim800 << attrClaim1400a << attrClaim1400b

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 4, contract.outCoveredClaims.size()
        assertEquals "quotaShare, ceded attritional claim", 50, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare, small ceded event claim", 400, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare, large ceded event claim", 500, contract.outCoveredClaims[2].ultimate
        assertEquals "quotaShare, large ceded single claim", 700, contract.outCoveredClaims[3].ultimate
    }

    void testCalculateCededClaimsEventLimitAAL() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim attrClaim800 = new Claim(claimType: ClaimType.EVENT, ultimate: 800d)
        Claim attrClaim1400a = new Claim(claimType: ClaimType.EVENT, ultimate: 1400d)
        Claim attrClaim1400b = new Claim(claimType: ClaimType.SINGLE, ultimate: 1400d)
        Claim attrClaim1000 = new Claim(claimType: ClaimType.EVENT, ultimate: 1000d)
        Claim attrClaim200 = new Claim(claimType: ClaimType.SINGLE, ultimate: 200d)

        ReinsuranceContract contract = getContractEventLimitAAL(0.5, 500, 2000)
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
