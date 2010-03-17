package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.util.TestProbe

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com, shartmann (at) munichre (dot) com
 */
class AdverseDevelopmentCoverContractStrategyTests extends GroovyTestCase {


    static ReinsuranceContract getContractSL0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.ADVERSEDEVELOPMENTCOVER,
                        ["attachmentPoint": 1.20,
                                "limit": 0.40,
                                "premiumBase": PremiumBase.GNPI,
                                "premium": 0.20,
                                "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContractSL1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.ADVERSEDEVELOPMENTCOVER,
                        ["attachmentPoint": 1.15,
                                "limit": 0.15,
                                "premiumBase": PremiumBase.GNPI,
                                "premium": 0.1,
                                "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContractSLAbs0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.ADVERSEDEVELOPMENTCOVER,
                        ["attachmentPoint": 2400,
                                "limit": 800,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premium": 400,
                                "coveredByReinsurer": 1d]))
    }

        static ReinsuranceContract getContractADCXS(double limit, double attachmentPoint) {
            return new ReinsuranceContract(
                    parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                            ReinsuranceContractType.ADVERSEDEVELOPMENTCOVER,
                            ["attachmentPoint": attachmentPoint,
                                    "limit": limit,
                                    "premiumBase": PremiumBase.ABSOLUTE,
                                    "premium": 400,
                                    "coveredByReinsurer": 1d]))
        }


    void testCedingClaimsGNPI() {
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 2600d)
        Claim claim2 = new Claim(claimType: ClaimType.SINGLE, ultimate: 600d)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 800d)

        ReinsuranceContract contract = getContractSL0()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()      //premium=2000

        assertTrue contract.outCoveredClaims.isEmpty()
        def probeLPT = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info
        contract.doCalculation()

        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", claim1.ultimate * 0.2, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", claim2.ultimate * 0.2, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", claim3.ultimate * 0.2, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "contract, premium", 2000d * 0.2, contract.outCoverUnderwritingInfo[0].premiumWritten
        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testCedingClaimsAbsolute() {
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 2600d)
        Claim claim2 = new Claim(claimType: ClaimType.SINGLE, ultimate: 600d)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 800d)

        ReinsuranceContract contract = getContractSLAbs0()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()      //premium=2000

        assertTrue contract.outCoveredClaims.isEmpty()
        def probeLPT = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info
        contract.doCalculation()

        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", claim1.ultimate * 0.2, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", claim2.ultimate * 0.2, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", claim3.ultimate * 0.2, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "contract, premium", 400d, contract.outCoverUnderwritingInfo[0].premiumWritten
        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testInitBookKeepingFigures() {
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 2600d)
        Claim claim2 = new Claim(claimType: ClaimType.SINGLE, ultimate: 600d)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 800d)
        List<Claim> claims = []
        claims << claim1 << claim2 << claim3
        UnderwritingInfo grossUnderwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()     //premium=2000
        ReinsuranceContract stopLoss = getContractSL0()                                             //40% xs 120% => 800xs 2400
        //============================================================ testInitBookKeepingFigures()
        stopLoss.parmContractStrategy.initBookKeepingFigures claims, [grossUnderwritingInfo]
        assertEquals "factor", 0.2, stopLoss.parmContractStrategy.factor                           //pay 800 out of 4000
        //============================================================ testGetCededUnderwriting
        UnderwritingInfo cededUnderwritingInfo = stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(grossUnderwritingInfo, 0d)

        assertEquals "premium written", stopLoss.parmContractStrategy.premium * grossUnderwritingInfo.premiumWritten, cededUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", stopLoss.parmContractStrategy.premium * grossUnderwritingInfo.premiumWrittenAsIf, cededUnderwritingInfo.premiumWrittenAsIf

    }



    void testGetCededUnderwritingInfoROE_IAE() {
        ReinsuranceContract stopLoss = getContractSL1()
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        stopLoss.parmContractStrategy.premiumBase = PremiumBase.RATE_ON_LINE
        shouldFail(IllegalArgumentException) {
            stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0d)
        }
    }


    void testGetCededUnderwritingInfoNOP_IAE() {
        ReinsuranceContract stopLoss = getContractSL1()
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        stopLoss.parmContractStrategy.premiumBase = PremiumBase.NUMBER_OF_POLICIES
        shouldFail(IllegalArgumentException) {
            stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0d)
        }
    }

    void testADC200XS200() {
        ClaimDevelopmentLeanPacket claim1 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 250, paid: 100, reserved: 150)
        ClaimDevelopmentLeanPacket claim2 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 100, paid: 20, reserved: 80)
        ClaimDevelopmentLeanPacket claim3 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 150, paid: 80, reserved: 70)

        ReinsuranceContract contract = getContractADCXS(200, 200)
        contract.inClaims << claim1 << claim2 << claim3

        PacketList<Claim> cededClaims = contract.outCoveredClaims
        PacketList<Claim> netClaims = contract.outUncoveredClaims
        
        contract.doCalculation()

        assertEquals "# covered claims", 3, cededClaims.size()
        assertEquals "# uncovered claims", 3, netClaims.size()

        // expect: proportional to gross incurred
        //todo(bgi): update & re-enable the tests below after the expected values are decided upon

        // test cededClaims
        assertEquals "claim 1, incurred", 100, ((ClaimDevelopmentLeanPacket) cededClaims[0]).incurred
        assertEquals "claim 1, paid", 0, ((ClaimDevelopmentLeanPacket) cededClaims[0]).paid
        assertEquals "claim 1, reserved", 100, ((ClaimDevelopmentLeanPacket) cededClaims[0]).reserved

        assertEquals "claim 2, incurred", 40, ((ClaimDevelopmentLeanPacket) cededClaims[1]).incurred
        assertEquals "claim 2, paid", 0, ((ClaimDevelopmentLeanPacket) cededClaims[1]).paid
        assertEquals "claim 2, reserved", 40, ((ClaimDevelopmentLeanPacket) cededClaims[1]).reserved

        assertEquals "claim 3, incurred", 60, ((ClaimDevelopmentLeanPacket) cededClaims[2]).incurred
        assertEquals "claim 3, paid", 0, ((ClaimDevelopmentLeanPacket) cededClaims[2]).paid
        assertEquals "claim 3, reserved", 60, ((ClaimDevelopmentLeanPacket) cededClaims[2]).reserved

        assertEquals "total claims incurred", 200, (((ClaimDevelopmentLeanPacket) cededClaims[0]).incurred
                                                   +((ClaimDevelopmentLeanPacket) cededClaims[1]).incurred
                                                   +((ClaimDevelopmentLeanPacket) cededClaims[2]).incurred)
        assertEquals "total claims paid", 0, (((ClaimDevelopmentLeanPacket) cededClaims[0]).paid
                                             +((ClaimDevelopmentLeanPacket) cededClaims[1]).paid
                                             +((ClaimDevelopmentLeanPacket) cededClaims[2]).paid)
        assertEquals "total claims reserved", 200, (((ClaimDevelopmentLeanPacket) cededClaims[0]).reserved
                                                   +((ClaimDevelopmentLeanPacket) cededClaims[1]).reserved
                                                   +((ClaimDevelopmentLeanPacket) cededClaims[2]).reserved)

        // test netClaims
        assertEquals "claim 1, net incurred", 150, ((ClaimDevelopmentLeanPacket) netClaims[0]).incurred
        assertEquals "claim 1, net paid", 100, ((ClaimDevelopmentLeanPacket) netClaims[0]).paid
        assertEquals "claim 1, net reserved", 50, ((ClaimDevelopmentLeanPacket) netClaims[0]).reserved

        assertEquals "claim 2, net incurred", 60, ((ClaimDevelopmentLeanPacket) netClaims[1]).incurred
        assertEquals "claim 2, net paid", 20, ((ClaimDevelopmentLeanPacket) netClaims[1]).paid
        assertEquals "claim 2, net reserved", 40, ((ClaimDevelopmentLeanPacket) netClaims[1]).reserved

        assertEquals "claim 3, net incurred", 90, ((ClaimDevelopmentLeanPacket) netClaims[2]).incurred
        assertEquals "claim 3, net paid", 80, ((ClaimDevelopmentLeanPacket) netClaims[2]).paid
        assertEquals "claim 3, net reserved", 10, ((ClaimDevelopmentLeanPacket) netClaims[2]).reserved

        assertEquals "total claims, net incurred", 300, (((ClaimDevelopmentLeanPacket) netClaims[0]).incurred
                                                        +((ClaimDevelopmentLeanPacket) netClaims[1]).incurred
                                                        +((ClaimDevelopmentLeanPacket) netClaims[2]).incurred)
        assertEquals "total claims, net paid", 200, (((ClaimDevelopmentLeanPacket) netClaims[0]).paid
                                                    +((ClaimDevelopmentLeanPacket) netClaims[1]).paid
                                                    +((ClaimDevelopmentLeanPacket) netClaims[2]).paid)
        assertEquals "total claims, net reserved", 100, (((ClaimDevelopmentLeanPacket) netClaims[0]).reserved
                                                        +((ClaimDevelopmentLeanPacket) netClaims[1]).reserved
                                                        +((ClaimDevelopmentLeanPacket) netClaims[2]).reserved)
    }

    void testADC200XS100() {
        
    }
}