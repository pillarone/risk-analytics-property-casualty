package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class StopLossContractStrategyTests extends GroovyTestCase {


    static ReinsuranceContract getContractSL0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.STOPLOSS,
                        ["attachmentPoint": 1.20,
                                "limit": 0.40,
                                "premiumBase": PremiumBase.GNPI,
                                "premium": 0.20,
                                "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContractSL1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.STOPLOSS,
                        ["attachmentPoint": 1.15,
                                "limit": 0.15,
                                "premiumBase": PremiumBase.GNPI,
                                "premium": 0.1,
                                "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContractSLAbs0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.STOPLOSS,
                        ["attachmentPoint": 2400,
                                "limit": 800,
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
        contract.doCalculation()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", claim1.ultimate * 0.2, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", claim2.ultimate * 0.2, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", claim3.ultimate * 0.2, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
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
        contract.doCalculation()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", claim1.ultimate * 0.2, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", claim2.ultimate * 0.2, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", claim3.ultimate * 0.2, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
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
        UnderwritingInfo cededUnderwritingInfo = stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(grossUnderwritingInfo, 0)

        assertEquals "premium written", stopLoss.parmContractStrategy.premium * grossUnderwritingInfo.premiumWritten, cededUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", stopLoss.parmContractStrategy.premium * grossUnderwritingInfo.premiumWrittenAsIf, cededUnderwritingInfo.premiumWrittenAsIf

    }



    void testGetCededUnderwritingInfoROE_IAE() {
        ReinsuranceContract stopLoss = getContractSL1()
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        stopLoss.parmContractStrategy.premiumBase = PremiumBase.RATE_ON_LINE
        shouldFail(IllegalArgumentException) {
            stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0)
        }
    }


    void testGetCededUnderwritingInfoNOP_IAE() {
        ReinsuranceContract stopLoss = getContractSL1()
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        stopLoss.parmContractStrategy.premiumBase = PremiumBase.NUMBER_OF_POLICIES
        shouldFail(IllegalArgumentException) {
            stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0)
        }
    }
}