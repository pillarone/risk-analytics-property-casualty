package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.ICoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.claims.TestLobComponent
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.NoneCoverAttributeStrategy
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments
import org.pillarone.riskanalytics.domain.pc.claims.TestPerilComponent
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.constraints.SegmentPortion
import org.pillarone.riskanalytics.domain.pc.claims.TestReserveComponent
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class PremiumAllocationTypeTests extends GroovyTestCase {

    IReinsuranceContractStrategy wxlContract1
    IReinsuranceContractStrategy cxlContract1
    IReinsuranceContractStrategy wcxlContract1

    IReinsuranceContractStrategy wxlContract2
    IReinsuranceContractStrategy cxlContract2
    IReinsuranceContractStrategy wcxlContract2

    IReinsuranceContractStrategy wxlContract3
    IReinsuranceContractStrategy cxlContract3
    IReinsuranceContractStrategy wcxlContract3

    ICommissionStrategy fixedCommission
    ICommissionStrategy slidingCommission
    ICommissionStrategy profitCommission

    static MultiCoverAttributeReinsuranceContract getMultiCoverAttributeReinsuranceContract(IReinsuranceContractStrategy contractStrategy,
                                                                                            ICoverAttributeStrategy coverStrategy,
                                                                                            ICommissionStrategy commissionStrategy) {
        MultiCoverAttributeReinsuranceContract contract = new MultiCoverAttributeReinsuranceContract(
                parmContractStrategy: contractStrategy,
                parmCover: coverStrategy,
                parmCommissionStrategy: commissionStrategy
        )
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        return contract
    }

    void setUp() {

        ConstraintsFactory.registerConstraint(new SegmentPortion())

        wxlContract1 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.WXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        cxlContract1 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.CXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        wcxlContract1 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.WCXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        wxlContract2 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.WXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.CLAIMS_SHARES, new HashMap()),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        cxlContract2 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.CXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.CLAIMS_SHARES, new HashMap()),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        wcxlContract2 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.WCXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.CLAIMS_SHARES, new HashMap()),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        wxlContract3 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.WXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.LINE_SHARES,
                                ['lineOfBusinessShares': new ConstrainedMultiDimensionalParameter([['motor', 'property'], [0.4d, 0.6d]],
                                        ["Business Lines", "shares"], ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))]),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        cxlContract3 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.CXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.LINE_SHARES,
                                ['lineOfBusinessShares': new ConstrainedMultiDimensionalParameter([['motor', 'property'], [0.4d, 0.6d]],
                                        ["Business Lines", "shares"], ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))]),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        wcxlContract3 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.WCXL,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "aggregateLimit": 3000,
                        "aggregateDeductible": 0,
                        "premiumBase": PremiumBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.LINE_SHARES,
                                ['lineOfBusinessShares': new ConstrainedMultiDimensionalParameter([['motor', 'property'], [0.4d, 0.6d]],
                                        ["Business Lines", "shares"], ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))]),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        fixedCommission = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0.1d])
    }


    static ICoverAttributeStrategy getCoverAttributeStrategy(Map<String, List<String>> cover, Model model = null) {
        boolean hasLines = cover.containsKey('lines')
        boolean hasPerils = cover.containsKey('perils')
        boolean hasReserves = cover.containsKey('reserves')

        ComboBoxTableMultiDimensionalParameter lines = hasLines ? new ComboBoxTableMultiDimensionalParameter(cover['lines'], ['Covered Lines'], LobMarker) : null
        ComboBoxTableMultiDimensionalParameter perils = hasPerils ? new ComboBoxTableMultiDimensionalParameter(cover['perils'], ['Covered Perils'], PerilMarker) : null
        ComboBoxTableMultiDimensionalParameter reserves = hasReserves ? new ComboBoxTableMultiDimensionalParameter(cover['reserves'], ['Covered Reserves'], LobMarker) : null

        // each of the strategy-specific ComboBoxTableMultiDimensionalParameter properties needs to set the simulation model (to simulate a choice from the GUI)
        if (model != null) {
            if (hasLines) {lines.setSimulationModel model}
            if (hasPerils) {perils.setSimulationModel model}
            if (hasReserves) {reserves.setSimulationModel model}
        }

        // return one of:
        hasLines && hasReserves ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESSRESERVES, ['connection': LogicArguments.AND, 'lines': lines, 'reserves': reserves]) :
            hasLines && hasPerils ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESSPERILS, ['connection': LogicArguments.AND, 'lines': lines, 'perils': perils]) :
                hasLines ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ['lines': lines]) :
                    hasReserves ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.RESERVES, ['reserves': reserves]) :
                        hasPerils ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.PERILS, ['perils': perils]) :
                            new NoneCoverAttributeStrategy()
    }

    /**
     * Create TestLobComponents with names (given a list of LOB names);
     * add them to a model (if specified);
     * return them in a map with name as key.
     */
    static Map<String, TestLobComponent> createLobs(List<String> lobNames, Model model = null) {
        Map<String, TestLobComponent> lob = new HashMap()
        for (String lobName: lobNames) {
            lob.put(lobName, new TestLobComponent(name: lobName))
            if (model != null) {
                model.allComponents << lob[lobName]
            }
        }
        return lob
    }

    void testPremiumShares() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestReserveComponent reserveA = new TestReserveComponent(name: 'reserve a')
        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')
        TestPerilComponent perilD = new TestPerilComponent(name: 'peril d')
        simulationScope.model.allComponents << perilA << perilB << perilC << perilD << reserveA
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor', 'property', 'legal'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract1,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model),
                fixedCommission)

        Claim claim0 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim1 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.ATTRITIONAL)
        Claim claim2 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim3 = new Claim(peril: perilB, lineOfBusiness: lob['motor'], value: 200d, claimType: ClaimType.SINGLE)
        Claim claim4 = new Claim(peril: perilC, lineOfBusiness: lob['motor'], value: 400d, claimType: ClaimType.SINGLE)
        Claim claim5 = new Claim(peril: perilA, lineOfBusiness: lob['property'], value: 600d, claimType: ClaimType.SINGLE)
        Claim claim6 = new Claim(peril: perilB, lineOfBusiness: lob['property'], value: 800d, claimType: ClaimType.SINGLE)
        Claim claim7 = new Claim(peril: perilD, lineOfBusiness: lob['property'], value: 2100d, claimType: ClaimType.SINGLE)
        Claim claim8 = new Claim(peril: reserveA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor = new UnderwritingInfo(premiumWritten: 1000, commission: 10, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty = new UnderwritingInfo(premiumWritten: 10000, commission: 30, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premiumWritten: 500, commission: 30, lineOfBusiness: lob['legal'])

        wxlContract1.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        wxlContract1.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 5, wxlContract1.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wxlContract1.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wxlContract1.outCoverUnderwritingInfo.size()

        List<Double> netPremiumWxl1 = []
        for (UnderwritingInfo underwritingInfo in wxlContract1.outNetAfterCoverUnderwritingInfo) {
            netPremiumWxl1.add(underwritingInfo.premiumWritten)
        }
        List<Double> cededPremiumWxl1 = []
        for (UnderwritingInfo underwritingInfo in wxlContract1.outCoverUnderwritingInfo) {
            cededPremiumWxl1.add(underwritingInfo.premiumWritten)
        }
        List<Double> commissionWxl1 = []
        for (UnderwritingInfo underwritingInfo in wxlContract1.outCoverUnderwritingInfo) {
            commissionWxl1.add(underwritingInfo.commission)
        }
//        assertEquals "ceded premium", true, cededPremiumWxl1.containsAll(new Double(1 / 9d * 5000), new Double(8 / 9d * 5000))
//        assertEquals "commission", true, commissionWxl1.containsAll(new Double(-0.1 / 9d * 5000), new Double(-0.1 * 8 / 9d * 5000))
//        assertEquals "net premium", true, netPremiumWxl1.containsAll(new Double(1000 - 1 / 9d * 5000), new Double(10000 - 8 / 9d * 5000))

        MultiCoverAttributeReinsuranceContract cxlContract1 = getMultiCoverAttributeReinsuranceContract(cxlContract1,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model),
                fixedCommission)

        cxlContract1.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        cxlContract1.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outUI = new TestProbe(cxlContract1, 'outCoverUnderwritingInfo')

        cxlContract1.doCalculation()

        assertEquals "# of filtered claims", 5, cxlContract1.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, cxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, cxlContract1.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, cxlContract1.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumCxl1 = []
        for (UnderwritingInfo underwritingInfo in cxlContract1.outCoverUnderwritingInfo) {
            cededPremiumCxl1.add(underwritingInfo.premiumWritten)
        }
        List<Double> commissionCxl1 = []
        for (UnderwritingInfo underwritingInfo in cxlContract1.outCoverUnderwritingInfo) {
            commissionCxl1.add(underwritingInfo.commission)
        }

//        assertEquals "commission", true, commissionCxl1.containsAll(new Double(-0.1 / 9d * 5000), new Double(-0.1 * 8 / 9d * 5000))
//        assertEquals "ceded premium", true, cededPremiumCxl1.containsAll(new Double(1 / 9d * 5000), new Double(8 / 9d * 5000))

        MultiCoverAttributeReinsuranceContract wcxlContract1 = getMultiCoverAttributeReinsuranceContract(wcxlContract1,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model),
                fixedCommission)

        wcxlContract1.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        wcxlContract1.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def UI = new TestProbe(wcxlContract1, 'outCoverUnderwritingInfo')

        wcxlContract1.doCalculation()

        assertEquals "# of filtered claims", 5, wcxlContract1.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, wcxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wcxlContract1.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wcxlContract1.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumWcxl1 = []
        for (UnderwritingInfo underwritingInfo in wcxlContract1.outCoverUnderwritingInfo) {
            cededPremiumWcxl1.add(underwritingInfo.premiumWritten)
        }
        List<Double> commissionWcxl1 = []
        for (UnderwritingInfo underwritingInfo in wcxlContract1.outCoverUnderwritingInfo) {
            commissionWcxl1.add(underwritingInfo.commission)
        }

//        assertEquals "commission", true, commissionWcxl1.containsAll(new Double(-0.1 / 9d * 5000), new Double(-0.1 * 8 / 9d * 5000))
//        assertEquals "ceded premium", true, cededPremiumWcxl1.containsAll(new Double(1 / 9d * 5000), new Double(8 / 9d * 5000))

    }

    void testClaimShares() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestReserveComponent reserveA = new TestReserveComponent(name: 'reserve a')
        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')
        TestPerilComponent perilD = new TestPerilComponent(name: 'peril d')
        simulationScope.model.allComponents << perilA << perilB << perilC << perilD
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor', 'property', 'legal'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract2 = getMultiCoverAttributeReinsuranceContract(wxlContract2,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        Claim claim0 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim1 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.ATTRITIONAL)
        Claim claim2 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim3 = new Claim(peril: perilB, lineOfBusiness: lob['motor'], value: 200d, claimType: ClaimType.SINGLE)
        Claim claim4 = new Claim(peril: perilC, lineOfBusiness: lob['motor'], value: 400d, claimType: ClaimType.SINGLE)
        Claim claim5 = new Claim(peril: perilA, lineOfBusiness: lob['property'], value: 600d, claimType: ClaimType.SINGLE)
        Claim claim6 = new Claim(peril: perilB, lineOfBusiness: lob['property'], value: 800d, claimType: ClaimType.SINGLE)
        Claim claim7 = new Claim(peril: perilD, lineOfBusiness: lob['property'], value: 2100d, claimType: ClaimType.SINGLE)
        Claim claim8 = new Claim(peril: reserveA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor = new UnderwritingInfo(premiumWritten: 1000, commission: 10, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty = new UnderwritingInfo(premiumWritten: 10000, commission: 30, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premiumWritten: 500, commission: 30, lineOfBusiness: lob['legal'])

        wxlContract2.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        wxlContract2.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outCoverUI = new TestProbe(wxlContract2, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract2, 'outNetAfterCoverUnderwritingInfo')

        wxlContract2.doCalculation()

        assertEquals "# of filtered claims", 5, wxlContract2.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, wxlContract2.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wxlContract2.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wxlContract2.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumWxl2 = []
        for (UnderwritingInfo underwritingInfo in wxlContract2.outCoverUnderwritingInfo) {
            cededPremiumWxl2.add(underwritingInfo.premiumWritten)
        }
        List<Double> commissionWxl2 = []
        for (UnderwritingInfo underwritingInfo in wxlContract2.outCoverUnderwritingInfo) {
            commissionWxl2.add(underwritingInfo.commission)
        }
        List<Double> netPremiumWxl2 = []
        for (UnderwritingInfo underwritingInfo in wxlContract2.outNetAfterCoverUnderwritingInfo) {
            netPremiumWxl2.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "net premium", true, netPremiumWxl2.containsAll(new Double(1000 - 2 / 9d * 5000), new Double(10000 - 7 / 9d * 5000))
//        assertEquals "commission", true, commissionWxl2.containsAll(new Double(-0.2 / 9d * 5000), new Double(-0.1 * 7 / 9d * 5000))
//        assertEquals "ceded premium", true, cededPremiumWxl2.containsAll(new Double(2 / 9d * 5000), new Double(7 / 9d * 5000))

        MultiCoverAttributeReinsuranceContract cxlContract2 = getMultiCoverAttributeReinsuranceContract(cxlContract2,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        cxlContract2.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        cxlContract2.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outUI = new TestProbe(cxlContract2, 'outCoverUnderwritingInfo')

        cxlContract2.doCalculation()

        assertEquals "# of filtered claims", 5, cxlContract2.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, cxlContract2.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, cxlContract2.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, cxlContract2.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumCxl2 = []
        for (UnderwritingInfo underwritingInfo in cxlContract2.outCoverUnderwritingInfo) {
            cededPremiumCxl2.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumCxl2.containsAll(new Double(2 / 9d * 5000), new Double(7 / 9d * 5000))

        MultiCoverAttributeReinsuranceContract wcxlContract2 = getMultiCoverAttributeReinsuranceContract(wcxlContract2,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        wcxlContract2.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        wcxlContract2.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def UI = new TestProbe(wcxlContract2, 'outCoverUnderwritingInfo')

        wcxlContract2.doCalculation()

        assertEquals "# of filtered claims", 5, wcxlContract2.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, wcxlContract2.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wcxlContract2.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wcxlContract2.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumWcxl2 = []
        for (UnderwritingInfo underwritingInfo in wcxlContract2.outCoverUnderwritingInfo) {
            cededPremiumWcxl2.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumWcxl2.containsAll(new Double(2 / 9d * 5000), new Double(7 / 9d * 5000))

    }

    void testLineShares() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestReserveComponent reserveA = new TestReserveComponent(name: 'reserve a')
        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')
        TestPerilComponent perilD = new TestPerilComponent(name: 'peril d')
        simulationScope.model.allComponents << perilA << perilB << perilC << perilD
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor', 'property', 'legal'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract3 = getMultiCoverAttributeReinsuranceContract(wxlContract3,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        Claim claim0 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim1 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.ATTRITIONAL)
        Claim claim2 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim3 = new Claim(peril: perilB, lineOfBusiness: lob['motor'], value: 200d, claimType: ClaimType.SINGLE)
        Claim claim4 = new Claim(peril: perilC, lineOfBusiness: lob['motor'], value: 400d, claimType: ClaimType.SINGLE)
        Claim claim5 = new Claim(peril: perilA, lineOfBusiness: lob['property'], value: 600d, claimType: ClaimType.SINGLE)
        Claim claim6 = new Claim(peril: perilB, lineOfBusiness: lob['property'], value: 800d, claimType: ClaimType.SINGLE)
        Claim claim7 = new Claim(peril: perilD, lineOfBusiness: lob['property'], value: 2100d, claimType: ClaimType.SINGLE)
        Claim claim8 = new Claim(peril: reserveA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor = new UnderwritingInfo(premiumWritten: 1000, commission: 10, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty = new UnderwritingInfo(premiumWritten: 10000, commission: 30, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premiumWritten: 500, commission: 30, lineOfBusiness: lob['legal'])

        wxlContract3.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        wxlContract3.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outCoverUI = new TestProbe(wxlContract3, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract3, 'outNetAfterCoverUnderwritingInfo')

        wxlContract3.doCalculation()

        assertEquals "# of filtered claims", 5, wxlContract3.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, wxlContract3.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wxlContract3.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wxlContract3.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumWxl3 = []
        for (UnderwritingInfo underwritingInfo in wxlContract3.outCoverUnderwritingInfo) {
            cededPremiumWxl3.add(underwritingInfo.premiumWritten)
        }
        List<Double> commissionWxl3 = []
        for (UnderwritingInfo underwritingInfo in wxlContract3.outCoverUnderwritingInfo) {
            commissionWxl3.add(underwritingInfo.commission)
        }
        List<Double> netPremiumWxl3 = []
        for (UnderwritingInfo underwritingInfo in wxlContract3.outNetAfterCoverUnderwritingInfo) {
            netPremiumWxl3.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "net premium", true, netPremiumWxl3.containsAll(new Double(1000 - 0.4 * 5000), new Double(10000 - 0.6 * 5000))
//        assertEquals "commission", true, commissionWxl3.containsAll(new Double(-0.1 * 0.4 * 5000), new Double(-0.1 * 0.6 * 5000))
//        assertEquals "ceded premium", true, cededPremiumWxl3.containsAll(new Double(0.4 * 5000), new Double(0.6 * 5000))

        MultiCoverAttributeReinsuranceContract cxlContract3 = getMultiCoverAttributeReinsuranceContract(cxlContract3,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        cxlContract3.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7
        cxlContract3.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outUI = new TestProbe(cxlContract3, 'outCoverUnderwritingInfo')

        cxlContract3.doCalculation()

        assertEquals "# of filtered claims", 5, cxlContract3.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, cxlContract3.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, cxlContract3.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, cxlContract3.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumCxl3 = []
        for (UnderwritingInfo underwritingInfo in cxlContract3.outCoverUnderwritingInfo) {
            cededPremiumCxl3.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumCxl3.containsAll(new Double(0.4 * 5000), new Double(0.6 * 5000))

        MultiCoverAttributeReinsuranceContract wcxlContract3 = getMultiCoverAttributeReinsuranceContract(wcxlContract3,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        wcxlContract3.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7
        wcxlContract3.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def UI = new TestProbe(wcxlContract3, 'outCoverUnderwritingInfo')

        wcxlContract3.doCalculation()

        assertEquals "# of filtered claims", 5, wcxlContract3.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, wcxlContract3.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wcxlContract3.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wcxlContract3.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumWcxl3 = []
        for (UnderwritingInfo underwritingInfo in wcxlContract3.outCoverUnderwritingInfo) {
            cededPremiumWcxl3.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumWcxl3.containsAll(new Double(0.4 * 5000), new Double(0.6 * 5000))

    }

    void testExcludingReserves() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestReserveComponent reserveA = new TestReserveComponent(name: 'reserve a')
        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')
        TestPerilComponent perilD = new TestPerilComponent(name: 'peril d')
        simulationScope.model.allComponents << perilA << perilB << perilC << perilD << reserveA
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor', 'property', 'legal'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract1,
                getCoverAttributeStrategy(['lines': ['motor', 'property']], simulationScope.model), fixedCommission)

        Claim claim0 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim1 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.ATTRITIONAL)
        Claim claim2 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim3 = new Claim(peril: perilB, lineOfBusiness: lob['motor'], value: 200d, claimType: ClaimType.SINGLE)
        Claim claim4 = new Claim(peril: perilC, lineOfBusiness: lob['motor'], value: 400d, claimType: ClaimType.SINGLE)
        Claim claim5 = new Claim(peril: perilA, lineOfBusiness: lob['property'], value: 600d, claimType: ClaimType.SINGLE)
        Claim claim6 = new Claim(peril: perilB, lineOfBusiness: lob['property'], value: 800d, claimType: ClaimType.SINGLE)
        Claim claim7 = new Claim(peril: perilD, lineOfBusiness: lob['property'], value: 2100d, claimType: ClaimType.SINGLE)
        Claim claim8 = new Claim(peril: reserveA, lineOfBusiness: lob['motor'], value: 1000d, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor = new UnderwritingInfo(premiumWritten: 1000, commission: 10, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty = new UnderwritingInfo(premiumWritten: 10000, commission: 30, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premiumWritten: 500, commission: 30, lineOfBusiness: lob['legal'])

        wxlContract1.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        wxlContract1.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')

        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 8, wxlContract1.outFilteredClaims.size()
        assertEquals "# of covered claims", 8, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wxlContract1.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wxlContract1.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumWxl1 = []
        for (UnderwritingInfo underwritingInfo in wxlContract1.outCoverUnderwritingInfo) {
            cededPremiumWxl1.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumWxl1.containsAll(new Double(1 / 11d * 5000), new Double(10 / 11d * 5000))

        MultiCoverAttributeReinsuranceContract cxlContract2 = getMultiCoverAttributeReinsuranceContract(cxlContract2,
                getCoverAttributeStrategy(['lines': ['motor', 'property']], simulationScope.model), fixedCommission)

        cxlContract2.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        cxlContract2.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outUI = new TestProbe(cxlContract2, 'outCoverUnderwritingInfo')

        cxlContract2.doCalculation()

        assertEquals "# of filtered claims", 8, cxlContract2.outFilteredClaims.size()
        assertEquals "# of covered claims", 8, cxlContract2.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, cxlContract2.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, cxlContract2.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumCxl2 = []
        for (UnderwritingInfo underwritingInfo in cxlContract2.outCoverUnderwritingInfo) {
            cededPremiumCxl2.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumCxl2.containsAll(new Double(8 / 43d * 5000), new Double(35 / 43d * 5000))


        MultiCoverAttributeReinsuranceContract wcxlContract3 = getMultiCoverAttributeReinsuranceContract(wcxlContract3,
                getCoverAttributeStrategy(['lines': ['motor', 'property']], simulationScope.model), fixedCommission)

        wcxlContract3.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        wcxlContract3.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def UI = new TestProbe(wcxlContract3, 'outCoverUnderwritingInfo')

        wcxlContract3.doCalculation()

        assertEquals "# of filtered claims", 8, wcxlContract3.outFilteredClaims.size()
        assertEquals "# of covered claims", 8, wcxlContract3.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wcxlContract3.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wcxlContract3.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumWcxl3 = []
        for (UnderwritingInfo underwritingInfo in wcxlContract3.outCoverUnderwritingInfo) {
            cededPremiumWcxl3.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumWcxl3.containsAll(new Double(0.4 * 5000), new Double(0.6 * 5000))

    }

    void testPremiumAllocationStopLoss() {

        IReinsuranceContractStrategy stopLoss1 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.STOPLOSS,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "stopLossContractBase": StopLossContractBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                        "premium": 5000,
                        "coveredByReinsurer": 1d])

        IReinsuranceContractStrategy stopLoss2 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.STOPLOSS,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "stopLossContractBase": StopLossContractBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.CLAIMS_SHARES, new HashMap()),
                        "premium": 5000,
                        "coveredByReinsurer": 1d])

        IReinsuranceContractStrategy stopLoss3 = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.STOPLOSS,
                ["attachmentPoint": 0,
                        "limit": 3000,
                        "stopLossContractBase": StopLossContractBase.ABSOLUTE,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.LINE_SHARES,
                                ['lineOfBusinessShares': new ConstrainedMultiDimensionalParameter([['motor', 'property'], [0.4d, 0.6d]],
                                        ["Business Lines", "shares"], ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))]),
                        "premium": 5000,
                        "coveredByReinsurer": 1d])

        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestReserveComponent reserveA = new TestReserveComponent(name: 'reserve a')
        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')
        TestPerilComponent perilD = new TestPerilComponent(name: 'peril d')
        simulationScope.model.allComponents << perilA << perilB << perilC << perilD << reserveA
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor', 'property', 'legal'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract stopLossContract1 = getMultiCoverAttributeReinsuranceContract(stopLoss1,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        MultiCoverAttributeReinsuranceContract stopLossContract2 = getMultiCoverAttributeReinsuranceContract(stopLoss2,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        MultiCoverAttributeReinsuranceContract stopLossContract3 = getMultiCoverAttributeReinsuranceContract(stopLoss3,
                getCoverAttributeStrategy(['lines': ['motor', 'property'], 'perils': ['peril a', 'peril b']], simulationScope.model), fixedCommission)

        Claim claim0 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim1 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.ATTRITIONAL)
        Claim claim2 = new Claim(peril: perilA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)
        Claim claim3 = new Claim(peril: perilB, lineOfBusiness: lob['motor'], value: 200d, claimType: ClaimType.SINGLE)
        Claim claim4 = new Claim(peril: perilC, lineOfBusiness: lob['motor'], value: 400d, claimType: ClaimType.SINGLE)
        Claim claim5 = new Claim(peril: perilA, lineOfBusiness: lob['property'], value: 600d, claimType: ClaimType.SINGLE)
        Claim claim6 = new Claim(peril: perilB, lineOfBusiness: lob['property'], value: 800d, claimType: ClaimType.SINGLE)
        Claim claim7 = new Claim(peril: perilD, lineOfBusiness: lob['property'], value: 2100d, claimType: ClaimType.SINGLE)
        Claim claim8 = new Claim(peril: reserveA, lineOfBusiness: lob['motor'], value: 100d, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor = new UnderwritingInfo(premiumWritten: 1000, commission: 10, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty = new UnderwritingInfo(premiumWritten: 10000, commission: 30, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premiumWritten: 500, commission: 30, lineOfBusiness: lob['legal'])

        stopLossContract1.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        stopLossContract1.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal
        stopLossContract2.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        stopLossContract2.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal
        stopLossContract3.inClaims << claim0 << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        stopLossContract3.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoProperty << underwritingInfoLegal

        def outCoverUI = new TestProbe(stopLossContract1, 'outCoverUnderwritingInfo')
        def outUI = new TestProbe(stopLossContract2, 'outCoverUnderwritingInfo')
        def coverUI = new TestProbe(stopLossContract3, 'outCoverUnderwritingInfo')

        stopLossContract1.doCalculation()

        assertEquals "# of filtered claims", 5, stopLossContract1.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, stopLossContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, stopLossContract1.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, stopLossContract1.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumStopLoss1 = []
        for (UnderwritingInfo underwritingInfo in stopLossContract1.outCoverUnderwritingInfo) {
            cededPremiumStopLoss1.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumStopLoss1.containsAll(new Double(1 / 9d * 5000), new Double(8 / 9d * 5000))

        stopLossContract2.doCalculation()

        assertEquals "# of filtered claims", 5, stopLossContract2.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, stopLossContract2.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, stopLossContract2.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, stopLossContract2.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumStopLoss2 = []
        for (UnderwritingInfo underwritingInfo in stopLossContract2.outCoverUnderwritingInfo) {
            cededPremiumStopLoss2.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumStopLoss2.containsAll(new Double(2 / 9d * 5000), new Double(7 / 9d * 5000))

        stopLossContract3.doCalculation()

        assertEquals "# of filtered claims", 5, stopLossContract3.outFilteredClaims.size()
        assertEquals "# of covered claims", 5, stopLossContract3.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, stopLossContract3.outFilteredUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, stopLossContract3.outCoverUnderwritingInfo.size()

        List<Double> cededPremiumStopLoss3 = []
        for (UnderwritingInfo underwritingInfo in stopLossContract3.outCoverUnderwritingInfo) {
            cededPremiumStopLoss3.add(underwritingInfo.premiumWritten)
        }
//        assertEquals "ceded premium", true, cededPremiumStopLoss3.containsAll(new Double(0.4 * 5000), new Double(0.6 * 5000))

    }

}
