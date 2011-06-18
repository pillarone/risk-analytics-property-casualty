package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts


import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.ICoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.claims.TestLobComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.claims.TestPerilComponent
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.generators.claims.TypableClaimsGenerator
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.domain.pc.lob.ConfigurableLobWithReserves
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CompanyCoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.NoneCompanyCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.LineOfBusinessCompanyCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.LineOfBusinessReservesCompanyCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.LineOfBusinessPerilsCompanyCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.ReservesCompanyCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.PerilsCompanyCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker
import org.pillarone.riskanalytics.domain.pc.company.Company
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.lob.CompanyConfigurableLobWithReserves
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorLean
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope

/** With the exception of testCoverCompanies() the test cases are copied from MultiCoverAttributeReinsuranceContractTests and simply
 * modified by using MultiCompanyCoverAttributeReinsuranceContract instead of MultiCoverAttributeReinsuranceContract
 *
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class MultiCompanyCoverAttributeReinsuranceContractTest extends GroovyTestCase {

    /**
     * get a 20% Quota Share contract with no commission, covering only the 'fire' LOB
     */
    static MultiCompanyCoverAttributeReinsuranceContract getQuotaShare20FireLOB() {
        MultiCompanyCoverAttributeReinsuranceContract contract = new MultiCompanyCoverAttributeReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2, "coveredByReinsurer": 1d]),
                parmInuringPriority: 10,
                parmCover: CompanyCoverAttributeStrategyType.getStrategy(
                        CompanyCoverAttributeStrategyType.LINESOFBUSINESS,
                        ['lines': new ComboBoxTableMultiDimensionalParameter(['fire'], ['Covered Segments'], LobMarker)])
        )
        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()))
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.periodStore = new PeriodStore(simulationScope.iterationScope.periodScope)
        return contract

    }


    static MultiCompanyCoverAttributeReinsuranceContract getMultiCompanyCoverAttributeReinsuranceContract(IReinsuranceContractStrategy contractStrategy,
                                                                                                          ICoverAttributeStrategy coverStrategy,
                                                                                                          int inuringPriority = 10) {
        MultiCompanyCoverAttributeReinsuranceContract contract = new MultiCompanyCoverAttributeReinsuranceContract(
                parmContractStrategy: contractStrategy,
                parmInuringPriority: inuringPriority,
                parmCover: coverStrategy
        )
        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()))
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.periodStore = new PeriodStore(simulationScope.iterationScope.periodScope)
        return contract
    }

    static IReinsuranceContractStrategy getQuotaShareContractStrategy(double quotaShare = 0.2,
                                                                      double coveredByReinsurer = 1d) {
        ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.QUOTASHARE,
                ["quotaShare": quotaShare, "coveredByReinsurer": coveredByReinsurer]
        )
    }

    static ICoverAttributeStrategy getCompanyCoverAttributeStrategy(Map<String, List<String>> cover, Model model = null) {
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
        hasLines && hasReserves ? CompanyCoverAttributeStrategyType.getStrategy(CompanyCoverAttributeStrategyType.LINESOFBUSINESSRESERVES, ['connection': LogicArguments.AND, 'lines': lines, 'reserves': reserves]) :
            hasLines && hasPerils ? CompanyCoverAttributeStrategyType.getStrategy(CompanyCoverAttributeStrategyType.LINESOFBUSINESSPERILS, ['connection': LogicArguments.AND, 'lines': lines, 'perils': perils]) :
                hasLines ? CompanyCoverAttributeStrategyType.getStrategy(CompanyCoverAttributeStrategyType.LINESOFBUSINESS, ['lines': lines]) :
                    hasReserves ? CompanyCoverAttributeStrategyType.getStrategy(CompanyCoverAttributeStrategyType.RESERVES, ['reserves': reserves]) :
                        hasPerils ? CompanyCoverAttributeStrategyType.getStrategy(CompanyCoverAttributeStrategyType.PERILS, ['perils': perils]) :
                            new NoneCompanyCoverAttributeStrategy()
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

    // code coverage

    void testGetStrategy() {
        ICoverAttributeStrategy coverStrategy = getCompanyCoverAttributeStrategy([:])
        assertTrue "cover strategy is None", coverStrategy instanceof NoneCompanyCoverAttributeStrategy

        coverStrategy = getCompanyCoverAttributeStrategy(['lines': ['L1']])
        assertTrue "cover strategy is LOB", coverStrategy instanceof LineOfBusinessCompanyCoverAttributeStrategy

        coverStrategy = getCompanyCoverAttributeStrategy(['perils': ['P1']])
        assertTrue "cover strategy is Perils", coverStrategy instanceof PerilsCompanyCoverAttributeStrategy

        coverStrategy = getCompanyCoverAttributeStrategy(['reserves': ['R1']])
        assertTrue "cover strategy is Reserves", coverStrategy instanceof ReservesCompanyCoverAttributeStrategy

        coverStrategy = getCompanyCoverAttributeStrategy(['lines': ['L1'], 'perils': ['P1']])
        assertTrue "cover strategy is LobPerils", coverStrategy instanceof LineOfBusinessPerilsCompanyCoverAttributeStrategy

        coverStrategy = getCompanyCoverAttributeStrategy(['lines': ['L1'], 'reserves': ['R1']])
        assertTrue "cover strategy is LobReserves", coverStrategy instanceof LineOfBusinessReservesCompanyCoverAttributeStrategy
    }

    void testDefaultContract() {
        MultiCompanyCoverAttributeReinsuranceContract contract = new MultiCompanyCoverAttributeReinsuranceContract()
        assertNotNull contract
        assertSame ReinsuranceContractType.TRIVIAL, contract.parmContractStrategy.type
        assertSame CompanyCoverAttributeStrategyType.ALL, contract.parmCover.type
    }

    void testUsageWithFilteringByLob() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')

        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['fire', 'hull', 'legal', 'flood', 'lightning', 'wind'], simulationScope.model)

        /**
         *  Construct the contract, putting the marker components into the model.
         *  Be sure to put the perils and LOBs into the model before constructing the contract,
         *  so that filterInChannels receives the correct lists of lines, perils & reserves.
         *  This won't happen unless each nonempty (lines, perils, reserves) ComboBoxTableMultiDimensionalParameter
         *  in parmCover initializes its comboBoxValues (to copy the values passed in getCoverAttributeStrategy)
         *  by finding the relevant components of the right markerClass
         *  already in the model when setSimulationModel is executed.
         *  (This trick is needed to simulate a user choice in the GUI.)
         */
        MultiCompanyCoverAttributeReinsuranceContract contract = getMultiCompanyCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCompanyCoverAttributeStrategy(['lines': ['fire', 'flood', 'lightning', 'wind']], simulationScope.model)
        )

        Claim claimFire1000 = getClaim(perilA, lob['fire'], 1000d, 0.2, ClaimType.ATTRITIONAL)
        Claim claimHull1100 = getClaim(perilA, lob['hull'], 1100d, 0.3, ClaimType.SINGLE)
        Claim claimLegal1200 = getClaim(perilA, lob['legal'], 1200d, 0.1, ClaimType.SINGLE)
        Claim claimFire1300 = getClaim(perilB, lob['fire'], 1300d, 0.4, ClaimType.ATTRITIONAL)
        Claim claimFire1400 = getClaim(perilB, lob['fire'], 1400d, 0.5, ClaimType.SINGLE)
        Claim claimFire1500 = getClaim(perilC, lob['fire'], 1500d, 0.6, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoFire = new UnderwritingInfo(premium: 300, commission: 10, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoHull = new UnderwritingInfo(premium: 200, commission: 30, lineOfBusiness: lob['hull'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premium: 100, commission: 20, lineOfBusiness: lob['legal'])
        UnderwritingInfo underwritingInfoFlood = new UnderwritingInfo(premium: 600, commission: 40, lineOfBusiness: lob['flood'])
        UnderwritingInfo underwritingInfoBlitz = new UnderwritingInfo(premium: 500, commission: 60, lineOfBusiness: lob['lightning'])
        UnderwritingInfo underwritingInfoWind = new UnderwritingInfo(premium: 400, commission: 50, lineOfBusiness: lob['wind'])

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claimFire1000 << claimHull1100 << claimLegal1200 << claimFire1300 << claimFire1400 << claimFire1500
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoFire << underwritingInfoHull << underwritingInfoLegal << underwritingInfoFlood << underwritingInfoBlitz << underwritingInfoWind

        def netUWInfoWired = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')
        def netClaimsWired = new TestProbe(contract, 'outUncoveredClaims')

        // test LineOfBusinessCoverAttributeStrategy
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.filterInChannels(contract.inUnderwritingInfo, incomingUnderwritingInfo)
        contract.doCalculation()
        assertEquals "# of (fire) filtered claims", 4, contract.inClaims.size()
        assertEquals "# of (fire) covered claims", 4, contract.outCoveredClaims.size()
        assertEquals "# of (fire) uncovered claims", 4, contract.outUncoveredClaims.size()
        assertEquals "# of (fire) filtered UWInfo", 1, contract.inUnderwritingInfo.size()
        assertEquals "# of (fire) cover UWInfo", 1, contract.outCoverUnderwritingInfo.size()

        assertEquals "covered, (fire) claim 0", 200, contract.outCoveredClaims[0].ultimate
        assertEquals "covered, (fire) claim 1", 260, contract.outCoveredClaims[1].ultimate
        assertEquals "covered, (fire) claim 2", 280, contract.outCoveredClaims[2].ultimate
        assertEquals "covered, (fire) claim 3", 300, contract.outCoveredClaims[3].ultimate

        assertEquals "net, (fire) claim 0", 800, contract.outUncoveredClaims[0].ultimate
        assertEquals "net, (fire) claim 1", 1040, contract.outUncoveredClaims[1].ultimate
        assertEquals "net, (fire) claim 2", 1120, contract.outUncoveredClaims[2].ultimate
        assertEquals "net, (fire) claim 3", 1200, contract.outUncoveredClaims[3].ultimate

        assertEquals "covered, (fire) uwinfo 0", 60, contract.outCoverUnderwritingInfo[0].premium
        assertEquals "net, (fire) uwinfo 0", 240, contract.outNetAfterCoverUnderwritingInfo[0].premium

    }

    // we expect everything to be filtered out

    void testUsageWithFilteringByImpossibleLobs() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')

        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['fire', 'hull', 'legal', 'flood', 'lightning', 'wind'], simulationScope.model)

        // test LineOfBusinessCoverAttributeStrategy (filter should produce no results)
        MultiCompanyCoverAttributeReinsuranceContract contract = getMultiCompanyCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCompanyCoverAttributeStrategy(['lines': ['doomsday', 'supernova', 'blackhole', 'apocalypse']], simulationScope.model)
        )

        Claim claimFire1000 = getClaim(perilA, lob['fire'], 1000d, 0.2, ClaimType.ATTRITIONAL)
        Claim claimHull1100 = getClaim(perilA, lob['hull'], 1100d, 0.3, ClaimType.SINGLE)
        Claim claimLegal1200 = getClaim(perilA, lob['legal'], 1200d, 0.1, ClaimType.SINGLE)
        Claim claimFire1300 = getClaim(perilB, lob['fire'], 1300d, 0.4, ClaimType.ATTRITIONAL)
        Claim claimFire1400 = getClaim(perilB, lob['fire'], 1400d, 0.5, ClaimType.SINGLE)
        Claim claimFire1500 = getClaim(perilC, lob['fire'], 1500d, 0.6, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoFire = new UnderwritingInfo(premium: 300, commission: 10, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoHull = new UnderwritingInfo(premium: 200, commission: 30, lineOfBusiness: lob['hull'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premium: 100, commission: 20, lineOfBusiness: lob['legal'])
        UnderwritingInfo underwritingInfoFlood = new UnderwritingInfo(premium: 600, commission: 40, lineOfBusiness: lob['flood'])
        UnderwritingInfo underwritingInfoBlitz = new UnderwritingInfo(premium: 500, commission: 60, lineOfBusiness: lob['lightning'])
        UnderwritingInfo underwritingInfoWind = new UnderwritingInfo(premium: 400, commission: 50, lineOfBusiness: lob['wind'])

        def netUWInfoWired = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')
        def netClaimsWired = new TestProbe(contract, 'outUncoveredClaims')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claimFire1000 << claimHull1100 << claimLegal1200 << claimFire1300 << claimFire1400 << claimFire1500
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoFire << underwritingInfoHull << underwritingInfoLegal << underwritingInfoFlood << underwritingInfoBlitz << underwritingInfoWind

        // test LineOfBusinessCoverAttributeStrategy
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.filterInChannels(contract.inUnderwritingInfo, incomingUnderwritingInfo)
        contract.doCalculation()

        assertEquals "# of (astronomical) filtered claims", 0, contract.inClaims.size()
        assertEquals "# of (astronomical) covered claims", 0, contract.outCoveredClaims.size()
        assertEquals "# of (astronomical) uncovered claims", 0, contract.outUncoveredClaims.size()
        assertEquals "# of (astronomical) filtered UWInfo", 0, contract.inUnderwritingInfo.size()
        assertEquals "# of (astronomical) cover UWInfo", 0, contract.outCoverUnderwritingInfo.size()
    }

    void testUsageWithCombinedFiltering() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        // create peril markers (and add them to the simulation model)
        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        TestPerilComponent perilC = new TestPerilComponent(name: 'peril c')
        simulationScope.model.allComponents << perilA << perilB << perilC

        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = createLobs(['fire', 'hull', 'legal', 'flood', 'wind'], simulationScope.model)

        // create contract; choose LineOfBusinessPerilsCoverAttributeStrategy
        MultiCompanyCoverAttributeReinsuranceContract contract = getMultiCompanyCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCompanyCoverAttributeStrategy(['lines': ['fire'], 'perils': ['peril b']], simulationScope.model)
        )

        Claim claimFire1000 = getClaim(perilA, lob['fire'], 1000d, 0.2, ClaimType.ATTRITIONAL)
        Claim claimHull1100 = getClaim(perilA, lob['hull'], 1100d, 0.3, ClaimType.SINGLE)
        Claim claimLegal1200 = getClaim(perilA, lob['legal'], 1200d, 0.1, ClaimType.SINGLE)
        Claim claimFire1300 = getClaim(perilB, lob['fire'], 1300d, 0.4, ClaimType.ATTRITIONAL)
        Claim claimFire1400 = getClaim(perilB, lob['fire'], 1400d, 0.5, ClaimType.SINGLE)
        Claim claimFire1500 = getClaim(perilC, lob['fire'], 1500d, 0.6, ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoFire = new UnderwritingInfo(origin: perilB, premium: 300, commission: 10, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoHull = new UnderwritingInfo(origin: perilA, premium: 200, commission: 30, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(origin: perilB, premium: 100, commission: 20, lineOfBusiness: lob['hull'])
        UnderwritingInfo underwritingInfoFlood = new UnderwritingInfo(origin: perilA, premium: 600, commission: 40, lineOfBusiness: lob['flood'])

        def netUWInfoWired = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')
        def netClaimsWired = new TestProbe(contract, 'outUncoveredClaims')
        def filteredClaimsWired = new TestProbe(contract, 'inClaims')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claimFire1000 << claimHull1100 << claimLegal1200 << claimFire1300 << claimFire1400 << claimFire1500
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfoFire << underwritingInfoHull << underwritingInfoLegal << underwritingInfoFlood

        // test LineOfBusinessCoverAttributeStrategy
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.filterInChannels(contract.inUnderwritingInfo, incomingUnderwritingInfo)
        contract.doCalculation()

        assertEquals "# of (fire/B) filtered claims", 2, contract.inClaims.size()
        assertEquals "# of (fire/B) covered claims", 2, contract.outCoveredClaims.size()
        assertEquals "# of (fire/B) uncovered claims", 2, contract.outUncoveredClaims.size()
        assertEquals "# of (fire/B) filtered UWInfo", 2, contract.inUnderwritingInfo.size()
        assertEquals "# of (fire/B) cover UWInfo", 2, contract.outCoverUnderwritingInfo.size()

        // claims are filtered by peril and lob
        assertEquals "covered, (fire) claim 0", 260, contract.outCoveredClaims[0].ultimate
        assertEquals "covered, (fire) claim 1", 280, contract.outCoveredClaims[1].ultimate
        assertEquals "net, (fire) claim 0", 1040, contract.outUncoveredClaims[0].ultimate
        assertEquals "net, (fire) claim 1", 1120, contract.outUncoveredClaims[1].ultimate

        // underwriting is filtered by lob but not by peril
        assertEquals "covered, (fire) uwinfo 0", 2.7 / 5.2 * 0.2 * 300, contract.outCoverUnderwritingInfo[0].premium, 1E-5
        assertEquals "covered, (fire) uwinfo 0", 2.7 / 5.2 * 0.2 * 200, contract.outCoverUnderwritingInfo[1].premium, 1E-5
        assertEquals "net, (fire) uwinfo 0", 300 - 2.7 / 5.2 * 0.2 * 300, contract.outNetAfterCoverUnderwritingInfo[0].premium, 1E-5
        assertEquals "net, (fire) uwinfo 0", 200 - 2.7 / 5.2 * 0.2 * 200, contract.outNetAfterCoverUnderwritingInfo[1].premium, 1E-5
        contract.reset()
    }

    void testDefaultUsageWithClaimDevelopmentLeanPackets() {
        ConfigurableLobWithReserves fireLine = new ConfigurableLobWithReserves(name: 'fire')
        MultiCompanyCoverAttributeReinsuranceContract contract = getQuotaShare20FireLOB()
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.simulationScope.model.allComponents << fireLine
        contract.parmCover.lines.setSimulationModel contract.simulationScope.model
        TestComponent origin = new TestComponent()
        TypableClaimsGenerator generator = new TypableClaimsGenerator()
        Event event1 = new Event()
        Event event2 = new Event()
        Claim originalClaim1 = getClaim(generator, fireLine, 10000, 0.5, ClaimType.ATTRITIONAL)
        Claim originalClaim2 = getClaim(generator, fireLine, 500, 0.5, ClaimType.ATTRITIONAL)
        ClaimDevelopmentLeanPacket claimDevelopment1 = getClaim(generator, fireLine, 10, 6, 0.5, origin, event1, originalClaim1)
        ClaimDevelopmentLeanPacket claimDevelopment2 = getClaim(generator, fireLine, 12, 8, 0.5, origin, event2, originalClaim2)
        contract.inClaims << claimDevelopment1 << claimDevelopment2

        def probeCoveredClaims = new TestProbe(contract, "outCoveredClaims")
        List coveredClaims = probeCoveredClaims.result

        contract.start()

        assertEquals '# ceded claims packets', 2, coveredClaims.size()
        assertEquals 'ceded incurred 0', 2d, coveredClaims[0].incurred
        assertEquals 'ceded incurred 1', 2.4, coveredClaims[1].incurred, 1E-10
        assertEquals 'ceded paid 0', 1.2, coveredClaims[0].paid, 1E-10
        assertEquals 'ceded paid 1', 1.6, coveredClaims[1].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, coveredClaims[0].reserved, 1E-10
        assertEquals 'ceded reserved 1', 0.8, coveredClaims[1].reserved, 1E-10
    }

    void testCoverLines() {
        // create a simulation scope and a model
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        // create some LOB marker components and add them to the model
        Map<String, TestLobComponent> lob = createLobs(['fire', 'motor'], simulationScope.model)

        TestComponent origin = new TestComponent()
        TypableClaimsGenerator generator = new TypableClaimsGenerator(name: 'peril 1')
        simulationScope.model.allComponents << generator
        Event event1 = new Event()
        Event event2 = new Event()
        Claim originalClaim1 = new Claim(
                value: 10000,
                event: event1,
                fractionOfPeriod: 0.6,
                claimType: ClaimType.ATTRITIONAL,
        )
        Claim originalClaim2 = new Claim(
                value: 500,
                event: event2,
                fractionOfPeriod: 0.5,
                claimType: ClaimType.ATTRITIONAL,
        )

        ClaimDevelopmentLeanPacket claimDevelopment1 = getClaim(generator, lob['fire'], 10d, 6d, 0.4d, origin, event1, originalClaim1)
        ClaimDevelopmentLeanPacket claimDevelopment2 = getClaim(generator, lob['motor'], 12d, 8d, 0.7d, origin, event2, originalClaim2)

        // create the contract (setting the simulation model in each cover attribute strategy to simulate GUI choice)
        MultiCompanyCoverAttributeReinsuranceContract contract = getMultiCompanyCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCompanyCoverAttributeStrategy(['lines': ['fire']], simulationScope.model)
        )

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claimDevelopment1 << claimDevelopment2

        // test LineOfBusinessCoverAttributeStrategy
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.doCalculation()
        assertEquals '# ceded claims packets', 1, contract.outCoveredClaims.size()
        assertEquals 'ceded incurred 0', 2d, contract.outCoveredClaims[0].incurred
        assertEquals 'ceded paid 0', 1.2, contract.outCoveredClaims[0].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, contract.outCoveredClaims[0].reserved, 1E-10
        assertEquals 'origin of fire claim', originalClaim1, contract.outCoveredClaims[0].originalClaim
        contract.reset()

        // choose a different cover attribute strategy (simulate effect of new choice in GUI)
        contract.parmCover = getCompanyCoverAttributeStrategy(['lines': ['motor']], simulationScope.model)
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()

        incomingClaims.clear()
        incomingClaims << claimDevelopment1 << claimDevelopment2

        // test LineOfBusinessCoverAttributeStrategy
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.doCalculation()
        assertEquals '# ceded claims packets', 1, contract.outCoveredClaims.size()
        assertEquals 'ceded incurred 0', 2.4d, contract.outCoveredClaims[0].incurred, 1E-10
        assertEquals 'ceded paid 0', 1.6, contract.outCoveredClaims[0].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, contract.outCoveredClaims[0].reserved, 1E-10
        assertEquals 'origin of motor claim', originalClaim2, contract.outCoveredClaims[0].originalClaim
        contract.reset()

        contract.parmCover = getCompanyCoverAttributeStrategy(['lines': ['fire', 'motor']], simulationScope.model)
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        contract.inClaims << claimDevelopment1 << claimDevelopment2
        contract.doCalculation()
        assertEquals '# ceded claims packets', 2, contract.outCoveredClaims.size()
        assertEquals 'origin of fire claim', originalClaim1, contract.outCoveredClaims[0].originalClaim
        assertEquals 'origin of motor claim', originalClaim2, contract.outCoveredClaims[1].originalClaim
        contract.reset()

        contract.parmCover = getCompanyCoverAttributeStrategy(['perils': ['peril 1']], simulationScope.model)
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        contract.inClaims << claimDevelopment1 << claimDevelopment2
        contract.doCalculation()
        assertEquals '# ceded claims packets', 2, contract.outCoveredClaims.size()
        assertEquals 'origin of fire claim', originalClaim1, contract.outCoveredClaims[0].originalClaim
        assertEquals 'origin of motor claim', originalClaim2, contract.outCoveredClaims[1].originalClaim
        contract.reset()
    }

    void testCoverCompanies() {

        Company companyA = new Company(name: 'company a', periodStore: new PeriodStore(null))
        Company companyB = new Company(name: 'company b', periodStore: new PeriodStore(null))
        Company companyC = new Company(name: 'company c', periodStore: new PeriodStore(null))

        ConstrainedString parmCompanyA = new ConstrainedString(ICompanyMarker, 'company a')
        ConstrainedString parmCompanyB = new ConstrainedString(ICompanyMarker, 'company b')
        ConstrainedString parmCompanyC = new ConstrainedString(ICompanyMarker, 'company c')

        parmCompanyA.selectedComponent = companyA
        parmCompanyB.selectedComponent = companyB
        parmCompanyC.selectedComponent = companyC

        CompanyConfigurableLobWithReserves motorA = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyA)
        CompanyConfigurableLobWithReserves motorB = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyB)
        CompanyConfigurableLobWithReserves propertyA = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyA)
        CompanyConfigurableLobWithReserves propertyC = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyC)

        ComboBoxTableMultiDimensionalParameter companies = new ComboBoxTableMultiDimensionalParameter(['company a', 'company b'], ['Covered Companies'], ICompanyMarker)
        companies.comboBoxValues.put('company a', companyA)
        companies.comboBoxValues.put('company b', companyB)

        MultiCompanyCoverAttributeReinsuranceContract contract = new MultiCompanyCoverAttributeReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2, "coveredByReinsurer": 1d]),
                parmInuringPriority: 0,
                parmCover: CompanyCoverAttributeStrategyType.getStrategy(
                        CompanyCoverAttributeStrategyType.COMPANIES,
                        ['companies': companies])
        )

        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        companies.setSimulationModel(simulationScope.model)
        simulationScope.model.allComponents << motorA << motorB << propertyA << propertyC << companyA << companyB << companyC

        TypableClaimsGenerator generator = new TypableClaimsGenerator(name: 'peril 1')
        simulationScope.model.allComponents << generator
        ReservesGeneratorLean reservesGenerator = new ReservesGeneratorLean(name: 'reserves 1')

        Claim claim1 = getClaim(generator, motorA, 100d)
        Claim claim2 = getClaim(generator, motorB, 400d)
        Claim claim3 = getClaim(generator, motorA, 200d)
        Claim claim4 = getClaim(generator, propertyA, 600d)
        Claim claim5 = getClaim(generator, propertyC, 800d)
        Claim claim6 = getClaim(reservesGenerator, motorA, 1000d)

        UnderwritingInfo underwritingInfo1 = new UnderwritingInfo(premium: 15000, commission: 200, lineOfBusiness: motorA)
        UnderwritingInfo underwritingInfo2 = new UnderwritingInfo(premium: 10000, commission: 100, lineOfBusiness: motorB)
        UnderwritingInfo underwritingInfo3 = new UnderwritingInfo(premium: 10000, commission: 100, lineOfBusiness: propertyC)

        def outCoverUI = new TestProbe(contract, 'outCoverUnderwritingInfo')
        def outNetUI = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')


        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claim1 << claim2 << claim3 << claim4 << claim5 << claim6
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << underwritingInfo1 << underwritingInfo2 << underwritingInfo3

        // test LineOfBusinessCoverAttributeStrategy
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.filterInChannels(contract.inUnderwritingInfo, incomingUnderwritingInfo)
        contract.doCalculation()

        assertEquals "# of filtered claims", 4, contract.inClaims.size()
        assertEquals "# of covered claims", 4, contract.outCoveredClaims.size()
        assertEquals "# of filtered UWInfo", 2, contract.inUnderwritingInfo.size()
        assertEquals "# of cover UWInfo", 2, contract.outCoverUnderwritingInfo.size()

        assertEquals "ceded claim 1", 20d, contract.outCoveredClaims[0].ultimate
        assertEquals "ceded claim 2", 80d, contract.outCoveredClaims[1].ultimate
        assertEquals "ceded claim 3", 40d, contract.outCoveredClaims[2].ultimate
        assertEquals "ceded claim 4", 120d, contract.outCoveredClaims[3].ultimate

        assertEquals "ceded premium motorA", 3000d, contract.outCoverUnderwritingInfo[0].premium
        assertEquals "ceded premium motorB", 2000d, contract.outCoverUnderwritingInfo[1].premium

        assertEquals "net premium motorA", 12000d, contract.outNetAfterCoverUnderwritingInfo[0].premium
        assertEquals "net premium motorB", 8000d, contract.outNetAfterCoverUnderwritingInfo[1].premium
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
