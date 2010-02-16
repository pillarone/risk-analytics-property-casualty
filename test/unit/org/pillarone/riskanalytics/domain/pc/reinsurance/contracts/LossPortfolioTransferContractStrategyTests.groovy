package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket

/**
 * @author shartmann (at) munichre (dot) com
 */
class LossPortfolioTransferContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.LOSSPORTFOLIOTRANSFER, ["quotaShare": 0.3, "commission": 0.0, "coveredByReinsurer": 1d]))
    }

    void testCalculateCoveredLoss() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim100 << largeClaim60

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 30, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, large ceded claim", 18, contract.outCoveredClaims[1].ultimate
    }

    void testCalculateCoveredReserves() {
        ClaimDevelopmentLeanPacket claim1 = new ClaimDevelopmentLeanPacket(incurred: 250d, paid: 100d)
        ClaimDevelopmentLeanPacket claim2 = new ClaimDevelopmentLeanPacket(incurred: 100d, paid: 20d)
        ClaimDevelopmentLeanPacket claim3 = new ClaimDevelopmentLeanPacket(incurred: 150d, paid: 80d)
        ReinsuranceContract contract = getContract0()
        contract.inClaims << claim1 << claim2 << claim3

        contract.doCalculation()

        assertEquals "# covered claims", 3, contract.outCoveredClaims.size()
        assertEquals "# unovered claims", 0, contract.outUncoveredClaims.size()
        assertEquals "incurred, claim1", 75d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[0]).incurred
        assertEquals "paid, claim1", 30d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[0]).paid
        assertEquals "reserved, claim1", 45d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[0]).reserved

        assertEquals "incurred, claim1", 30d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[1]).incurred
        assertEquals "paid, claim1", 6d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[1]).paid
        assertEquals "reserved, claim1", 24d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[1]).reserved

        assertEquals "incurred, claim1", 45d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[2]).incurred
        assertEquals "paid, claim1", 24d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[2]).paid
        assertEquals "reserved, claim1", 21d, ((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[2]).reserved

        assertEquals "incurred, all claims", 150d, (((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[0]).incurred
                +((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[1]).incurred
                +((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[2]).incurred)
        assertEquals "paid, all claims", 60d, (((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[0]).paid
                +((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[1]).paid
                +((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[2]).paid)
        assertEquals "reserved, all claims", 90d, (((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[0]).reserved
                +((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[1]).reserved
                +((ClaimDevelopmentLeanPacket) contract.outCoveredClaims[2]).reserved)
    }
}
