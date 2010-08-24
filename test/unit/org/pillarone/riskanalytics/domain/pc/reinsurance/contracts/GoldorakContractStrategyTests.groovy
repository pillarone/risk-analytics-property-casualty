package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests

/**
 * @author shartmann (at) munichre (dot) com
 */
class GoldorakContractStrategyTests extends GroovyTestCase {


    static ReinsuranceContract getContractGoldorakAbs() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.GOLDORAK,
                        ["cxlAttachmentPoint": 100d,
                                "cxlLimit": 200d,
                                "cxlAggregateLimit": 400d,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premium": 20d,
                                "coveredByReinsurer": 1d,
                                "slAttachmentPoint": 1000d,
                                "slLimit": 180d,
                                "goldorakSlThreshold" : 1180d,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])
                        ]
                )
        )
    }

    static ReinsuranceContract getContractGoldorakRateOnLine() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.GOLDORAK,
                        ["cxlAttachmentPoint": 100d,
                                "cxlLimit": 200d,
                                "cxlAggregateLimit": 400d,
                                "premiumBase": PremiumBase.RATE_ON_LINE,
                                "premium": 0.10d,
                                "coveredByReinsurer": 1d,
                                "slAttachmentPoint": 1000d,
                                "slLimit": 180d,
                                "goldorakSlThreshold" : 1180d,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])
                        ]
                )
        )
    }

    static ReinsuranceContract getContractGoldorakGNPI() {
        // GNPI = Gross Premium = 1000
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.GOLDORAK,
                        ["cxlAttachmentPoint": 0.10d,
                                "cxlLimit": 0.20d,
                                "cxlAggregateLimit": 0.40d,
                                "premiumBase": PremiumBase.GNPI,
                                "premium": 0.05d,
                                "coveredByReinsurer": 1d,
                                "slAttachmentPoint": 1d,
                                "slLimit": 0.180d,
                                "goldorakSlThreshold" : 1.18d,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])
                        ]
                )
        )
    }



    void testCedingClaimsAbsoluteCXL() {
        Event event0 = new Event(date: 0)
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 600d)
        Claim claim2 = new Claim(claimType: ClaimType.AGGREGATED_EVENT, ultimate: 400d, event: event0)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract contract = getContractGoldorakAbs()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo4()      //premium=1000

        def probeContractNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        GoldorakContractStrategy goldStrategy = contract.parmContractStrategy
        goldStrategy.initBookkeepingFigures(contract.inClaims, contract.inUnderwritingInfo)

        assertTrue contract.outCoveredClaims.isEmpty()
        contract.doCalculation()

        assertEquals "# ceded claims", 3, contract.outCoveredClaims.size()
        assertEquals "claim1, ceded", 0d, contract.outCoveredClaims[0].ultimate
        assertEquals "claim2, ceded", 200d, contract.outCoveredClaims[1].ultimate
        assertEquals "claim3, ceded", 0d, contract.outCoveredClaims[2].ultimate

        assertEquals "# net claims", 3, contract.outUncoveredClaims.size()
        assertEquals "claim1, net", 600d, contract.outUncoveredClaims[0].ultimate
        assertEquals "claim2, net", 200d, contract.outUncoveredClaims[1].ultimate
        assertEquals "claim3, net", 100d, contract.outUncoveredClaims[2].ultimate
        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testCedingClaimsAbsoluteSL() {
        Event event0 = new Event(date: 0)
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 800d)
        Claim claim2 = new Claim(claimType: ClaimType.AGGREGATED_EVENT, ultimate: 400d, event: event0)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract contract = getContractGoldorakAbs()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo4()      //premium=1000
        double slRatio=180d/1300d

        def probeContractNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        GoldorakContractStrategy goldStrategy = contract.parmContractStrategy
        goldStrategy.initBookkeepingFigures(contract.inClaims, contract.inUnderwritingInfo)

        assertTrue contract.outCoveredClaims.isEmpty()
        contract.doCalculation()
        assertEquals "ceded claims", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", claim1.ultimate * slRatio, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", claim2.ultimate * slRatio, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", claim3.ultimate * slRatio, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 3, contract.outUncoveredClaims.size()

        assertEquals "# ceded claims", 3, contract.outCoveredClaims.size()
        assertEquals "claim1, ceded", claim1.ultimate * slRatio, contract.outCoveredClaims[0].ultimate
        assertEquals "claim2, ceded", claim2.ultimate * slRatio, contract.outCoveredClaims[1].ultimate
        assertEquals "claim3, ceded", claim3.ultimate * slRatio, contract.outCoveredClaims[2].ultimate

        assertEquals "# net claims", 3, contract.outUncoveredClaims.size()
        assertEquals "claim1, net", claim1.ultimate * (1.0 - slRatio), contract.outUncoveredClaims[0].ultimate
        assertEquals "claim2, net", claim2.ultimate * (1.0 - slRatio), contract.outUncoveredClaims[1].ultimate
        assertEquals "claim3, net", claim3.ultimate * (1.0 - slRatio), contract.outUncoveredClaims[2].ultimate

        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testCedingClaimsGnpiCXL() {
        Event event0 = new Event(date: 0)
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 600d)
        Claim claim2 = new Claim(claimType: ClaimType.AGGREGATED_EVENT, ultimate: 400d, event: event0)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract contract = getContractGoldorakGNPI()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo4()      //premium=1000

        def probeContractNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        GoldorakContractStrategy goldStrategy = contract.parmContractStrategy
        goldStrategy.initBookkeepingFigures(contract.inClaims, contract.inUnderwritingInfo)

        assertTrue contract.outCoveredClaims.isEmpty()
        contract.doCalculation()
        assertEquals "ceded claims", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", 0d, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", 200d, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", 0d, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 3, contract.outUncoveredClaims.size()
        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testCedingClaimsGnpiSL() {
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 800d)
        Claim claim2 = new Claim(claimType: ClaimType.AGGREGATED_EVENT, ultimate: 400d)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract contract = getContractGoldorakGNPI()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo4()      //premium=1000
        double slRatio=180d/1300d

        def probeContractNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        GoldorakContractStrategy goldStrategy = contract.parmContractStrategy
        goldStrategy.initBookkeepingFigures(contract.inClaims, contract.inUnderwritingInfo)

        assertTrue contract.outCoveredClaims.isEmpty()
        contract.doCalculation()
        assertEquals "ceded claims", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", claim1.ultimate * slRatio, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", claim2.ultimate * slRatio, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", claim3.ultimate * slRatio, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 3, contract.outUncoveredClaims.size()
        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testCedingClaimsRolCXL() {
        Event event0 = new Event(date: 0)
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 600d)
        Claim claim2 = new Claim(claimType: ClaimType.AGGREGATED_EVENT, ultimate: 400d, event: event0)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract contract = getContractGoldorakRateOnLine()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo4()      //premium=1000

        def probeContractNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        GoldorakContractStrategy goldStrategy = contract.parmContractStrategy
        goldStrategy.initBookkeepingFigures(contract.inClaims, contract.inUnderwritingInfo)

        assertTrue contract.outCoveredClaims.isEmpty()
        contract.doCalculation()
        assertEquals "ceded claims", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", 0d, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", 200d, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", 0d, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 3, contract.outUncoveredClaims.size()
        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testCedingClaimsRolSL() {
        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 800d)
        Claim claim2 = new Claim(claimType: ClaimType.AGGREGATED_EVENT, ultimate: 400d)
        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract contract = getContractGoldorakRateOnLine()
        contract.inClaims << claim1 << claim2 << claim3
        contract.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo4()      //premium=1000
        double slRatio=180d/1300d

        def probeContractNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        GoldorakContractStrategy goldStrategy = contract.parmContractStrategy
        goldStrategy.initBookkeepingFigures(contract.inClaims, contract.inUnderwritingInfo)

        assertTrue contract.outCoveredClaims.isEmpty()
        contract.doCalculation()
        assertEquals "ceded claims", 3, contract.outCoveredClaims.size()
        assertEquals "outClaims[0]", claim1.ultimate * slRatio, contract.outCoveredClaims[0].ultimate
        assertEquals "outClaims[1]", claim2.ultimate * slRatio, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims[2]", claim3.ultimate * slRatio, contract.outCoveredClaims[2].ultimate

        assertEquals "outClaimsNet.size", 3, contract.outUncoveredClaims.size()
        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }
}
//
// void testInitBookKeepingFigures() {
//        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 2600d)
//        Claim claim2 = new Claim(claimType: ClaimType.SINGLE, ultimate: 600d)
//        Claim claim3 = new Claim(claimType: ClaimType.SINGLE, ultimate: 800d)
//        List<Claim> claims = []
//        claims << claim1 << claim2 << claim3
//        UnderwritingInfo grossUnderwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()     //premium=2000
//        ReinsuranceContract stopLoss = getContractSL0()                                             //40% xs 120% => 800xs 2400
//        //============================================================ testInitBookKeepingFigures()
//        stopLoss.parmContractStrategy.initBookkeepingFigures claims, [grossUnderwritingInfo]
//        assertEquals "factor", 0.2, stopLoss.parmContractStrategy.factor                           //pay 800 out of 4000
//        //============================================================ testGetCededUnderwriting
//        UnderwritingInfo cededUnderwritingInfo = stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(grossUnderwritingInfo, 0)
//
//        assertEquals "premium written", stopLoss.parmContractStrategy.premium * grossUnderwritingInfo.premiumWritten, cededUnderwritingInfo.premiumWritten
//        assertEquals "premium written as if", stopLoss.parmContractStrategy.premium * grossUnderwritingInfo.premiumWrittenAsIf, cededUnderwritingInfo.premiumWrittenAsIf
//
//    }
//
//
//
//    void testGetCededUnderwritingInfoROE_IAE() {
//        ReinsuranceContract stopLoss = getContractSL1()
//        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
//        stopLoss.parmContractStrategy.premiumBase = PremiumBase.RATE_ON_LINE
//        shouldFail(IllegalArgumentException) {
//            stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0)
//        }
//    }
//
//
//    void testGetCededUnderwritingInfoNOP_IAE() {
//        ReinsuranceContract stopLoss = getContractSL1()
//        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
//        stopLoss.parmContractStrategy.premiumBase = PremiumBase.NUMBER_OF_POLICIES
//        shouldFail(IllegalArgumentException) {
//            stopLoss.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0)
//        }
//    }