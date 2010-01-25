package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TrivialContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    }

    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim50 = new Claim(claimType: ClaimType.SINGLE, ultimate: 50d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)
        Claim largeClaim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim100 << largeClaim50 << largeClaim60 << largeClaim100 << largeClaim100

        List contractNetClaims = new TestProbe(contract, "outUncoveredClaims").result

        contract.doCalculation()

        assertEquals "outClaimsNet.size()", 5, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size()", 5, contract.outCoveredClaims.size()
        assertEquals "attrClaim100", attrClaim100.ultimate, contract.outUncoveredClaims[0].ultimate
        assertEquals "largeClaim50", largeClaim50.ultimate, contract.outUncoveredClaims[1].ultimate
        assertEquals "largeClaim60", largeClaim60.ultimate, contract.outUncoveredClaims[2].ultimate
        assertEquals "largeClaim100", largeClaim100.ultimate, contract.outUncoveredClaims[3].ultimate
        assertEquals "largeClaim100", largeClaim100.ultimate, contract.outUncoveredClaims[4].ultimate
        assertEquals "attrClaim100 ceded 0", 0, contract.outCoveredClaims[0].ultimate
        assertEquals "largeClaim50 ceded 0", 0, contract.outCoveredClaims[1].ultimate
        assertEquals "largeClaim60 ceded 0", 0, contract.outCoveredClaims[2].ultimate
        assertEquals "largeClaim100 ceded 0", 0, contract.outCoveredClaims[3].ultimate
        assertEquals "largeClaim100 ceded 0", 0, contract.outCoveredClaims[4].ultimate
    }
}