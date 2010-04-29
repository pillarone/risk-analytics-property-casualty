package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getQuotaShareContract(double quotaShare, Map params = [:], double coveredByReinsurer = 1d) {
        boolean isEL = params.containsKey('eventLimit')
        boolean isAAD = params.containsKey('aad')
        boolean isAAL = params.containsKey('aal')
        LimitStrategyType type = isEL ? (isAAL ? LimitStrategyType.EVENTLIMITAAL : LimitStrategyType.EVENTLIMIT) :
                                isAAD ? (isAAL ? LimitStrategyType.AALAAD : LimitStrategyType.AAD) :
                                        (isAAL ? LimitStrategyType.AAL : LimitStrategyType.NONE)
        return new ReinsuranceContract(
            parmContractStrategy: ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.QUOTASHARE, [
                    "quotaShare": quotaShare,
                    "coveredByReinsurer": coveredByReinsurer,
                    "limit": LimitStrategyType.getStrategy(type, params)]))
    }

    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getQuotaShareContract(0.5, [:], 0.1)
        contract.inClaims << attrClaim100 << largeClaim60

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 5, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, large ceded claim", 3, contract.outCoveredClaims[1].ultimate
    }

    void testCalculateCededClaimAALAAD() {
        ReinsuranceContract contract = getQuotaShareContract(0.5, ['aad': 50, 'aal': 200], 0.1)
        contract.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 550d)
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 550", 20, contract.outCoveredClaims[0].ultimate

        contract = getQuotaShareContract(0.5, ['aad': 50, 'aal': 200], 0.1)
        contract.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 200d)
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 200", 7.5, contract.outCoveredClaims[0].ultimate

        contract = getQuotaShareContract(0.5, ['aad': 50, 'aal': 200], 0.1)
        contract.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 30d)
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 30", 0, contract.outCoveredClaims[0].ultimate
    }

    void testCalculateCededClaimAALAADTrivial() {
        ReinsuranceContract contract = getQuotaShareContract(0.5, ['aad': 0, 'aal': 200], 0.1)
        contract.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 500d)
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 500", 20, contract.outCoveredClaims[0].ultimate

        contract = getQuotaShareContract(0.5, ['aad': 0, 'aal': 200], 0.1)
        contract.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 180d)
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 180", 9, contract.outCoveredClaims[0].ultimate
    }

    void testCalculateCededClaimAALAADHigher() {
        ReinsuranceContract contract = getQuotaShareContract(0.5, ['aad': 200, 'aal': 50], 0.1)
        contract.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 400d)
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 400", 5, contract.outCoveredClaims[0].ultimate

        contract = getQuotaShareContract(0.5, ['aad': 200, 'aal': 50], 0.1)
        contract.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 250d)
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 1, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 250", 2.5, contract.outCoveredClaims[0].ultimate

    }

    void testCalculateCededClaimsAAD() {
        Claim attrClaim50 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim attrClaim70 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 70d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getQuotaShareContract(0.5, ['aad': 100], 0.1)
        contract.inClaims << attrClaim50 << attrClaim70 << largeClaim60

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim 50", 0, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, attritional ceded claim 70", 1, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare1, large ceded claim", 3, contract.outCoveredClaims[2].ultimate
    }

    void testCalculateCededClaimsAAL() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, value: 60d)

        ReinsuranceContract quotaShareAAL = getQuotaShareContract(0.5, ['aal': 30], 0.1)
        quotaShareAAL.inClaims << attrClaim100 << largeClaim60

        quotaShareAAL.doCalculation()

        assertEquals "outClaims.size", 2, quotaShareAAL.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 3, quotaShareAAL.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, attritional ceded claim", 0, quotaShareAAL.outCoveredClaims[1].ultimate
    }

    void testCalculateCededClaimsEventLimit() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim eventClaim800 = new Claim(claimType: ClaimType.EVENT, ultimate: 800d)
        Claim eventClaim1400a = new Claim(claimType: ClaimType.EVENT, ultimate: 1400d)
        Claim largeClaim1400b = new Claim(claimType: ClaimType.SINGLE, ultimate: 1400d)

        ReinsuranceContract contract = getQuotaShareContract(0.5, ['eventLimit': 500], 0.1)
        contract.inClaims << attrClaim100 << eventClaim800 << eventClaim1400a << largeClaim1400b

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 4, contract.outCoveredClaims.size()
        assertEquals "quotaShare, ceded attritional claim", 5, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare, small ceded event claim", 40, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare, large ceded event claim", 50, contract.outCoveredClaims[2].ultimate
        assertEquals "quotaShare, large ceded single claim", 70, contract.outCoveredClaims[3].ultimate
    }

    void testCalculateCededClaimsEventLimitAAL() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim eventClaim800 = new Claim(claimType: ClaimType.EVENT, ultimate: 800d)
        Claim eventClaim1400a = new Claim(claimType: ClaimType.EVENT, ultimate: 1400d)
        Claim largeClaim1400b = new Claim(claimType: ClaimType.SINGLE, ultimate: 1400d)
        Claim eventClaim1000 = new Claim(claimType: ClaimType.EVENT, ultimate: 1000d)
        Claim largeClaim200 = new Claim(claimType: ClaimType.SINGLE, ultimate: 200d)

        ReinsuranceContract contract = getQuotaShareContract(0.5, ['eventLimit': 500, 'aal': 2000], 0.1)
        contract.inClaims << attrClaim100 << eventClaim800 << eventClaim1400a << largeClaim1400b << eventClaim1000 << largeClaim200

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 6, contract.outCoveredClaims.size()
        assertEquals "quotaShare, ceded attritional claim", 5, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare, small ceded event claim", 40, contract.outCoveredClaims[1].ultimate
        assertEquals "quotaShare, large ceded event claim", 50, contract.outCoveredClaims[2].ultimate
        assertEquals "quotaShare, large ceded single claim", 70, contract.outCoveredClaims[3].ultimate
        assertEquals "quotaShare, large ceded event claim", 35, contract.outCoveredClaims[4].ultimate
        assertEquals "quotaShare, small ceded single claim", 0, contract.outCoveredClaims[5].ultimate
    }
}
