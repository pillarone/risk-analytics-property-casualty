package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionTests

class DynamicReinsuranceProgramTests extends GroovyTestCase {

    DynamicReinsuranceProgram program
    UnderwritingSegment underwritingSegment

    Claim attrMarketClaim1000 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 1000d, fractionOfPeriod: 0d)
    Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d, fractionOfPeriod: 0d, originalClaim: attrMarketClaim1000)
    Claim largeMarketClaim600 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 600d, fractionOfPeriod: 0d)
    Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, fractionOfPeriod: 0.1d, originalClaim: largeMarketClaim600)

    void testDefaultContracts() {
        program = new DynamicReinsuranceProgram()
        ReinsuranceContract contract = program.createDefaultSubComponent()
        assertNotNull contract
        assertSame ReinsuranceContractType.TRIVIAL, contract.parmContractStrategy.type
    }

    void testUsageSerialContractOrder() {
        program = new DynamicReinsuranceProgram()

        ReinsuranceContract quotaShare1 = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2,
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 10,
                simulationScope: CommissionTests.getTestSimulationScope())
        ReinsuranceContract quotaShare2 = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.1,
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 1,
                simulationScope: CommissionTests.getTestSimulationScope())
        ReinsuranceContract quotaShare3 = new ReinsuranceContract(
                parmInuringPriority: 5,
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE, [
                                "quotaShare": 0.15,
                                "coveredByReinsurer": 1d,
                                "limit": LimitStrategyType.getStrategy(
                                        LimitStrategyType.AAL, [
                                                "aal": 20d ])]),
                simulationScope: CommissionTests.getTestSimulationScope())
        quotaShare1.name = "subContract0"
        quotaShare2.name = "subContract1"
        quotaShare3.name = "subContract2"
        program.addSubComponent(quotaShare1)
        program.addSubComponent(quotaShare2)
        program.addSubComponent(quotaShare3)
        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        program.wire()

        List qs1ClaimsNet = (new TestProbe(quotaShare1, "outUncoveredClaims")).result
        List qs1ClaimsCeded = (new TestProbe(quotaShare1, "outCoveredClaims")).result
        List qs2ClaimsNet = (new TestProbe(quotaShare2, "outUncoveredClaims")).result
        List qs2ClaimsCeded = (new TestProbe(quotaShare2, "outCoveredClaims")).result
        List qs3ClaimsNet = (new TestProbe(quotaShare3, "outUncoveredClaims")).result
        List qs3ClaimsCeded = (new TestProbe(quotaShare3, "outCoveredClaims")).result

        program.start()

        assertTrue "QS1 has 2 net claims packets", 2 == qs1ClaimsNet.size()
        assertTrue "QS2 has 2 net claims packets", 2 == qs2ClaimsNet.size()
        assertTrue "QS3 has 2 net claims packets", 2 == qs3ClaimsNet.size()

        assertEquals "quotaShare1, attritional net claim", 61.2, qs1ClaimsNet[0].ultimate
        assertEquals "quotaShare1, attritional ceded claim", 15.3, qs1ClaimsCeded[0].ultimate
        assertEquals "quotaShare1, large net claim", 38, qs1ClaimsNet[1].ultimate
        assertEquals "quotaShare1, large ceded claim", 9.5, qs1ClaimsCeded[1].ultimate
        assertEquals "quotaShare2, attritional net claim", 90, qs2ClaimsNet[0].ultimate
        assertEquals "quotaShare2, attritional ceded claim", 10, qs2ClaimsCeded[0].ultimate
        assertEquals "quotaShare2, large net claim", 54, qs2ClaimsNet[1].ultimate
        assertEquals "quotaShare2, large ceded claim", 6, qs2ClaimsCeded[1].ultimate
        assertEquals "quotaShare3, attritional net claim", 76.5, qs3ClaimsNet[0].ultimate
        assertEquals "quotaShare3, attritional ceded claim", 13.5, qs3ClaimsCeded[0].ultimate
        assertEquals "quotaShare3, large net claim", 47.5, qs3ClaimsNet[1].ultimate
        assertEquals "quotaShare3, large ceded claim", 6.5, qs3ClaimsCeded[1].ultimate
    }

    void testUsageMixedContractOrder() {
        program = new DynamicReinsuranceProgram()

        ReinsuranceContract quotaShare = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2,
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 0,
                simulationScope: CommissionTests.getTestSimulationScope())
        ReinsuranceContract wxl20xs10 = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 10,
                                "limit": 20,
                                "aggregateLimit": 20,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 1,
                simulationScope: CommissionTests.getTestSimulationScope())
        ReinsuranceContract wxl20xs30 = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 30,
                                "limit": 20,
                                "aggregateLimit": 20,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 1,
                simulationScope: CommissionTests.getTestSimulationScope())

        wxl20xs10.name = "subContract0"
        quotaShare.name = "subContract1"
        wxl20xs30.name = "subContract2"
        program.addSubComponent(wxl20xs10)
        program.addSubComponent(quotaShare)
        program.addSubComponent(wxl20xs30)
        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        program.wire()

        def probeQSnet = new TestProbe(quotaShare, "outUncoveredClaims")
        List qsClaimsNet = probeQSnet.result

        def probeQSCeded = new TestProbe(quotaShare, "outCoveredClaims")
        List qsClaimsCeded = probeQSCeded.result

        def probeWXL20xs10 = new TestProbe(wxl20xs10, "outUncoveredClaims")
        List wxl20xs10Net = probeWXL20xs10.result

        def probeWXL20xs10Ceded = new TestProbe(wxl20xs10, "outCoveredClaims")
        List wxl20xs10Ceded = probeWXL20xs10Ceded.result

        def probeWXL20xs30Net = new TestProbe(wxl20xs30, "outUncoveredClaims")
        List wxl20xs30Net = probeWXL20xs30Net.result

        def probeWXL20xs30Ceded = new TestProbe(wxl20xs30, "outCoveredClaims")
        List wxl20xs30Ceded = probeWXL20xs30Ceded.result

        def probeProgramGross = new TestProbe(program, "outClaimsGross")
        List programGross = probeProgramGross.result

        def probeProgramCeded = new TestProbe(program, "outClaimsCeded")
        List programCeded = probeProgramCeded.result

        def probeProgramNet = new TestProbe(program, "outClaimsNet")
        List programNet = probeProgramNet.result

        program.start()

        assertTrue 2 == qsClaimsNet.size()
        assertTrue 2 == wxl20xs10Net.size()
        assertTrue 2 == wxl20xs30Net.size()
        assertEquals "#program gross claims program", 2, programGross.size()
        assertEquals "#program ceded claims program", 6, programCeded.size()
        assertEquals "#program net claims program", 2, programNet.size()

        assertEquals "quotaShare, attritional net claim", 80, qsClaimsNet[0].ultimate
        assertEquals "quotaShare, attritional ceded claim", 20, qsClaimsCeded[0].ultimate
        assertEquals "quotaShare, large net claim", 48, qsClaimsNet[1].ultimate
        assertEquals "quotaShare, large ceded claim", 12, qsClaimsCeded[1].ultimate

        assertEquals "wxl20xs10, attritional net claim", 80, wxl20xs10Net[0].ultimate
        assertEquals "wxl20xs10, large net claim", 28, wxl20xs10Net[1].ultimate
        assertEquals "wxl20xs10, large ceded claim", 0, wxl20xs10Ceded[0].ultimate
        assertEquals "wxl20xs10, large ceded claim", 20, wxl20xs10Ceded[1].ultimate

        assertEquals "wxl20xs30, attritional net claim", 80, wxl20xs30Net[0].ultimate
        assertEquals "wxl20xs30, large net claim", 30, wxl20xs30Net[1].ultimate
        assertEquals "wxl20xs30, large ceded claim", 0, wxl20xs30Ceded[0].ultimate
        assertEquals "wxl20xs30, large ceded claim", 18, wxl20xs30Ceded[1].ultimate

        assertEquals "program, attritional gross claim", 100, programGross[0].ultimate
        assertEquals "program, attritional net claim", 80, programNet[0].ultimate

        assertEquals "program, large gross claim", 60, programGross[1].ultimate
        assertEquals "program, large net claim", 10, programNet[1].ultimate

        assertEquals "program, attritional ceded claim, attritional contract1", 20, programCeded[0].ultimate
        assertEquals "program, large ceded claim, single contract1", 12, programCeded[1].ultimate
        assertEquals "program, large ceded claim, attritional contract0", 0, programCeded[2].ultimate
        assertEquals "program, large ceded claim, single contract0", 20, programCeded[3].ultimate
        assertEquals "program, large ceded claim, attritional contract2", 0, programCeded[4].ultimate
        assertEquals "program, large ceded claim, single contract2", 18, programCeded[5].ultimate
    }

    void testUsageQS_3WXL() {
        Claim attrMarketClaim1000 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 1000d, fractionOfPeriod: 0d)
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d, fractionOfPeriod: 0d, originalClaim: attrMarketClaim1000)
        Claim largeMarketClaim600 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 600d, fractionOfPeriod: 0d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, value: 60d, fractionOfPeriod: 0.1d, originalClaim: largeMarketClaim600)
        Claim largeClaim900 = new Claim(claimType: ClaimType.SINGLE, value: 900d, fractionOfPeriod: 0.2d)
        Claim largeClaim90 = new Claim(claimType: ClaimType.SINGLE, value: 90d, fractionOfPeriod: 0.2d, originalClaim: largeClaim900)

        program = new DynamicReinsuranceProgram()

        ReinsuranceContract quotaShare = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2,
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 0,
                simulationScope: CommissionTests.getTestSimulationScope())
        ReinsuranceContract wxl20xs10 = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 10,
                                "limit": 20,
                                "aggregateLimit": 20,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 1,
                simulationScope: CommissionTests.getTestSimulationScope())
        ReinsuranceContract wxl20xs30 = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 30,
                                "limit": 20,
                                "aggregateLimit": 20,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 1,
                simulationScope: CommissionTests.getTestSimulationScope())
        ReinsuranceContract wxl20xs50 = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 50,
                                "limit": 20,
                                "aggregateLimit": 20,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 1,
                simulationScope: CommissionTests.getTestSimulationScope())

        wxl20xs50.name = "subContract0"
        wxl20xs10.name = "subContract1"
        quotaShare.name = "subContract2"
        wxl20xs30.name = "subContract3"
        program.addSubComponent(wxl20xs50)
        program.addSubComponent(wxl20xs10)
        program.addSubComponent(quotaShare)
        program.addSubComponent(wxl20xs30)
        program.inClaims << attrClaim100 << largeClaim60 << largeClaim90
        program.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        program.wire()

        def probeQSnet = new TestProbe(quotaShare, "outUncoveredClaims")
        List qsClaimsNet = probeQSnet.result
        def probeQSCeded = new TestProbe(quotaShare, "outCoveredClaims")
        List qsClaimsCeded = probeQSCeded.result

        def probeWXL20xs10 = new TestProbe(wxl20xs10, "outUncoveredClaims")
        List wxl20xs10Net = probeWXL20xs10.result
        def probeWXL20xs10Ceded = new TestProbe(wxl20xs10, "outCoveredClaims")
        List wxl20xs10Ceded = probeWXL20xs10Ceded.result

        def probeWXL20xs30Net = new TestProbe(wxl20xs30, "outUncoveredClaims")
        List wxl20xs30Net = probeWXL20xs30Net.result
        def probeWXL20xs30Ceded = new TestProbe(wxl20xs30, "outCoveredClaims")
        List wxl20xs30Ceded = probeWXL20xs30Ceded.result

        def probeWXL20xs50Net = new TestProbe(wxl20xs50, "outUncoveredClaims")
        List wxl20xs50Net = probeWXL20xs50Net.result
        def probeWXL20xs50Ceded = new TestProbe(wxl20xs50, "outCoveredClaims")
        List wxl20xs50Ceded = probeWXL20xs50Ceded.result

        def probeProgramGross = new TestProbe(program, "outClaimsGross")
        List programGross = probeProgramGross.result
        def probeProgramCeded = new TestProbe(program, "outClaimsCeded")
        List programCeded = probeProgramCeded.result
        def probeProgramNet = new TestProbe(program, "outClaimsNet")
        List programNet = probeProgramNet.result

        program.start()

        assertTrue 3 == qsClaimsNet.size()
        assertTrue 3 == wxl20xs10Net.size()
        assertTrue 3 == wxl20xs30Net.size()
        assertTrue 3 == wxl20xs50Net.size()
        assertEquals "#program gross claims program", 3, programGross.size()
        assertEquals "#program ceded claims program", 12, programCeded.size()
        assertEquals "#program net claims program", 3, programNet.size()

        assertEquals "quotaShare, attritional net claim", 80, qsClaimsNet[0].ultimate
        assertEquals "quotaShare, attritional ceded claim", 20, qsClaimsCeded[0].ultimate
        assertEquals "quotaShare, large net claim 2", 48, qsClaimsNet[1].ultimate
        assertEquals "quotaShare, large ceded claim 2", 12, qsClaimsCeded[1].ultimate
        assertEquals "quotaShare, large net claim 3", 72, qsClaimsNet[2].ultimate
        assertEquals "quotaShare, large ceded claim 3", 18, qsClaimsCeded[2].ultimate

        assertEquals "wxl20xs10, attritional net claim", 80, wxl20xs10Net[0].ultimate
        assertEquals "wxl20xs10, large net claim 2", 28, wxl20xs10Net[1].ultimate
        assertEquals "wxl20xs10, large ceded claim 2", 20, wxl20xs10Ceded[1].ultimate
        assertEquals "wxl20xs10, large ceded claim 3", 0, wxl20xs10Ceded[2].ultimate
        assertEquals "wxl20xs10, large net claim 3", 72, wxl20xs10Net[2].ultimate

        assertEquals "wxl20xs30, attritional net claim", 80, wxl20xs30Net[0].ultimate
        assertEquals "wxl20xs30, large net claim 2", 30, wxl20xs30Net[1].ultimate
        assertEquals "wxl20xs30, large ceded claim 2", 0, wxl20xs30Ceded[0].ultimate
        assertEquals "wxl20xs30, large ceded claim 2", 18, wxl20xs30Ceded[1].ultimate
        assertEquals "wxl20xs30, large net claim 3", 70, wxl20xs30Net[2].ultimate
        assertEquals "wxl20xs30, large ceded claim 3", 2, wxl20xs30Ceded[2].ultimate

        assertEquals "wxl20xs50, attritional net claim", 80, wxl20xs50Net[0].ultimate
        assertEquals "wxl20xs50, large net claim 2", 48, wxl20xs50Net[1].ultimate
        assertEquals "wxl20xs50, large net claim 3", 52, wxl20xs50Net[2].ultimate
        assertEquals "wxl20xs50, large ceded claim 3", 0, wxl20xs50Ceded[0].ultimate
        assertEquals "wxl20xs50, large ceded claim 3", 0, wxl20xs50Ceded[1].ultimate
        assertEquals "wxl20xs50, large ceded claim 3", 20, wxl20xs50Ceded[2].ultimate

        assertEquals "program, attritional gross claim", 100, programGross[0].ultimate
        assertEquals "program, attritional net claim", 80, programNet[0].ultimate
        assertEquals "program, large gross claim 2", 60, programGross[1].ultimate
        assertEquals "program, large net claim 2", 10, programNet[1].ultimate
        assertEquals "program, large gross claim 3", 90, programGross[2].ultimate
        assertEquals "program, large net claim 3", 50, programNet[2].ultimate

        assertEquals "program, attritional ceded claim, contract2", 20, programCeded[0].ultimate
        assertEquals "program, large ceded claim 2, contract2", 12, programCeded[1].ultimate
        assertEquals "program, large ceded claim 3, contract2", 18, programCeded[2].ultimate
        assertEquals "program, attritional ceded claim, contract0", 0, programCeded[3].ultimate
        assertEquals "program, large ceded claim 2, contract0", 0, programCeded[4].ultimate
        assertEquals "program, large ceded claim 3, contract0", 20, programCeded[5].ultimate
        assertEquals "program, attritional ceded claim, contract1", 0, programCeded[6].ultimate
        assertEquals "program, large ceded claim 2, contract1", 20, programCeded[7].ultimate
        assertEquals "program, large ceded claim 3, contract1", 0, programCeded[8].ultimate
        assertEquals "program, attritional ceded claim, contract3", 0, programCeded[9].ultimate
        assertEquals "program, large ceded claim 2, contract3", 18, programCeded[10].ultimate
        assertEquals "program, large ceded claim 3, contract3", 2, programCeded[11].ultimate
    }
}