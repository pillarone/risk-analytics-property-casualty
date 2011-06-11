package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.TestLobComponent
import org.pillarone.riskanalytics.domain.pc.claims.TestPerilComponent
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.pc.generators.claims.TypableClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.*
import org.pillarone.riskanalytics.domain.pc.lob.ConfigurableLobWithReserves
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase

/**
 * These test cases don't totally match the situation as it is when the component is used in a model as the
 * simulationScope.model is a trivial one and peril/lob reference information is injected more directly.
 *
 * @author stefan.kunz & ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class MultiCoverAttributeReinsuranceContractTests extends GroovyTestCase {

    /**
     * get a 20% Quota Share contract with no commission, covering only the 'fire' LOB
     */
    static MultiCoverAttributeReinsuranceContract getQuotaShare20FireLOB() {
        MultiCoverAttributeReinsuranceContract contract = new MultiCoverAttributeReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2, "coveredByReinsurer": 1d]),
                parmInuringPriority: 10,
                parmCover: CoverAttributeStrategyType.getStrategy(
                        CoverAttributeStrategyType.LINESOFBUSINESS,
                        ['lines': new ComboBoxTableMultiDimensionalParameter(['fire'], ['Covered Segments'], ISegmentMarker)])
        )
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        return contract

    }


    static MultiCoverAttributeReinsuranceContract getMultiCoverAttributeReinsuranceContract(IReinsuranceContractStrategy contractStrategy,
                                                                                            ICoverAttributeStrategy coverStrategy,
                                                                                            int inuringPriority = 10) {
        MultiCoverAttributeReinsuranceContract contract = new MultiCoverAttributeReinsuranceContract(
                parmContractStrategy: contractStrategy,
                parmInuringPriority: inuringPriority,
                parmCover: coverStrategy
        )
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        return contract
    }

    static IReinsuranceContractStrategy getQuotaShareContractStrategy(double quotaShare = 0.2,
                                                                      double coveredByReinsurer = 1d) {
        ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.QUOTASHARE,
                ["quotaShare": quotaShare, "coveredByReinsurer": coveredByReinsurer]
        )
    }

    static ICoverAttributeStrategy getCoverAttributeStrategy(Map<String, List<String>> cover, Model model = null) {
        boolean hasLines = cover.containsKey('lines')
        boolean hasPerils = cover.containsKey('perils')
        boolean hasReserves = cover.containsKey('reserves')

        ComboBoxTableMultiDimensionalParameter lines = hasLines ? new ComboBoxTableMultiDimensionalParameter(cover['lines'], ['Covered Segments'], ISegmentMarker) : null
        ComboBoxTableMultiDimensionalParameter perils = hasPerils ? new ComboBoxTableMultiDimensionalParameter(cover['perils'], ['Covered Perils'], IPerilMarker) : null
        ComboBoxTableMultiDimensionalParameter reserves = hasReserves ? new ComboBoxTableMultiDimensionalParameter(cover['reserves'], ['Covered Reserves'], ISegmentMarker) : null

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

    // code coverage

    void testGetStrategy() {
        ICoverAttributeStrategy coverStrategy = getCoverAttributeStrategy([:])
        assertTrue "cover strategy is None", coverStrategy instanceof NoneCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['lines': ['L1']])
        assertTrue "cover strategy is LOB", coverStrategy instanceof LineOfBusinessCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['perils': ['P1']])
        assertTrue "cover strategy is Perils", coverStrategy instanceof PerilsCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['reserves': ['R1']])
        assertTrue "cover strategy is Reserves", coverStrategy instanceof ReservesCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['lines': ['L1'], 'perils': ['P1']])
        assertTrue "cover strategy is LobPerils", coverStrategy instanceof LineOfBusinessPerilsCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['lines': ['L1'], 'reserves': ['R1']])
        assertTrue "cover strategy is LobReserves", coverStrategy instanceof LineOfBusinessReservesCoverAttributeStrategy
    }

    void testDefaultContract() {
        MultiCoverAttributeReinsuranceContract contract = new MultiCoverAttributeReinsuranceContract()
        assertNotNull contract
        assertSame ReinsuranceContractType.TRIVIAL, contract.parmContractStrategy.type
        assertSame CoverAttributeStrategyType.ALL, contract.parmCover.type
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
        MultiCoverAttributeReinsuranceContract contract = getMultiCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCoverAttributeStrategy(['lines': ['fire', 'flood', 'lightning', 'wind']], simulationScope.model)
        )
        contract.parmPremiumBase = ReinsuranceContractPremiumBase.PROPORTIONALTOCOVEREDCLAIMS

        Claim claimFire1000 = new Claim(peril: perilA, lineOfBusiness: lob['fire'], value: 1000d, fractionOfPeriod: 0.2, claimType: ClaimType.ATTRITIONAL)
        Claim claimHull1100 = new Claim(peril: perilA, lineOfBusiness: lob['hull'], value: 1100d, fractionOfPeriod: 0.3, claimType: ClaimType.SINGLE)
        Claim claimLegal1200 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 1200d, fractionOfPeriod: 0.1, claimType: ClaimType.SINGLE)
        Claim claimFire1300 = new Claim(peril: perilB, lineOfBusiness: lob['fire'], value: 1300d, fractionOfPeriod: 0.4, claimType: ClaimType.ATTRITIONAL)
        Claim claimFire1400 = new Claim(peril: perilB, lineOfBusiness: lob['fire'], value: 1400d, fractionOfPeriod: 0.5, claimType: ClaimType.SINGLE)
        Claim claimFire1500 = new Claim(peril: perilC, lineOfBusiness: lob['fire'], value: 1500d, fractionOfPeriod: 0.6, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoFire = new UnderwritingInfo(premium: 300, commission: 10, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoHull = new UnderwritingInfo(premium: 200, commission: 30, lineOfBusiness: lob['hull'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premium: 100, commission: 20, lineOfBusiness: lob['legal'])
        UnderwritingInfo underwritingInfoFlood = new UnderwritingInfo(premium: 600, commission: 40, lineOfBusiness: lob['flood'])
        UnderwritingInfo underwritingInfoBlitz = new UnderwritingInfo(premium: 500, commission: 60, lineOfBusiness: lob['lightning'])
        UnderwritingInfo underwritingInfoWind = new UnderwritingInfo(premium: 400, commission: 50, lineOfBusiness: lob['wind'])

        contract.inClaims << claimFire1000 << claimHull1100 << claimLegal1200 << claimFire1300 << claimFire1400 << claimFire1500
        contract.inUnderwritingInfo << underwritingInfoFire << underwritingInfoHull << underwritingInfoLegal << underwritingInfoFlood << underwritingInfoBlitz << underwritingInfoWind

        def netUWInfoWired = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')
        def netClaimsWired = new TestProbe(contract, 'outUncoveredClaims')

        // test LineOfBusinessCoverAttributeStrategy
        contract.doCalculation()
        assertEquals "# of (fire) filtered claims", 4, contract.outFilteredClaims.size()
        assertEquals "# of (fire) covered claims", 4, contract.outCoveredClaims.size()
        assertEquals "# of (fire) uncovered claims", 4, contract.outUncoveredClaims.size()
        assertEquals "# of (fire) filtered UWInfo", 1, contract.outFilteredUnderwritingInfo.size()
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
        MultiCoverAttributeReinsuranceContract contract = getMultiCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCoverAttributeStrategy(['lines': ['doomsday', 'supernova', 'blackhole', 'apocalypse']], simulationScope.model)
        )

        Claim claimFire1000 = new Claim(peril: perilA, lineOfBusiness: lob['fire'], value: 1000d, fractionOfPeriod: 0.2, claimType: ClaimType.ATTRITIONAL)
        Claim claimHull1100 = new Claim(peril: perilA, lineOfBusiness: lob['hull'], value: 1100d, fractionOfPeriod: 0.3, claimType: ClaimType.SINGLE)
        Claim claimLegal1200 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 1200d, fractionOfPeriod: 0.1, claimType: ClaimType.SINGLE)
        Claim claimFire1300 = new Claim(peril: perilB, lineOfBusiness: lob['fire'], value: 1300d, fractionOfPeriod: 0.4, claimType: ClaimType.ATTRITIONAL)
        Claim claimFire1400 = new Claim(peril: perilB, lineOfBusiness: lob['fire'], value: 1400d, fractionOfPeriod: 0.5, claimType: ClaimType.SINGLE)
        Claim claimFire1500 = new Claim(peril: perilC, lineOfBusiness: lob['fire'], value: 1500d, fractionOfPeriod: 0.6, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoFire = new UnderwritingInfo(premium: 300, commission: 10, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoHull = new UnderwritingInfo(premium: 200, commission: 30, lineOfBusiness: lob['hull'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(premium: 100, commission: 20, lineOfBusiness: lob['legal'])
        UnderwritingInfo underwritingInfoFlood = new UnderwritingInfo(premium: 600, commission: 40, lineOfBusiness: lob['flood'])
        UnderwritingInfo underwritingInfoBlitz = new UnderwritingInfo(premium: 500, commission: 60, lineOfBusiness: lob['lightning'])
        UnderwritingInfo underwritingInfoWind = new UnderwritingInfo(premium: 400, commission: 50, lineOfBusiness: lob['wind'])

        def netUWInfoWired = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')
        def netClaimsWired = new TestProbe(contract, 'outUncoveredClaims')

        contract.inClaims << claimFire1000 << claimHull1100 << claimLegal1200 << claimFire1300 << claimFire1400 << claimFire1500
        contract.inUnderwritingInfo << underwritingInfoFire << underwritingInfoHull << underwritingInfoLegal << underwritingInfoFlood << underwritingInfoBlitz << underwritingInfoWind
        contract.doCalculation()

        assertEquals "# of (astronomical) filtered claims", 0, contract.outFilteredClaims.size()
        assertEquals "# of (astronomical) covered claims", 0, contract.outCoveredClaims.size()
        assertEquals "# of (astronomical) uncovered claims", 0, contract.outUncoveredClaims.size()
        assertEquals "# of (astronomical) filtered UWInfo", 0, contract.outFilteredUnderwritingInfo.size()
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
        MultiCoverAttributeReinsuranceContract contract = getMultiCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCoverAttributeStrategy(['lines': ['fire'], 'perils': ['peril b']], simulationScope.model)
        )
        contract.parmPremiumBase = ReinsuranceContractPremiumBase.PROPORTIONALTOCOVEREDCLAIMS

        Claim claimFire1000 = new Claim(peril: perilA, lineOfBusiness: lob['fire'], value: 1000d, fractionOfPeriod: 0.2, claimType: ClaimType.ATTRITIONAL)
        Claim claimHull1100 = new Claim(peril: perilA, lineOfBusiness: lob['hull'], value: 1100d, fractionOfPeriod: 0.3, claimType: ClaimType.SINGLE)
        Claim claimLegal1200 = new Claim(peril: perilA, lineOfBusiness: lob['legal'], value: 1200d, fractionOfPeriod: 0.1, claimType: ClaimType.SINGLE)
        Claim claimFire1300 = new Claim(peril: perilB, lineOfBusiness: lob['fire'], value: 1300d, fractionOfPeriod: 0.4, claimType: ClaimType.ATTRITIONAL)
        Claim claimFire1400 = new Claim(peril: perilB, lineOfBusiness: lob['fire'], value: 1400d, fractionOfPeriod: 0.5, claimType: ClaimType.SINGLE)
        Claim claimFire1500 = new Claim(peril: perilC, lineOfBusiness: lob['fire'], value: 1500d, fractionOfPeriod: 0.6, claimType: ClaimType.SINGLE)

        UnderwritingInfo underwritingInfoFire = new UnderwritingInfo(origin: perilB, premium: 300, commission: 10, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoHull = new UnderwritingInfo(origin: perilA, premium: 200, commission: 30, lineOfBusiness: lob['fire'])
        UnderwritingInfo underwritingInfoLegal = new UnderwritingInfo(origin: perilB, premium: 100, commission: 20, lineOfBusiness: lob['hull'])
        UnderwritingInfo underwritingInfoFlood = new UnderwritingInfo(origin: perilA, premium: 600, commission: 40, lineOfBusiness: lob['flood'])

        def netUWInfoWired = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')
        def netClaimsWired = new TestProbe(contract, 'outUncoveredClaims')
        def filteredClaimsWired = new TestProbe(contract, 'outFilteredClaims')

        contract.inClaims << claimFire1000 << claimHull1100 << claimLegal1200 << claimFire1300 << claimFire1400 << claimFire1500
        contract.inUnderwritingInfo << underwritingInfoFire << underwritingInfoHull << underwritingInfoLegal << underwritingInfoFlood
        contract.doCalculation()

        assertEquals "# of (fire/B) filtered claims", 2, contract.outFilteredClaims.size()
        assertEquals "# of (fire/B) covered claims", 2, contract.outCoveredClaims.size()
        assertEquals "# of (fire/B) uncovered claims", 2, contract.outUncoveredClaims.size()
        assertEquals "# of (fire/B) filtered UWInfo", 2, contract.outFilteredUnderwritingInfo.size()
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
        MultiCoverAttributeReinsuranceContract contract = getQuotaShare20FireLOB()
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.simulationScope.model.allComponents << fireLine
        contract.parmCover.lines.setSimulationModel contract.simulationScope.model
        TestComponent origin = new TestComponent()
        TypableClaimsGenerator generator = new TypableClaimsGenerator()
        Event event1 = new Event()
        Event event2 = new Event()
        Claim originalClaim1 = new Claim(
                value: 10000,
                event: event1,
                fractionOfPeriod: 0.5,
                lineOfBusiness: fireLine,
                peril: generator,
                claimType: ClaimType.ATTRITIONAL,
        )
        Claim originalClaim2 = new Claim(
                value: 500,
                event: event2,
                lineOfBusiness: fireLine,
                fractionOfPeriod: 0.5,
                peril: generator,
                claimType: ClaimType.ATTRITIONAL,
        )
        ClaimDevelopmentLeanPacket claimDevelopment1 = new ClaimDevelopmentLeanPacket(
                ultimate: 10,
                paid: 6,
                origin: origin,
                originalClaim: originalClaim1,
                lineOfBusiness: fireLine,
                event: event1,
                peril: generator,
                fractionOfPeriod: 0.5)
        ClaimDevelopmentLeanPacket claimDevelopment2 = new ClaimDevelopmentLeanPacket(
                ultimate: 12,
                paid: 8,
                origin: origin,
                originalClaim: originalClaim2,
                lineOfBusiness: fireLine,
                event: event2,
                peril: generator,
                fractionOfPeriod: 0.5)
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
                peril: generator,
                claimType: ClaimType.ATTRITIONAL,
        )
        Claim originalClaim2 = new Claim(
                value: 500,
                event: event2,
                fractionOfPeriod: 0.5,
                peril: generator,
                claimType: ClaimType.ATTRITIONAL,
        )
        ClaimDevelopmentLeanPacket claimDevelopment1 = new ClaimDevelopmentLeanPacket(
                ultimate: 10,
                paid: 6,
                origin: origin,
                originalClaim: originalClaim1,
                event: event1,
                peril: generator,
                lineOfBusiness: lob['fire'],
                fractionOfPeriod: 0.4)
        ClaimDevelopmentLeanPacket claimDevelopment2 = new ClaimDevelopmentLeanPacket(
                ultimate: 12,
                paid: 8,
                origin: origin,
                originalClaim: originalClaim2,
                event: event2,
                peril: generator,
                lineOfBusiness: lob['motor'],
                fractionOfPeriod: 0.7)

        // create the contract (setting the simulation model in each cover attribute strategy to simulate GUI choice)
        MultiCoverAttributeReinsuranceContract contract = getMultiCoverAttributeReinsuranceContract(
                getQuotaShareContractStrategy(),
                getCoverAttributeStrategy(['lines': ['fire']], simulationScope.model)
        )
        contract.inClaims << claimDevelopment1 << claimDevelopment2

        contract.doCalculation()
        assertEquals '# ceded claims packets', 1, contract.outCoveredClaims.size()
        assertEquals 'ceded incurred 0', 2d, contract.outCoveredClaims[0].incurred
        assertEquals 'ceded paid 0', 1.2, contract.outCoveredClaims[0].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, contract.outCoveredClaims[0].reserved, 1E-10
        assertEquals 'origin of fire claim', originalClaim1, contract.outCoveredClaims[0].originalClaim
        contract.reset()

        // choose a different cover attribute strategy (simulate effect of new choice in GUI)
        contract.parmCover = getCoverAttributeStrategy(['lines': ['motor']], simulationScope.model)
        contract.inClaims << claimDevelopment1 << claimDevelopment2
        contract.doCalculation()
        assertEquals '# ceded claims packets', 1, contract.outCoveredClaims.size()
        assertEquals 'ceded incurred 0', 2.4d, contract.outCoveredClaims[0].incurred, 1E-10
        assertEquals 'ceded paid 0', 1.6, contract.outCoveredClaims[0].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, contract.outCoveredClaims[0].reserved, 1E-10
        assertEquals 'origin of motor claim', originalClaim2, contract.outCoveredClaims[0].originalClaim
        contract.reset()

        contract.parmCover = getCoverAttributeStrategy(['lines': ['fire', 'motor']], simulationScope.model)
        contract.inClaims << claimDevelopment1 << claimDevelopment2
        contract.doCalculation()
        assertEquals '# ceded claims packets', 2, contract.outCoveredClaims.size()
        assertEquals 'origin of fire claim', originalClaim1, contract.outCoveredClaims[0].originalClaim
        assertEquals 'origin of motor claim', originalClaim2, contract.outCoveredClaims[1].originalClaim
        contract.reset()

        contract.parmCover = getCoverAttributeStrategy(['perils': ['peril 1']], simulationScope.model)
        contract.inClaims << claimDevelopment1 << claimDevelopment2
        contract.doCalculation()
        assertEquals '# ceded claims packets', 2, contract.outCoveredClaims.size()
        assertEquals 'origin of fire claim', originalClaim1, contract.outCoveredClaims[0].originalClaim
        assertEquals 'origin of motor claim', originalClaim2, contract.outCoveredClaims[1].originalClaim
        contract.reset()
    }
}