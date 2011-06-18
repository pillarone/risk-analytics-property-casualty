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
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class PremiumAllocationTypeTests extends GroovyTestCase {

    static final double EPSILON = 1E-10 

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
        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()))
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.periodStore = new PeriodStore(simulationScope.iterationScope.periodScope)
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
                                        ["Segment", "Share"], ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))]),
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
                                        ["Segment", "Share"], ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))]),
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
                                        ["Segment", "Share"], ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))]),
                        "premium": 5000,
                        "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                        "coveredByReinsurer": 1d])

        fixedCommission = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0.1d])
    }


    static ICoverAttributeStrategy getCoverAttributeStrategy(Map<String, List<String>> cover, Model model = null) {
        boolean hasLines = cover.containsKey('lines')
        boolean hasPerils = cover.containsKey('perils')
        boolean hasReserves = cover.containsKey('reserves')

        ComboBoxTableMultiDimensionalParameter lines = hasLines ? new ComboBoxTableMultiDimensionalParameter(cover['lines'], ['Covered Segments'], LobMarker) : null
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

    void testPremiumSharesOneLine() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract1,
                getCoverAttributeStrategy(['lines': ['motor']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.SINGLE)
        Claim claim3 = getClaim(perilB, lob['motor'], 200, 0, ClaimType.SINGLE)
        Claim claim4 = getClaim(perilB, lob['motor'], 400, 0, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor10000 = new UnderwritingInfo(premium: 10000, commission: 100, lineOfBusiness: lob['motor'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor10000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 4, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 4, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium 15000", 3000, wxlContract1.outCoverUnderwritingInfo[0].premium
        assertEquals "ceded underwriting info premium 10000", 2000, wxlContract1.outCoverUnderwritingInfo[1].premium

        assertEquals "net underwriting info premium 15000", 12000, wxlContract1.outNetAfterCoverUnderwritingInfo[0].premium
        assertEquals "net underwriting info premium 10000", 8000, wxlContract1.outNetAfterCoverUnderwritingInfo[1].premium

    }

    void testPremiumSharesTwoLines() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor','property'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract1,
                getCoverAttributeStrategy(['lines': ['motor','property']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.SINGLE)
        Claim claim3 = getClaim(perilB, lob['motor'], 200, 0, ClaimType.SINGLE)
        Claim claim4 = getClaim(perilB, lob['motor'], 400, 0, ClaimType.SINGLE)
        Claim claim5 = getClaim(perilA, lob['property'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim6 = getClaim(perilA, lob['property'], 100, 0, ClaimType.SINGLE)
        Claim claim7 = getClaim(perilB, lob['property'], 200, 0, ClaimType.SINGLE)
        Claim claim8 = getClaim(perilB, lob['property'], 400, 0, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor5000 = new UnderwritingInfo(premium: 5000, commission: 100, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty3000 = new UnderwritingInfo(premium: 3000, commission: 100, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoProperty2000 = new UnderwritingInfo(premium: 2000, commission: 100, lineOfBusiness: lob['property'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor5000 << underwritingInfoProperty3000 << underwritingInfoProperty2000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 8, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 8, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 4, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 4, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium motor 15000", 3000, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium
        assertEquals "ceded underwriting info premium motor 5000", 1000, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "ceded underwriting info premium property 3000", 600, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "ceded underwriting info premium property 2000", 400, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium

        assertEquals "net underwriting info premium motor 15000", 12000, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium
        assertEquals "net underwriting info premium motor 10000", 4000, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "net underwriting info premium property 10000", 2400, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "net underwriting info premium property 10000", 1600, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium
    }

    void testPremiumSharesNoCededClaims() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor','property'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract1,
                getCoverAttributeStrategy(['lines': ['motor','property']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 0, 0, ClaimType.SINGLE)
        Claim claim3 = getClaim(perilB, lob['motor'], 0, 0, ClaimType.SINGLE)
        Claim claim4 = getClaim(perilB, lob['motor'], 0, 0, ClaimType.SINGLE)
        Claim claim5 = getClaim(perilA, lob['property'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim6 = getClaim(perilA, lob['property'], 0, 0, ClaimType.SINGLE)
        Claim claim7 = getClaim(perilB, lob['property'], 0, 0, ClaimType.SINGLE)
        Claim claim8 = getClaim(perilB, lob['property'], 0, 0, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor5000 = new UnderwritingInfo(premium: 5000, commission: 100, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty3000 = new UnderwritingInfo(premium: 3000, commission: 100, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoProperty2000 = new UnderwritingInfo(premium: 2000, commission: 100, lineOfBusiness: lob['property'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor5000 << underwritingInfoProperty3000 << underwritingInfoProperty2000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 8, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 8, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 4, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 4, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium motor 15000", 3000, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium
        assertEquals "ceded underwriting info premium motor 5000", 1000, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "ceded underwriting info premium property 3000", 600, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "ceded underwriting info premium property 2000", 400, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium

        assertEquals "net underwriting info premium motor 15000", 12000, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium
        assertEquals "net underwriting info premium motor 10000", 4000, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "net underwriting info premium property 3000", 2400, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "net underwriting info premium property 2000", 1600, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium
    }

    void testLinesSharesOneLine() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract3,
                getCoverAttributeStrategy(['lines': ['motor']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 100d, 0d, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 100d, 0d, ClaimType.SINGLE)
        Claim claim3 = getClaim(perilB, lob['motor'], 200d, 0d, ClaimType.SINGLE)
        Claim claim4 = getClaim(perilB, lob['motor'], 400d, 0d, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor10000 = new UnderwritingInfo(premium: 10000, commission: 100, lineOfBusiness: lob['motor'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor10000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 4, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 4, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium 15000", 3000, wxlContract1.outCoverUnderwritingInfo[0].premium
        assertEquals "ceded underwriting info premium 10000", 2000, wxlContract1.outCoverUnderwritingInfo[1].premium

        assertEquals "net underwriting info premium 15000", 12000, wxlContract1.outNetAfterCoverUnderwritingInfo[0].premium
        assertEquals "net underwriting info premium 10000", 8000, wxlContract1.outNetAfterCoverUnderwritingInfo[1].premium

    }

    void testLinesSharesTwoLines() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor','property'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract3,
                getCoverAttributeStrategy(['lines': ['motor','property']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim3 = getClaim(perilB, lob['motor'], 200, 0, ClaimType.ATTRITIONAL)
        Claim claim4 = getClaim(perilB, lob['motor'], 400, 0, ClaimType.ATTRITIONAL)
        Claim claim5 = getClaim(perilA, lob['property'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim6 = getClaim(perilA, lob['property'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim7 = getClaim(perilB, lob['property'], 200, 0, ClaimType.ATTRITIONAL)
        Claim claim8 = getClaim(perilB, lob['property'], 400, 0, ClaimType.ATTRITIONAL)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor5000 = new UnderwritingInfo(premium: 5000, commission: 100, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty3000 = new UnderwritingInfo(premium: 3000, commission: 100, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoProperty2000 = new UnderwritingInfo(premium: 2000, commission: 100, lineOfBusiness: lob['property'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor5000 << underwritingInfoProperty3000 << underwritingInfoProperty2000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 8, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 8, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 4, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 4, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium motor 15000", 1500, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium, EPSILON
        assertEquals "ceded underwriting info premium motor 5000", 500, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "ceded underwriting info premium property 3000", 1800, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "ceded underwriting info premium property 2000", 1200, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium

        assertEquals "net underwriting info premium motor 15000", 13500, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium, EPSILON
        assertEquals "net underwriting info premium motor 5000", 4500, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "net underwriting info premium property 3000", 1200, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "net underwriting info premium property 2000", 800, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium
    }

    void testLinesSharesNoCededClaims() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor','property'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract3,
                getCoverAttributeStrategy(['lines': ['motor','property']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim3 = getClaim(perilB, lob['motor'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim4 = getClaim(perilB, lob['motor'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim5 = getClaim(perilA, lob['property'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim6 = getClaim(perilA, lob['property'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim7 = getClaim(perilB, lob['property'], 0, 0, ClaimType.ATTRITIONAL)
        Claim claim8 = getClaim(perilB, lob['property'], 0, 0, ClaimType.ATTRITIONAL)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor5000 = new UnderwritingInfo(premium: 5000, commission: 100, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty3000 = new UnderwritingInfo(premium: 3000, commission: 100, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoProperty2000 = new UnderwritingInfo(premium: 2000, commission: 100, lineOfBusiness: lob['property'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor5000 << underwritingInfoProperty3000 << underwritingInfoProperty2000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 8, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 8, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 4, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 4, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium motor 15000", 1500, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium, EPSILON
        assertEquals "ceded underwriting info premium motor 5000", 500, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "ceded underwriting info premium property 3000", 1800, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "ceded underwriting info premium property 2000", 1200, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium

        assertEquals "net underwriting info premium motor 15000", 13500, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium, EPSILON
        assertEquals "net underwriting info premium motor 5000", 4500, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "net underwriting info premium property 3000", 1200, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "net underwriting info premium property 2000", 800, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium
    }

    
    void testClaimSharesOneLine() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract2,
                getCoverAttributeStrategy(['lines': ['motor']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim3 = getClaim(perilB, lob['motor'], 200, 0, ClaimType.ATTRITIONAL)
        Claim claim4 = getClaim(perilB, lob['motor'], 400, 0, ClaimType.ATTRITIONAL)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor10000 = new UnderwritingInfo(premium: 10000, commission: 100, lineOfBusiness: lob['motor'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor10000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 4, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 4, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium 15000", 3000, wxlContract1.outCoverUnderwritingInfo[0].premium
        assertEquals "ceded underwriting info premium 10000", 2000, wxlContract1.outCoverUnderwritingInfo[1].premium

        assertEquals "net underwriting info premium 15000", 12000, wxlContract1.outNetAfterCoverUnderwritingInfo[0].premium
        assertEquals "net underwriting info premium 10000", 8000, wxlContract1.outNetAfterCoverUnderwritingInfo[1].premium
    }

    void testClaimSharesTwoLines() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor','property'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract2,
                getCoverAttributeStrategy(['lines': ['motor','property']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 200, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 300, 0, ClaimType.SINGLE)
        Claim claim3 = getClaim(perilB, lob['motor'], 800, 0, ClaimType.SINGLE)
        Claim claim4 = getClaim(perilB, lob['motor'], 100, 0, ClaimType.SINGLE)
        Claim claim5 = getClaim(perilA, lob['property'], 200, 0, ClaimType.ATTRITIONAL)
        Claim claim6 = getClaim(perilA, lob['property'], 500, 0, ClaimType.SINGLE)
        Claim claim7 = getClaim(perilB, lob['property'], 400, 0, ClaimType.SINGLE)
        Claim claim8 = getClaim(perilB, lob['property'], 900, 0, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor5000 = new UnderwritingInfo(premium: 5000, commission: 100, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty3000 = new UnderwritingInfo(premium: 3000, commission: 100, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoProperty2000 = new UnderwritingInfo(premium: 2000, commission: 100, lineOfBusiness: lob['property'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor5000 << underwritingInfoProperty3000 << underwritingInfoProperty2000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 8, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 8, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 4, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 4, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium motor 15000", 1500, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium, EPSILON
        assertEquals "ceded underwriting info premium motor 5000", 500, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "ceded underwriting info premium property 3000", 1800, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "ceded underwriting info premium property 2000", 1200, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium

        assertEquals "net underwriting info premium motor 15000", 13500, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium, EPSILON
        assertEquals "net underwriting info premium motor 5000", 4500, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "net underwriting info premium property 3000", 1200, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "net underwriting info premium property 2000", 800, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium
    }

    void testClaimSharesNoCededClaims() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB
        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['motor','property'], simulationScope.model)

        MultiCoverAttributeReinsuranceContract wxlContract1 = getMultiCoverAttributeReinsuranceContract(wxlContract2,
                getCoverAttributeStrategy(['lines': ['motor','property']], simulationScope.model),
                fixedCommission)

        Claim claim1 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim2 = getClaim(perilA, lob['motor'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim3 = getClaim(perilB, lob['motor'], 200, 0, ClaimType.ATTRITIONAL)
        Claim claim4 = getClaim(perilB, lob['motor'], 400, 0, ClaimType.ATTRITIONAL)
        Claim claim5 = getClaim(perilA, lob['property'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim6 = getClaim(perilA, lob['property'], 100, 0, ClaimType.ATTRITIONAL)
        Claim claim7 = getClaim(perilB, lob['property'], 200, 0, ClaimType.ATTRITIONAL)
        Claim claim8 = getClaim(perilB, lob['property'], 400, 0, ClaimType.ATTRITIONAL)

        UnderwritingInfo underwritingInfoMotor15000 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoMotor5000 = new UnderwritingInfo(premium: 5000, commission: 100, lineOfBusiness: lob['motor'])
        UnderwritingInfo underwritingInfoProperty3000 = new UnderwritingInfo(premium: 3000, commission: 100, lineOfBusiness: lob['property'])
        UnderwritingInfo underwritingInfoProperty2000 = new UnderwritingInfo(premium: 2000, commission: 100, lineOfBusiness: lob['property'])

        def outCoverUI = new TestProbe(wxlContract1, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(wxlContract1, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4 << claim5 << claim6 << claim7 << claim8
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoMotor15000 << underwritingInfoMotor5000 << underwritingInfoProperty3000 << underwritingInfoProperty2000

        wxlContract1.filterInChannels(wxlContract1.inClaims, incomingClaims)
        wxlContract1.filterInChannels(wxlContract1.inUnderwritingInfo, incomingUnderwritingInfo)
        wxlContract1.doCalculation()

        assertEquals "# of filtered claims", 8, wxlContract1.inClaims.size()
        assertEquals "# of covered claims", 8, wxlContract1.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 4, wxlContract1.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 4, wxlContract1.outCoverUnderwritingInfo.size()

        assertEquals "ceded underwriting info premium motor 15000", 3000, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium
        assertEquals "ceded underwriting info premium motor 5000", 1000, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "ceded underwriting info premium property 3000", 600, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "ceded underwriting info premium property 2000", 400, wxlContract1.outCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium

        assertEquals "net underwriting info premium motor 15000", 12000, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor15000}.premium
        assertEquals "net underwriting info premium motor 10000", 4000, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoMotor5000}.premium
        assertEquals "net underwriting info premium property 3000", 2400, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty3000}.premium
        assertEquals "net underwriting info premium property 2000", 1600, wxlContract1.outNetAfterCoverUnderwritingInfo.find {it.originalUnderwritingInfo == underwritingInfoProperty2000}.premium
    }

    private ClaimDevelopmentLeanPacket getClaim(PerilMarker peril, LobMarker lob, double ultimate, double paid,
                                                double fractionOfPeriod, Component origin, Event event,
                                                Claim originalClaim) {
        ClaimDevelopmentLeanPacket claim = new ClaimDevelopmentLeanPacket(ultimate: ultimate, paid: paid,
                fractionOfPeriod: fractionOfPeriod, origin: origin, event: event, originalClaim: originalClaim)
        claim.addMarker(PerilMarker, peril)
        claim.addMarker(LobMarker, lob)
        claim
    }

    private Claim getClaim(PerilMarker peril, LobMarker lob, double ultimate, double fractionOfPeriod, ClaimType claimType) {
        Claim claim = new Claim(ultimate: ultimate, fractionOfPeriod: fractionOfPeriod, claimType: claimType)
        claim.addMarker(PerilMarker, peril)
        claim.addMarker(LobMarker, lob)
        claim
    }

    private Claim getClaim(PerilMarker peril, LobMarker lob, double ultimate) {
        Claim claim = new Claim(ultimate: ultimate)
        claim.addMarker(PerilMarker, peril)
        claim.addMarker(LobMarker, lob)
        claim
    }

    private Claim getClaim(IReserveMarker reserve, LobMarker lob, double ultimate) {
        Claim claim = new Claim(ultimate: ultimate)
        claim.addMarker(IReserveMarker, reserve)
        claim.addMarker(LobMarker, lob)
        claim
    }
}
