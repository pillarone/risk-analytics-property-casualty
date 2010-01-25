package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareProfitCommissionContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREPROFITCOMMISSION, ["quotaShare": 0.5, "commission": 0.1, "coveredByReinsurer": 1d,
                                                             "expensesOfReinsurer": 0.05, "profitCommission": 0.2]))
    }

    void testCalculateCommissionOnly() {
        Claim attrClaim40 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 40d)
        Claim singleClaim60 = new Claim(claimType: ClaimType.SINGLE, value: 60d)
        Claim singleClaim80 = new Claim(claimType: ClaimType.SINGLE, value: 80d)

        UnderwritingInfo premium = new UnderwritingInfo(premiumWritten: 200d)

        ReinsuranceContract contract = getContract0()
        contract.inUnderwritingInfo << premium

        contract.inClaims << attrClaim40 << singleClaim60

        def probe = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "premium quotaShare", 100, contract.outCoverUnderwritingInfo[0].premiumWritten
        assertEquals "commission quotaShare", 17, contract.outCoverUnderwritingInfo[0].commission

        contract = getContract0()
        probe = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims
        contract.inUnderwritingInfo << premium

        contract.inClaims << attrClaim40 << singleClaim60
        contract.inClaims << singleClaim80

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "premium quotaShare", 100, contract.outCoverUnderwritingInfo[0].premiumWritten
        assertEquals "commission quotaShare", 10, contract.outCoverUnderwritingInfo[0].commission
    }
}