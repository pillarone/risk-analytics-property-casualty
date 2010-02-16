package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType

/**
 * @author shartmann (at) munichre (dot) com
 */
class LossPortfolioTransferContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.LOSSPORTFOLIOTRANSFER, ["quotaShare": 0.5, "commission": 0.0, "coveredByReinsurer": 1d]))
    }

    void testCalculateCoveredLoss() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim100 << largeClaim60

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 50, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, large ceded claim", 30, contract.outCoveredClaims[1].ultimate
    }
}