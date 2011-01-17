package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.LPTPremiumBase
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests

/**
 * @author shartmann (at) munichre (dot) com
 */
class LossPortfolioTransferContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContractAbsolute0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.LOSSPORTFOLIOTRANSFER, ["quotaShare": 0.3d, "premiumBase": LPTPremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContractAbsolute100() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.LOSSPORTFOLIOTRANSFER, ["quotaShare": 0.3d, "premiumBase": LPTPremiumBase.ABSOLUTE, "premium": 100d, "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContractRelative05() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.LOSSPORTFOLIOTRANSFER, ["quotaShare": 0.3d, "premiumBase": LPTPremiumBase.RELATIVE_TO_CEDED_RESERVES_VOLUME, "premium": 200d, "coveredByReinsurer": 0.8]))
    }

    void testCalculateCoveredLoss() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContractAbsolute100()
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()
        contract.inClaims << attrClaim100 << largeClaim60

        def probeLPT = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "quotaShare1, attritional ceded claim", 30, contract.outCoveredClaims[0].ultimate
        assertEquals "quotaShare1, large ceded claim", 18, contract.outCoveredClaims[1].ultimate
        assertEquals "contract, premium", 100, contract.outCoverUnderwritingInfo[0].premium
    }

    void testCalculateCoveredReserves() {
        ClaimDevelopmentLeanPacket claim1 = new ClaimDevelopmentLeanPacket(incurred: 250d, paid: 100d)
        ClaimDevelopmentLeanPacket claim2 = new ClaimDevelopmentLeanPacket(incurred: 100d, paid: 20d)
        ClaimDevelopmentLeanPacket claim3 = new ClaimDevelopmentLeanPacket(incurred: 150d, paid: 80d)
        ReinsuranceContract contract = getContractAbsolute100()
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()
        contract.inClaims << claim1 << claim2 << claim3

        def probeLPT = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

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

        assertEquals "contract, premium", 100, contract.outCoverUnderwritingInfo[0].premium

    }

    void testAbsolutePremiumBase() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContractAbsolute0()
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()
        contract.inClaims << attrClaim100 << largeClaim60

        def probeLPT = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "contract, premium", 0, contract.outCoverUnderwritingInfo[0].premium
    }

    void testRelativeToCededReservesPremiumBase() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceContract contract = getContractRelative05() 
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()
        contract.inInitialReserves << new SingleValuePacket()
        contract.inInitialReserves[0].setValue(1000d)
        contract.inClaims << attrClaim100 << largeClaim60

        def probeLPT = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 2, contract.outCoveredClaims.size()
        assertEquals "contract, premium", 1000d *0.8 * 0.3, contract.outCoverUnderwritingInfo[0].premium, 10E-8
    }

}
