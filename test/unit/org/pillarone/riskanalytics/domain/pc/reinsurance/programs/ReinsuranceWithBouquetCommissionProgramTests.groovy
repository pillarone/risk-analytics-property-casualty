package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.Commission
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionTests
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable.ApplicableStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType
import org.pillarone.riskanalytics.domain.pc.generators.claims.DevelopedTypableClaimsGenerator

/**
 * @author shartmann (at) munichre (dot) com
 */
class ReinsuranceWithBouquetCommissionProgramTests extends GroovyTestCase {

    ReinsuranceWithBouquetCommissionProgram program
    UnderwritingSegment underwritingSegment

    DevelopedTypableClaimsGenerator claimsGenerator = new DevelopedTypableClaimsGenerator()
    Claim attrMarketClaim1000 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 1000d, fractionOfPeriod: 0d, peril: claimsGenerator)
    Claim largeMarketClaim600 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 600d, fractionOfPeriod: 0d, peril: claimsGenerator)
    Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d, fractionOfPeriod: 0d, originalClaim: attrMarketClaim1000, peril: claimsGenerator)
    Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, fractionOfPeriod: 0.1d, originalClaim: largeMarketClaim600, peril: claimsGenerator)

    static MultiCoverAttributeReinsuranceContract getQuotaShare(int quotaShare/*units=percent!*/, int priority, double limit = 0d) {
        new MultiCoverAttributeReinsuranceContract(
            parmInuringPriority: priority,
            parmContractStrategy: ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.QUOTASHARE, [
                    "quotaShare": 0.01d * quotaShare,
                    "coveredByReinsurer": 1d,
                    "limit": (limit > 0) ?
                        LimitStrategyType.getStrategy(LimitStrategyType.AAL, ["aal": limit]) :
                        LimitStrategyType.getStrategy(LimitStrategyType.NONE, [:])
                ]),
            simulationScope: CommissionTests.getTestSimulationScope()
            // parmCover: default is ALL
        )
    }

    static MultiCoverAttributeReinsuranceContract getWXL(double attachment){
        new MultiCoverAttributeReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": attachment,
                                "limit": 20,
                                "aggregateLimit": 20,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]),
                parmInuringPriority: 5,
                simulationScope: CommissionTests.getTestSimulationScope())
    }

    static double getListPropertySum (List list, String propertyName) {
        double sum = 0
        for (Iterator<Object> i = list.iterator(); i.hasNext();) {
            sum += i.next().getProperties()[propertyName]
        }
        sum
    }

    void testDefaultContracts() {
        program = new ReinsuranceWithBouquetCommissionProgram()
        ReinsuranceContract contract = program.subContracts.createDefaultSubComponent()
        assertNotNull contract
        assertSame ReinsuranceContractType.TRIVIAL, contract.parmContractStrategy.type
    }

    void testUsageSerialContractOrder() {
        program = new ReinsuranceWithBouquetCommissionProgram()

        MultiCoverAttributeReinsuranceContract quotaShare1 = getQuotaShare(20,10)
        MultiCoverAttributeReinsuranceContract quotaShare2 = getQuotaShare(10,1)
        MultiCoverAttributeReinsuranceContract quotaShare3 = getQuotaShare(15,5,20) // annualAggregateLimit AAL = 20
        quotaShare1.name = "subContract0"
        quotaShare2.name = "subContract1"
        quotaShare3.name = "subContract2"
        program.subContracts.addSubComponent(quotaShare1)
        program.subContracts.addSubComponent(quotaShare2)
        program.subContracts.addSubComponent(quotaShare3)
        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << CommissionTests.getUnderwritingInfoFromSelf(
                                            premium: 2000, numberOfPolicies: 100, exposureDefinition: Exposure.ABSOLUTE)

        program.internalWiring()

        def probeQS1net = new TestProbe(quotaShare1, "outUncoveredClaims")
        def probeQS2net = new TestProbe(quotaShare2, "outUncoveredClaims")
        def probeQS3net = new TestProbe(quotaShare3, "outUncoveredClaims")

        def probeQS1ceded = new TestProbe(quotaShare1, "outCoveredClaims")
        def probeQS2ceded = new TestProbe(quotaShare2, "outCoveredClaims")
        def probeQS3ceded = new TestProbe(quotaShare3, "outCoveredClaims")

        List qs1ClaimsNet = probeQS1net.result
        List qs2ClaimsNet = probeQS2net.result
        List qs3ClaimsNet = probeQS3net.result

        List qs1ClaimsCeded = probeQS1ceded.result
        List qs2ClaimsCeded = probeQS2ceded.result
        List qs3ClaimsCeded = probeQS3ceded.result

        program.start()

        assertEquals "# quotaShare1 net claims", 2, qs1ClaimsNet.size()
        assertEquals "# quotaShare2 net claims", 2, qs2ClaimsNet.size()
        assertEquals "# quotaShare3 net claims", 2, qs3ClaimsNet.size()

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
        program = new ReinsuranceWithBouquetCommissionProgram()

        MultiCoverAttributeReinsuranceContract quotaShare = getQuotaShare(20,0)
        MultiCoverAttributeReinsuranceContract wxl20xs10 = getWXL(10)
        MultiCoverAttributeReinsuranceContract wxl20xs30 = getWXL(30)

        wxl20xs10.name = "subContract0"
        quotaShare.name = "subContract1"
        wxl20xs30.name = "subContract2"
        program.subContracts.addSubComponent(wxl20xs10)
        program.subContracts.addSubComponent(quotaShare)
        program.subContracts.addSubComponent(wxl20xs30)
        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << CommissionTests.getUnderwritingInfoFromSelf(
                                            premium: 2000, numberOfPolicies: 100, exposureDefinition: Exposure.ABSOLUTE)

        program.internalWiring()

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
        Claim attrMarketClaim1000 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 1000d, fractionOfPeriod: 0d, peril: claimsGenerator)
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d, fractionOfPeriod: 0d, originalClaim: attrMarketClaim1000, peril: claimsGenerator)
        Claim largeMarketClaim600 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 600d, fractionOfPeriod: 0d, peril: claimsGenerator)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, value: 60d, fractionOfPeriod: 0.1d, originalClaim: largeMarketClaim600, peril: claimsGenerator)
        Claim largeClaim900 = new Claim(claimType: ClaimType.SINGLE, value: 900d, fractionOfPeriod: 0.2d, peril: claimsGenerator)
        Claim largeClaim90 = new Claim(claimType: ClaimType.SINGLE, value: 90d, fractionOfPeriod: 0.2d, originalClaim: largeClaim900, peril: claimsGenerator)

        program = new ReinsuranceWithBouquetCommissionProgram()

        MultiCoverAttributeReinsuranceContract quotaShare = getQuotaShare(20,0)
        MultiCoverAttributeReinsuranceContract wxl20xs10 = getWXL(10)
        MultiCoverAttributeReinsuranceContract wxl20xs30 = getWXL(30)
        MultiCoverAttributeReinsuranceContract wxl20xs50 = getWXL(50)

        wxl20xs50.name = "subContract0"
        wxl20xs10.name = "subContract1"
        quotaShare.name = "subContract2"
        wxl20xs30.name = "subContract3"
        program.subContracts.addSubComponent(wxl20xs50)
        program.subContracts.addSubComponent(wxl20xs10)
        program.subContracts.addSubComponent(quotaShare)
        program.subContracts.addSubComponent(wxl20xs30)
        program.inClaims << attrClaim100 << largeClaim60 << largeClaim90
        UnderwritingInfo underwritingInfo = CommissionTests.getUnderwritingInfoFromSelf(
                                            premium: 2000, numberOfPolicies: 100, exposureDefinition: Exposure.ABSOLUTE)
        underwritingInfo.originalUnderwritingInfo = underwritingInfo
        program.inUnderwritingInfo << underwritingInfo

        program.internalWiring()

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

    void testUsageSerialContractOrderWithCommissions() {
        program = new ReinsuranceWithBouquetCommissionProgram()

        MultiCoverAttributeReinsuranceContract quotaShare1 = getQuotaShare(20,9); quotaShare1.name = "qs1"
        MultiCoverAttributeReinsuranceContract quotaShare2 = getQuotaShare(10,1); quotaShare2.name = "qs2"
        MultiCoverAttributeReinsuranceContract quotaShare3 = getQuotaShare(15,5); quotaShare3.name = "qs3"

        SimulationScope simulationScope = CommissionTests.getTestSimulationScope(2010)

        Commission commission1 = new Commission(
            parmCommissionStrategy:
                CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION,
                    [commission: 0.1d]),
            parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT,
                    [applicableContracts: new ComboBoxTableMultiDimensionalParameter(
                        ['qs1', 'qs2'], ['Applicable Contracts'], IReinsuranceContractMarker)]),
            simulationScope: simulationScope,
            name: 'commission 1'
        )
        Commission commission2 = new Commission(
            parmCommissionStrategy:
                CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION,
                    [commission: 0.2d]),
            parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT,
                    [applicableContracts: new ComboBoxTableMultiDimensionalParameter(
                        ['qs3'], ['Applicable Contracts'], IReinsuranceContractMarker)]),
            simulationScope: simulationScope,
            name: 'commission 2'
        )

        // cf: CommissionTests.getUnderwritingInfoFromOrigin; here, we want the origin and its copy to have different fields (..AsIf, #ofPolicies, exposure).
        Component origin1 = new TestComponent()
        UnderwritingInfo uIOrigin1 = new UnderwritingInfo(origin: origin1,
                                                          premium: 2000, numberOfPolicies: 100, exposureDefinition: Exposure.ABSOLUTE)
        UnderwritingInfo uwInfo1 = UnderwritingInfoPacketFactory.copy(uIOrigin1)
        uwInfo1.originalUnderwritingInfo = uIOrigin1
        uwInfo1.origin = origin1

        program.subContracts.addSubComponent quotaShare1
        program.subContracts.addSubComponent quotaShare2
        program.subContracts.addSubComponent quotaShare3
        program.subCommissions.addSubComponent commission1
        program.subCommissions.addSubComponent commission2
        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << uwInfo1
        program.internalWiring()

        for (Component component : program.subContracts.componentList) {
            simulationScope.model.allComponents << component
        }
        commission1.parmApplicableStrategy.applicableContracts.setSimulationModel simulationScope.model
        commission2.parmApplicableStrategy.applicableContracts.setSimulationModel simulationScope.model

        def probeQS1net = new TestProbe(quotaShare1, "outUncoveredClaims")
        def probeQS2net = new TestProbe(quotaShare2, "outUncoveredClaims")
        def probeQS3net = new TestProbe(quotaShare3, "outUncoveredClaims")

        def probeQS1ceded = new TestProbe(quotaShare1, "outCoveredClaims")
        def probeQS2ceded = new TestProbe(quotaShare2, "outCoveredClaims")
        def probeQS3ceded = new TestProbe(quotaShare3, "outCoveredClaims")

        List qs1ClaimsNet = probeQS1net.result
        List qs2ClaimsNet = probeQS2net.result
        List qs3ClaimsNet = probeQS3net.result

        List qs1ClaimsCeded = probeQS1ceded.result
        List qs2ClaimsCeded = probeQS2ceded.result
        List qs3ClaimsCeded = probeQS3ceded.result

        def probeProgramClaimsNet = new TestProbe(program, "outClaimsNet")
        List programClaimsNet = probeProgramClaimsNet.result

        def probeProgramClaimsCeded = new TestProbe(program, "outClaimsCeded")
        List programClaimsCeded = probeProgramClaimsCeded.result

        def probeProgramUWInfoGross = new TestProbe(program, "outUnderwritingInfo")
        List programPremiumGross = probeProgramUWInfoGross.result

        def probeProgramUWInfoCeded = new TestProbe(program, "outCoverUnderwritingInfo")
        List programPremiumCeded = probeProgramUWInfoCeded.result

        def probeProgramUWInfoNet = new TestProbe(program, "outNetAfterCoverUnderwritingInfo")
        List programPremiumNet = probeProgramUWInfoNet.result

        Map<String, Map<String, Object>> results = [
            'qs2.outCoveredClaims': ['component': quotaShare2, 'channel': 'outCoveredClaims', 'values': ['ultimate': [10, 6]], 'count': 2],
            'qs3.outCoveredClaims': ['component': quotaShare3, 'channel': 'outCoveredClaims', 'values': ['ultimate': [13.5, 8.1]], 'count': 2],
            'qs1.outCoveredClaims': ['component': quotaShare1, 'channel': 'outCoveredClaims', 'values': ['ultimate': [15.3, 9.18]], 'count': 2],
            'qs2.outUncoveredClaims': ['component': quotaShare2, 'channel': 'outUncoveredClaims', 'values': ['ultimate': [90, 54]], 'count': 2],
            'qs3.outUncoveredClaims': ['component': quotaShare3, 'channel': 'outUncoveredClaims', 'values': ['ultimate': [76.5, 45.9]], 'count': 2],
            'qs1.outUncoveredClaims': ['component': quotaShare1, 'channel': 'outUncoveredClaims', 'values': ['ultimate': [61.2, 36.72]], 'count': 2],
            'program.Contracts.outClaimsGross': ['component': program.subContracts, 'channel': 'outClaimsGross', 'count': 2],
            'program.Contracts.outClaimsCeded': ['component': program.subContracts, 'channel': 'outClaimsCeded', 'count': 6],
            'program.Contracts.outClaimsNet': ['component': program.subContracts, 'channel': 'outClaimsNet', 'count': 2],
            'program.Contracts.outCoverUnderwritingInfo': ['component': program.subContracts, 'channel': 'outCoverUnderwritingInfo', 'values': ['premium': [200,270,306], 'commission': [-20,-54,-30.6]], 'count': 3],
            'program.Contracts.outNetAfterCoverUnderwritingInfo': ['component': program.subContracts, 'channel': 'outNetAfterCoverUnderwritingInfo', 'values': ['premium': [1224], 'commission': [0]], 'count': 1],
            'program.uwInfoMerger.outUnderwritingInfoGross': ['component': program.underwritingInfoMerger, 'channel': 'outUnderwritingInfoGross', 'count': 1],
            'program.uwInfoMerger.outUnderwritingInfoCeded': ['component': program.underwritingInfoMerger, 'channel': 'outUnderwritingInfoCeded', 'count': 1],
            'program.uwInfoMerger.outUnderwritingInfoNet': ['component': program.underwritingInfoMerger, 'channel': 'outUnderwritingInfoNet', 'count': 1],
            'program.Commissions.outUnderwritingInfoModified': ['component': program.subCommissions, 'channel': 'outUnderwritingInfoModified', 'values': ['premium': [270,200,306], 'commission': [-54,-20,-30.6]], 'count': 3],
            'program.Commissions.outUnderwritingInfoUnmodified': ['component': program.subCommissions, 'channel': 'outUnderwritingInfoUnmodified', 'count': 0],
            'program.financialsAggregator.outContractFinancials': ['component': program.financialsAggregator, 'channel': 'outContractFinancials', 'count': 1],
            'program.outCoverUnderwritingInfo': ['component': program, 'channel': 'outCoverUnderwritingInfo', 'values': ['premium': [776], 'commission': [-104.6]], 'count': 1],
            'program.outUnderwritingInfo': ['component': program, 'channel': 'outUnderwritingInfo', 'values': ['premium': [2000], 'commission': [0]], 'count': 1],
            'program.outNetAfterCoverUnderwritingInfo': ['component': program, 'channel': 'outNetAfterCoverUnderwritingInfo', 'values': ['premium': [1224], 'commission': [104.6]], 'count': 1],
        ]
        for (Map.Entry<String, Map<String, Object>> entry : results.entrySet()) {
            Map<Component, String> map = (Map<Component, String>) entry.getValue()
            def testProbe = new TestProbe((Component) map.getAt('component'), (String) map.getAt('channel'))
            map.putAt('list', testProbe.result)
            map.remove('component')
            map.remove('channel')
        }

        program.start()

        // check expected packet counts
        for (Map.Entry<String, Map<String, Object>> entry : results.entrySet()) {
            String resultName = entry.getKey()
            Map<Component, String> map = (Map<Component, String>) entry.getValue()
            List resultList = (List) map.getAt('list')
            if (map.containsKey('count')) {
                assertEquals "# $resultName", map.getAt('count'), resultList.size()
            }
            if (map.containsKey('values')) {
                for (Map.Entry<String, Object> values : map.getAt('values').entrySet()) {
                    String propertyName = values.getKey()
                    List valueList = (List) values.getValue()
                    int valueCount = valueList.size()
                    assertEquals "# $resultName (value count)", valueCount, resultList.size()
                    for (int i=0; i<valueCount; i++) {
                        double resultFound = (double) resultList[i].getProperties().getAt(propertyName)
                        if (valueList[i] instanceof List) {
                            List<Double> valueWithError = (List<Double>) valueList[i]
                            assertEquals "$resultName[$i].$propertyName", valueWithError[0], resultFound, valueWithError[1]
                        } else {
                            assertEquals "$resultName[$i].$propertyName", valueList[i], resultFound
                        }
                    }
                }
            }
        }

        assertEquals "# quotaShare1 net claims", 2, qs1ClaimsNet.size()
        assertEquals "# quotaShare2 net claims", 2, qs2ClaimsNet.size()
        assertEquals "# quotaShare3 net claims", 2, qs3ClaimsNet.size()

        // check each result array in the same order in which they should have been processed
        assertEquals "qs2, attritional ceded claim 1", 10, qs2ClaimsCeded[0].ultimate
        assertEquals "qs2, attritional net claim 1", 90, qs2ClaimsNet[0].ultimate
        assertEquals "qs3, attritional ceded claim 1", 13.5, qs3ClaimsCeded[0].ultimate
        assertEquals "qs3, attritional net claim 1", 76.5, qs3ClaimsNet[0].ultimate
        assertEquals "qs1, attritional ceded claim 1", 15.3, qs1ClaimsCeded[0].ultimate
        assertEquals "qs1, attritional net claim 1", 61.2, qs1ClaimsNet[0].ultimate

        assertEquals "qs2, attritional ceded claim 2", 6, qs2ClaimsCeded[1].ultimate
        assertEquals "qs2, attritional net claim 2", 54, qs2ClaimsNet[1].ultimate
        assertEquals "qs3, attritional ceded claim 2", 8.1, qs3ClaimsCeded[1].ultimate
        assertEquals "qs3, attritional net claim 2", 45.9, qs3ClaimsNet[1].ultimate
        assertEquals "qs1, attritional ceded claim 2", 9.18, qs1ClaimsCeded[1].ultimate
        assertEquals "qs1, attritional net claim 2", 36.72, qs1ClaimsNet[1].ultimate

        assertEquals "# program ceded claims", 2*3, programClaimsCeded.size()
        assertEquals "# program net claims", 2, programClaimsNet.size()

        assertEquals "program, total net claim", 61.2, programClaimsNet[0].ultimate
        assertEquals "program, total net claim", 36.72, programClaimsNet[1].ultimate
        assertEquals "program, total net claim", 61.2+36.72, getListPropertySum(programClaimsNet, 'ultimate')
        assertEquals "program, total ceded claim", 38.8+23.28, getListPropertySum(programClaimsCeded, 'ultimate'), 1E-13

        assertEquals "# program premium written gross", 1, programPremiumGross.size()
        assertEquals "# program premium written ceded", 1, programPremiumCeded.size()
        assertEquals "# program premium written net", 1, programPremiumNet.size()

        assertEquals "program, commission on ceded premium", -104.6, programPremiumCeded[0].commission
        assertEquals "program, commission on net premium", 104.6, programPremiumNet[0].commission
    }

}