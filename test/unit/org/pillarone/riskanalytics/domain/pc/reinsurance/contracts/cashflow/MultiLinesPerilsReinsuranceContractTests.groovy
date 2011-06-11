package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.apache.commons.lang.NotImplementedException
import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.TestLobComponent
import org.pillarone.riskanalytics.domain.pc.claims.TestPerilComponent
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCoverAttributeReinsuranceContractTests
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentWithIBNRPacket
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.*

/**
 * @author stefan.kunz & ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class MultiLinesPerilsReinsuranceContractTests extends GroovyTestCase {

    static MultiLinesPerilsReinsuranceContract getContractFullCoverAllLinesPerils() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.25, "coveredByReinsurer": 1d]),
                parmCommissionStrategy: CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0d]),
                parmCoverPeriod: new FullPeriodCoveredStrategy(),
                parmCoveredByReinsurer: 1d,
                parmInuringPriority: 0d)
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        simulationScope.iterationScope = getNewFirstIterationScope(new DateTime(2010, 1, 1, 0, 0, 0, 0), Period.years(1))
        contract.simulationScope = simulationScope

        return contract
    }

    static MultiLinesPerilsReinsuranceContract getQuotaShare50FullCoverAllLinesPerils() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.50, "coveredByReinsurer": 1d]),
                parmCommissionStrategy: CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0d]),
                parmCoverPeriod: new FullPeriodCoveredStrategy(),
                parmCoveredByReinsurer: 1d,
                parmInuringPriority: 0d)
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        simulationScope.iterationScope = getNewFirstIterationScope(new DateTime(2010, 1, 1, 0, 0, 0, 0), Period.years(1))
        contract.simulationScope = simulationScope

        return contract
    }

    static MultiLinesPerilsReinsuranceContract getContractHalfPeriodCoverAllLinesPerils() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.25, "coveredByReinsurer": 1d]),
                parmCommissionStrategy: CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0d]),
                parmCoverPeriod: CoverPeriodType.getStrategy(
                        CoverPeriodType.PERIOD,
                        ['start': new DateTime(2010, 1, 1, 0, 0, 0, 0), 'end': new DateTime(2010, 6, 30, 0, 0, 0, 0)]),
                parmCoveredByReinsurer: 1d,
                parmInuringPriority: 0d)
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        simulationScope.iterationScope = getNewFirstIterationScope(new DateTime(2010, 1, 1, 0, 0, 0, 0), Period.years(1))
        contract.simulationScope = simulationScope
        return contract
    }

    static MultiLinesPerilsReinsuranceContract getContractThreePeriodCoverDelayedAllLinesPerils() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.25, "coveredByReinsurer": 1d]),
                parmCommissionStrategy: CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0d]),
                parmCoverPeriod: CoverPeriodType.getStrategy(
                        CoverPeriodType.PERIOD,
                        ['start': new DateTime(2011, 1, 1, 0, 0, 0, 0), 'end': new DateTime(2013, 12, 31, 0, 0, 0, 0)]),
                parmCoveredByReinsurer: 1d,
                parmInuringPriority: 0d)
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        simulationScope.iterationScope = getNewFirstIterationScope(new DateTime(2010, 1, 1, 0, 0, 0, 0), Period.years(1))
        contract.simulationScope = simulationScope
        return contract
    }

    static MultiLinesPerilsReinsuranceContract getAllLinesPerilsQuotaShareContract(int iterationStartYear, int contractStartYear,
                                                                                   int contractDuration = 1, double quotaShare = 0.5, double inuringPriority = 0, double commission = 0) {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getContractStrategy(
                        ReinsuranceContractType.QUOTASHARE, ["quotaShare": quotaShare, "coveredByReinsurer": 1d]),
                parmCommissionStrategy: CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, ["commission": commission]),
                parmCoverPeriod: CoverPeriodType.getStrategy(
                        CoverPeriodType.PERIOD,
                        ['start': new DateTime(contractStartYear, 1, 1, 0, 0, 0, 0),
                                'end': new DateTime(contractStartYear + contractDuration - 1, 12, 31, 0, 0, 0, 0)]),
                parmCoveredByReinsurer: 1d,
                parmInuringPriority: inuringPriority)
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        simulationScope.iterationScope = getNewFirstIterationScope(new DateTime(iterationStartYear, 1, 1, 0, 0, 0, 0), Period.years(1))
        contract.simulationScope = simulationScope
        return contract
    }

    /** this method is like the one in MultiCoverAttributeReinsuranceContractTests, but has no reserves  */
    static ICoverAttributeStrategy getCoverAttributeStrategy(Map<String, List<String>> cover, Model model = null) {
        boolean hasLines = cover.containsKey('lines')
        boolean hasPerils = cover.containsKey('perils')

        ComboBoxTableMultiDimensionalParameter lines = hasLines ? new ComboBoxTableMultiDimensionalParameter(cover['lines'], ['Covered Segments'], ISegmentMarker) : null
        ComboBoxTableMultiDimensionalParameter perils = hasPerils ? new ComboBoxTableMultiDimensionalParameter(cover['perils'], ['Covered Perils'], IPerilMarker) : null

        // each of the strategy-specific ComboBoxTableMultiDimensionalParameter properties needs to set the simulation model (to simulate a choice from the GUI)
        if (model != null) {
            if (hasLines) {lines.setSimulationModel model}
            if (hasPerils) {perils.setSimulationModel model}
        }

        // return one of:
        hasLines && hasPerils ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESSPERILS, ['connection': LogicArguments.AND, 'lines': lines, 'perils': perils]) :
            hasLines ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ['lines': lines]) :
                hasPerils ? CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.PERILS, ['perils': perils]) :
                    new NoneCoverAttributeStrategy()
    }

    static IterationScope getNewFirstIterationScope(DateTime startSimulation, Period period) {
        IterationScope iterationScope = new IterationScope(
                periodScope: new PeriodScope(
                        periodCounter: new ContinuousPeriodCounter(
                                startSimulation, period
                        )
                )
        )
        iterationScope.prepareNextIteration()
        return iterationScope
    }

    // code coverage

    void testGetStrategy() {
        ICoverAttributeStrategy coverStrategy = getCoverAttributeStrategy([:])
        assertTrue "cover strategy is None", coverStrategy instanceof NoneCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['lines': ['L1']])
        assertTrue "cover strategy is LOB", coverStrategy instanceof LineOfBusinessCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['perils': ['P1']])
        assertTrue "cover strategy is Perils", coverStrategy instanceof PerilsCoverAttributeStrategy

        coverStrategy = getCoverAttributeStrategy(['lines': ['L1'], 'perils': ['P1']])
        assertTrue "cover strategy is LobPerils", coverStrategy instanceof LineOfBusinessPerilsCoverAttributeStrategy
    }

    void testDefaultContract() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract()
        assertNotNull contract
        assertSame ReinsuranceContractType.TRIVIAL, contract.parmContractStrategy.type

        // make sure that the IDE hasn't introduced an error when "optimizing imports" (we want *cashflow*.cover.CAST)
        assertSame org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.cover.CoverAttributeStrategyType.ALL, contract.parmCover.type
    }

    void testOneFullPeriodCover() {
        MultiLinesPerilsReinsuranceContract contract = getContractFullCoverAllLinesPerils()
        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500)
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480)
        contract.inClaims << claim1000 << claim800

        def netClaims = new TestProbe(contract, 'outUncoveredClaims')

        contract.doCalculation()

        assertEquals '# gross covered', 2, contract.outClaimsGrossInCoveredPeriod.size()
        assertEquals 'claim1000 gross covered', claim1000, contract.outClaimsGrossInCoveredPeriod[0]
        assertEquals 'claim800 gross covered', claim800, contract.outClaimsGrossInCoveredPeriod[1]

        assertEquals '# ceded', 2, contract.outCoveredClaims.size()
        assertEquals 'claim1000 incurred ceded', 250, contract.outCoveredClaims[0].incurred
        assertEquals 'claim800 incurred ceded', 200, contract.outCoveredClaims[1].incurred
        assertEquals 'claim1000 paid ceded', 125, contract.outCoveredClaims[0].paid
        assertEquals 'claim800 paid ceded', 120, contract.outCoveredClaims[1].paid
        assertEquals 'claim1000 reserved ceded', 125, contract.outCoveredClaims[0].reserved
        assertEquals 'claim800 reserved ceded', 80, contract.outCoveredClaims[1].reserved
        assertEquals 'claim1000 change in reserves ceded', 125, contract.outCoveredClaims[0].changeInReserves
        assertEquals 'claim800 change in reserves ceded', 120, contract.outCoveredClaims[1].changeInReserves

        assertEquals '# net', 2, contract.outUncoveredClaims.size()
        assertEquals 'claim1000 incurred net', 750, contract.outUncoveredClaims[0].incurred
        assertEquals 'claim800 incurred net', 600, contract.outUncoveredClaims[1].incurred
        assertEquals 'claim1000 paid net', 375, contract.outUncoveredClaims[0].paid
        assertEquals 'claim800 paid net', 360, contract.outUncoveredClaims[1].paid
        assertEquals 'claim1000 reserved net', 375, contract.outUncoveredClaims[0].reserved
        assertEquals 'claim800 reserved net', 240, contract.outUncoveredClaims[1].reserved
        assertEquals 'claim1000 change in reserves net', 375, contract.outUncoveredClaims[0].changeInReserves
        assertEquals 'claim800 change in reserves net', 360, contract.outUncoveredClaims[1].changeInReserves
    }

    void testHalfPeriodCover() {
        MultiLinesPerilsReinsuranceContract contract = getContractHalfPeriodCoverAllLinesPerils()
        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, fractionOfPeriod: 0.25)
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, fractionOfPeriod: 0.51)
        contract.inClaims << claim1000 << claim800

        SimulationScope simulationScope = new SimulationScope()
        simulationScope.model = new VoidTestModel()
        simulationScope.iterationScope = getNewFirstIterationScope(new DateTime(2010, 1, 1, 0, 0, 0, 0), Period.years(1))
        contract.simulationScope = simulationScope

        def netClaims = new TestProbe(contract, 'outUncoveredClaims')

        contract.doCalculation()

        assertEquals '# gross covered', 1, contract.outClaimsGrossInCoveredPeriod.size()
        assertEquals 'claim1000 gross covered', claim1000, contract.outClaimsGrossInCoveredPeriod[0]

        assertEquals '# ceded', 1, contract.outCoveredClaims.size()
        assertEquals 'claim1000 incurred ceded', 250, contract.outCoveredClaims[0].incurred
        assertEquals 'claim1000 paid ceded', 125, contract.outCoveredClaims[0].paid
        assertEquals 'claim1000 reserved ceded', 125, contract.outCoveredClaims[0].reserved
        assertEquals 'claim1000 change in reserves ceded', 125, contract.outCoveredClaims[0].changeInReserves

        assertEquals '# net', 1, contract.outUncoveredClaims.size()
        assertEquals 'claim1000 incurred net', 750, contract.outUncoveredClaims[0].incurred
        assertEquals 'claim1000 paid net', 375, contract.outUncoveredClaims[0].paid
        assertEquals 'claim1000 reserved net', 375, contract.outUncoveredClaims[0].reserved
        assertEquals 'claim1000 change in reserves net', 375, contract.outUncoveredClaims[0].changeInReserves
    }

    void testThreePeriodCoverDelayed() {
        MultiLinesPerilsReinsuranceContract contract = getContractThreePeriodCoverDelayedAllLinesPerils()
        ClaimDevelopmentPacket claim1000P0 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, fractionOfPeriod: 0.25)
        ClaimDevelopmentPacket claim800P0 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, fractionOfPeriod: 0.51)
        ClaimDevelopmentPacket claim1000P1 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, fractionOfPeriod: 0.25, originalPeriod: 1)
        ClaimDevelopmentPacket claim800P1 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, fractionOfPeriod: 0.51, originalPeriod: 1)
        ClaimDevelopmentPacket claim1000P3 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, fractionOfPeriod: 0.25, originalPeriod: 3)
        ClaimDevelopmentPacket claim800P3 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, fractionOfPeriod: 0.51, originalPeriod: 3)
        ClaimDevelopmentPacket claim1000P4 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, fractionOfPeriod: 0.25, originalPeriod: 4)
        ClaimDevelopmentPacket claim800P4 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, fractionOfPeriod: 0.51, originalPeriod: 4)
        contract.inClaims << claim1000P0 << claim800P0 << claim1000P1 << claim800P1

        def netClaims = new TestProbe(contract, 'outUncoveredClaims')

        contract.doCalculation()
        assertEquals 'year 2010 # gross covered', 1, contract.outClaimsGrossInCoveredPeriod.size()
        assertEquals 'year 2010 # ceded', 1, contract.outCoveredClaims.size()
        assertEquals 'year 2010 # net', 1, contract.outUncoveredClaims.size()
        contract.reset()

        contract.inClaims << claim1000P0 << claim800P0 << claim1000P1 << claim800P1
        contract.parmContractStrategy = ReinsuranceContractType.getTrivial()
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        contract.doCalculation()
        assertEquals 'year 2011, # gross covered', 2, contract.outClaimsGrossInCoveredPeriod.size()
        assertEquals 'year 2011, claim1000 gross covered', claim1000P1, contract.outClaimsGrossInCoveredPeriod[0]
        assertEquals 'year 2011, claim800 gross covered', claim800P1, contract.outClaimsGrossInCoveredPeriod[1]

        assertEquals 'year 2011, # ceded', 2, contract.outCoveredClaims.size()
        assertEquals 'year 2011, claim1000 incurred ceded', 250, contract.outCoveredClaims[0].incurred
        assertEquals 'year 2011, claim800 incurred ceded', 200, contract.outCoveredClaims[1].incurred
        assertEquals 'year 2011, claim1000 paid ceded', 125, contract.outCoveredClaims[0].paid
        assertEquals 'year 2011, claim800 paid ceded', 120, contract.outCoveredClaims[1].paid
        assertEquals 'year 2011, claim1000 reserved ceded', 125, contract.outCoveredClaims[0].reserved
        assertEquals 'year 2011, claim800 reserved ceded', 80, contract.outCoveredClaims[1].reserved
        assertEquals 'year 2011, claim1000 change in reserves ceded', 125, contract.outCoveredClaims[0].changeInReserves
        assertEquals 'year 2011, claim800 change in reserves ceded', 120, contract.outCoveredClaims[1].changeInReserves

        assertEquals 'year 2011, # net', 2, contract.outUncoveredClaims.size()
        assertEquals 'year 2011, claim1000 incurred net', 750, contract.outUncoveredClaims[0].incurred
        assertEquals 'year 2011, claim800 incurred net', 600, contract.outUncoveredClaims[1].incurred
        assertEquals 'year 2011, claim1000 paid net', 375, contract.outUncoveredClaims[0].paid
        assertEquals 'year 2011, claim800 paid net', 360, contract.outUncoveredClaims[1].paid
        assertEquals 'year 2011, claim1000 reserved net', 375, contract.outUncoveredClaims[0].reserved
        assertEquals 'year 2011, claim800 reserved net', 240, contract.outUncoveredClaims[1].reserved
        assertEquals 'year 2011, claim1000 change in reserves net', 375, contract.outUncoveredClaims[0].changeInReserves
        assertEquals 'year 2011, claim800 change in reserves net', 360, contract.outUncoveredClaims[1].changeInReserves

        contract.reset()

        contract.inClaims << claim1000P0 << claim800P0 << claim1000P1 << claim800P1
        contract.parmContractStrategy = ReinsuranceContractType.getTrivial()
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        contract.doCalculation()
        assertEquals 'year 2012 # gross covered', 2, contract.outClaimsGrossInCoveredPeriod.size()      // todo(sku): investigate
        assertEquals 'year 2012 # ceded', 2, contract.outCoveredClaims.size()
        assertEquals 'year 2012 # net', 2, contract.outUncoveredClaims.size()

        contract.reset()

        contract.inClaims << claim1000P0 << claim800P0 << claim1000P1 << claim800P1 << claim1000P3 << claim800P3
        contract.parmContractStrategy = ReinsuranceContractType.getTrivial()
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        contract.doCalculation()
        assertEquals 'year 2013, # gross covered', 4, contract.outClaimsGrossInCoveredPeriod.size()
        assertEquals 'year 2013, claim1000 gross covered', claim1000P1, contract.outClaimsGrossInCoveredPeriod[0]
        assertEquals 'year 2013, claim1000 gross covered', claim1000P3, contract.outClaimsGrossInCoveredPeriod[1]
        assertEquals 'year 2013, claim800 gross covered', claim800P1, contract.outClaimsGrossInCoveredPeriod[2]
        assertEquals 'year 2013, claim800 gross covered', claim800P3, contract.outClaimsGrossInCoveredPeriod[3]

        assertEquals 'year 2013, # ceded', 4, contract.outCoveredClaims.size()
        assertEquals 'year 2013, claim1000 incurred ceded', 250, contract.outCoveredClaims[1].incurred
        assertEquals 'year 2013, claim800 incurred ceded', 200, contract.outCoveredClaims[2].incurred
        assertEquals 'year 2013, claim1000 paid ceded', 125, contract.outCoveredClaims[1].paid
        assertEquals 'year 2013, claim800 paid ceded', 120, contract.outCoveredClaims[2].paid
        assertEquals 'year 2013, claim1000 reserved ceded', 125, contract.outCoveredClaims[1].reserved
        assertEquals 'year 2013, claim800 reserved ceded', 80, contract.outCoveredClaims[2].reserved
        assertEquals 'year 2013, claim1000 change in reserves ceded', 125, contract.outCoveredClaims[1].changeInReserves
        assertEquals 'year 2013, claim800 change in reserves ceded', 120, contract.outCoveredClaims[2].changeInReserves

        assertEquals 'year 2013, # net', 4, contract.outUncoveredClaims.size()
        assertEquals 'year 2013, claim1000 incurred net', 750, contract.outUncoveredClaims[1].incurred
        assertEquals 'year 2013, claim800 incurred net', 600, contract.outUncoveredClaims[2].incurred
        assertEquals 'year 2013, claim1000 paid net', 375, contract.outUncoveredClaims[1].paid
        assertEquals 'year 2013, claim800 paid net', 360, contract.outUncoveredClaims[2].paid
        assertEquals 'year 2013, claim1000 reserved net', 375, contract.outUncoveredClaims[1].reserved
        assertEquals 'year 2013, claim800 reserved net', 240, contract.outUncoveredClaims[2].reserved
        assertEquals 'year 2013, claim1000 change in reserves net', 375, contract.outUncoveredClaims[1].changeInReserves
        assertEquals 'year 2013, claim800 change in reserves net', 360, contract.outUncoveredClaims[2].changeInReserves

        contract.reset()

        contract.inClaims << claim1000P0 << claim800P0 << claim1000P1 << claim800P1 << claim1000P4 << claim800P4
        contract.parmContractStrategy = ReinsuranceContractType.getTrivial()
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        contract.doCalculation()
        assertEquals 'year 2014 # gross covered', 2, contract.outClaimsGrossInCoveredPeriod.size()
        assertEquals 'year 2014 # ceded', 2, contract.outCoveredClaims.size()
        assertEquals 'year 2014 # net', 2, contract.outUncoveredClaims.size()
    }

    void testNoContractSet() {
        MultiLinesPerilsReinsuranceContract contract = getContractFullCoverAllLinesPerils()
        contract.parmContractStrategy = null

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500)
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480)
        contract.inClaims << claim1000 << claim800

        shouldFail IllegalArgumentException, {contract.doCalculation()}
    }

    /**
     * Each contract may not have more than one nontrivial contract strategy instance over the scope of a simulation.
     */
    void testTwoContractSet() {
        // set up a contract (25% quota share) for 4 years (1.1.2010--31.12.2013)
        // with inuring priority 0 and iteration scope starting 1.1.2010
        MultiLinesPerilsReinsuranceContract contract = getAllLinesPerilsQuotaShareContract(2010, 2010, 4, 0.25, 0)

        // define some claims
        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500)
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480)

        // simulate the first period of the first iteration
        contract.inClaims << claim1000 << claim800
        contract.doCalculation()

        // simulate the second period, but with another nontrivial contract strategy (instance)
        contract.reset()
        contract.parmContractStrategy = ReinsuranceContractType.getContractStrategy(
                ReinsuranceContractType.QUOTASHARE, ['quotaShare': 0.25])
        contract.parmCommissionStrategy = CommissionStrategyType.getStrategy(
                CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0d])

        contract.inClaims << claim1000 << claim800
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        shouldFail IllegalArgumentException, {contract.doCalculation()}
    }

    /**
     * Make sure that filterClaimsInCoveredPeriod rejects processing all subclasses of Claim other than
     * ClaimDevelopmentPacket, i.e. (currently) it should throw a NotImplementedException if any claim
     * in inClaims is an instance of one of these classes:
     * <ul><li>ClaimDevelopmentLeanPacket</li>
     * <li>ClaimDevelopmentWithIBNRPacket</li>
     */
    void testUnimplementedClaim() {
        MultiLinesPerilsReinsuranceContract contract = getAllLinesPerilsQuotaShareContract(2011, 2011, 1, 0.5, 0)

        contract.inClaims << new ClaimDevelopmentLeanPacket()
        shouldFail NotImplementedException, {contract.doCalculation()}

        contract.reset()
        contract.parmContractStrategy = ReinsuranceContractType.getTrivial()
        contract.inClaims << new ClaimDevelopmentWithIBNRPacket()
        shouldFail NotImplementedException, {contract.doCalculation()}
    }

    /**
     * Make sure that filterClaimsInCoveredPeriod processes a claim of class Claim (code coverage)
     */
    void testClaimNotFromSubclass() {
        MultiLinesPerilsReinsuranceContract contract = getAllLinesPerilsQuotaShareContract(2011, 2011, 1, 0.5, 0)
        Claim claimInTime = new Claim()
        claimInTime.setFractionOfPeriod 0.9
        contract.inClaims << claimInTime
        contract.doCalculation()
        assertEquals '# claims covered in period', 1, contract.outClaimsGrossInCoveredPeriod.size()

        contract = getAllLinesPerilsQuotaShareContract(2011, 2011, 1, 0.5, 0)
        Claim claimTooLate = new Claim()
        claimTooLate.setFractionOfPeriod 1
        contract.inClaims << claimTooLate
        contract.doCalculation()
        assertEquals '# claims covered in period', 1, contract.outClaimsGrossInCoveredPeriod.size()
        assertEquals 'zero claim', 0, contract.outClaimsGrossInCoveredPeriod[0].ultimate
    }

    /**
     * Make sure that Net UnderwritingInfo is not calculated when not wired in a contract (code/branch coverage test)
     */
    void testUnderwritingInfoCededButNotNetWired() {
        MultiLinesPerilsReinsuranceContract contract = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, 0.25, 0, 0.2)
        contract.name = 'Quota Share 25%'

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        contract.inClaims << claim1000 << claim800

        UnderwritingInfo originalUnderwritingInfo200 = new UnderwritingInfo(premium: 202, commission: 17)
        UnderwritingInfo originalUnderwritingInfo100 = new UnderwritingInfo(premium: 101, commission: 7)
        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: 13, originalUnderwritingInfo: originalUnderwritingInfo200)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: 3, originalUnderwritingInfo: originalUnderwritingInfo100)
        contract.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        def wiredUWInfoFiltered = new TestProbe(contract, 'outFilteredUnderwritingInfo')
        def wiredUWInfoCeded = new TestProbe(contract, 'outCoverUnderwritingInfo')

        contract.doCalculation()

        assertEquals 'number of UnderwritingInfo packets ceded after contract', 2, contract.outFilteredUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets ceded after contract', 2, contract.outCoverUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets net after contract', 0, contract.outNetAfterCoverUnderwritingInfo.size()

        assertEquals 'underwritinginfo200 ceded premium written after contract', 50, contract.outCoverUnderwritingInfo[0].premium
        assertEquals 'underwritinginfo100 ceded premium written after contract', 25, contract.outCoverUnderwritingInfo[1].premium
        assertEquals 'underwritinginfo200 ceded commission after contract', -0.2 * 0.25 * 200, contract.outCoverUnderwritingInfo[0].commission
        assertEquals 'underwritinginfo100 ceded commission after contract', -0.2 * 0.25 * 100, contract.outCoverUnderwritingInfo[1].commission
    }

    /**
     * This test, testUnderwritingInfoNetButNotCededWired, complements the
     * one above, testUnderwritingInfoCededButNotNetWired, but should
     * not be necessary for code coverage.
     */
    void testUnderwritingInfoNetButNotCededWired() {
        MultiLinesPerilsReinsuranceContract contract = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, 0.25, 0, 0.2)
        contract.name = 'Quota Share 25%'

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        contract.inClaims << claim1000 << claim800

        UnderwritingInfo originalUnderwritingInfo200 = new UnderwritingInfo(premium: 202, commission: 17)
        UnderwritingInfo originalUnderwritingInfo100 = new UnderwritingInfo(premium: 101, commission: 7)
        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: 13, originalUnderwritingInfo: originalUnderwritingInfo200)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: 3, originalUnderwritingInfo: originalUnderwritingInfo100)
        contract.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        def wiredUWInfoFiltered = new TestProbe(contract, 'outFilteredUnderwritingInfo')
        def wiredUWInfoNet = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')

        contract.doCalculation()

        assertEquals 'number of UnderwritingInfo packets ceded after contract', 2, contract.outFilteredUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets ceded after contract', 2, contract.outCoverUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets net after contract', 2, contract.outNetAfterCoverUnderwritingInfo.size()

        // these values should be the same as in testUnderwritingInfoCededButNotNetWired (because when Net is wired, Ceded is also calculated)
        assertEquals 'underwritinginfo200 ceded premium written after contract', 50, contract.outCoverUnderwritingInfo[0].premium
        assertEquals 'underwritinginfo100 ceded premium written after contract', 25, contract.outCoverUnderwritingInfo[1].premium
        assertEquals 'underwritinginfo200 ceded commission after contract', -0.2 * 0.25 * 200, contract.outCoverUnderwritingInfo[0].commission
        assertEquals 'underwritinginfo100 ceded commission after contract', -0.2 * 0.25 * 100, contract.outCoverUnderwritingInfo[1].commission

        // these values were not present in testUnderwritingInfoCededButNotNetWired (since Net was not wired, only Ceded)
        assertEquals 'underwritinginfo200 net premium written after contract', 150, contract.outNetAfterCoverUnderwritingInfo[0].premium
        assertEquals 'underwritinginfo100 net premium written after contract', 75, contract.outNetAfterCoverUnderwritingInfo[1].premium
        assertEquals 'underwritinginfo200 net commission after contract', 0.2 * 0.25 * 200, contract.outNetAfterCoverUnderwritingInfo[0].commission
        assertEquals 'underwritinginfo100 net commission after contract', 0.2 * 0.25 * 100, contract.outNetAfterCoverUnderwritingInfo[1].commission
    }

    void testTrivialContractStrategy() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getContractStrategy(ReinsuranceContractType.TRIVIAL, [:])
        )
        contract.simulationScope = new SimulationScope(
                iterationScope: getNewFirstIterationScope(new DateTime(2010, 1, 1, 0, 0, 0, 0), Period.years(1)),
                model: new VoidTestModel()
        )

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        contract.inClaims << claim1000 << claim800

        UnderwritingInfo originalUnderwritingInfo200 = new UnderwritingInfo(premium: 200, commission: 50)
        UnderwritingInfo originalUnderwritingInfo100 = new UnderwritingInfo(premium: 100, commission: 5)
        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: 50, originalUnderwritingInfo: originalUnderwritingInfo200)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: 5, originalUnderwritingInfo: originalUnderwritingInfo100)
        contract.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        def netClaims = new TestProbe(contract, 'outUncoveredClaims')
        def cededClaims = new TestProbe(contract, 'outCoveredClaims')

        def wiredUWInfoFiltered = new TestProbe(contract, 'outFilteredUnderwritingInfo')
        def wiredUWInfoNet = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')

        contract.doCalculation()

        assertEquals 'number of claim packets net after contract', 0, contract.outUncoveredClaims.size()
        assertEquals 'number of claim packets ceded after contract', 0, contract.outCoveredClaims.size()

        assertEquals 'number of UnderwritingInfo packets filtered', 2, contract.outFilteredUnderwritingInfo.size() // is 2 since trivial contract cover is ALL
        assertEquals 'number of UnderwritingInfo packets ceded after contract', 0, contract.outCoverUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets net after contract', 2, contract.outNetAfterCoverUnderwritingInfo.size()
    }

    void testTrivialContractStrategyNoneCovered() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]),
                parmCover: new NoneCoverAttributeStrategy()
        )
        contract.simulationScope = new SimulationScope(
                iterationScope: getNewFirstIterationScope(new DateTime(2010, 1, 1, 0, 0, 0, 0), Period.years(1)),
                model: new VoidTestModel()
        )

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        contract.inClaims << claim1000 << claim800

        UnderwritingInfo originalUnderwritingInfo200 = new UnderwritingInfo(premium: 200, commission: 50)
        UnderwritingInfo originalUnderwritingInfo100 = new UnderwritingInfo(premium: 100, commission: 5)
        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: 50, originalUnderwritingInfo: originalUnderwritingInfo200)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: 5, originalUnderwritingInfo: originalUnderwritingInfo100)
        contract.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        def netClaims = new TestProbe(contract, 'outUncoveredClaims')
        def cededClaims = new TestProbe(contract, 'outCoveredClaims')

        def wiredUWInfoFiltered = new TestProbe(contract, 'outFilteredUnderwritingInfo')
        def wiredUWInfoNet = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')

        contract.doCalculation()

        assertEquals 'number of claim packets net after contract', 0, contract.outUncoveredClaims.size()
        assertEquals 'number of claim packets ceded after contract', 0, contract.outCoveredClaims.size()

        assertEquals 'number of UnderwritingInfo packets filtered', 0, contract.outFilteredUnderwritingInfo.size() // is 0 since none are covered
        assertEquals 'number of UnderwritingInfo packets ceded after contract', 0, contract.outCoverUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets net after contract', 0, contract.outNetAfterCoverUnderwritingInfo.size()
    }

    void testUsageWithCombinedFiltering() {
        SimulationScope simulationScope = new SimulationScope(model: new VoidTestModel())

        // create peril markers (and add them to the simulation model)
        TestPerilComponent perilA = new TestPerilComponent(name: 'peril a')
        TestPerilComponent perilB = new TestPerilComponent(name: 'peril b')
        simulationScope.model.allComponents << perilA << perilB

        // create LOB markers (and add them to the simulation model)
        Map<String, TestLobComponent> lob = MultiCoverAttributeReinsuranceContractTests.createLobs(['fire', 'hull'], simulationScope.model)

        // create contract; choose QuotaShareContractStrategy and LineOfBusinessPerilsCoverAttributeStrategy
        MultiLinesPerilsReinsuranceContract contract = getAllLinesPerilsQuotaShareContract(2010, 2010, 4, 0.25, 0, 0.2)
        // contract only covers hull LOB from peril B (both conditions are enforced; connection type is AND)
        contract.parmCover = getCoverAttributeStrategy(['lines': ['hull'], 'perils': ['peril b']], simulationScope.model)

        Claim claim001 = new Claim(lineOfBusiness: lob['fire'], peril: perilA, value: 1000, fractionOfPeriod: 0.1, claimType: ClaimType.SINGLE)
        Claim claim002 = new Claim(lineOfBusiness: lob['fire'], peril: perilA, value: 2000, fractionOfPeriod: 0.2, claimType: ClaimType.ATTRITIONAL)
        Claim claim011 = new Claim(lineOfBusiness: lob['fire'], peril: perilB, value: 3000, fractionOfPeriod: 0.4, claimType: ClaimType.SINGLE)
        Claim claim012 = new Claim(lineOfBusiness: lob['fire'], peril: perilB, value: 4000, fractionOfPeriod: 0.8, claimType: ClaimType.ATTRITIONAL)
        Claim claim101 = new Claim(lineOfBusiness: lob['hull'], peril: perilA, value: 5000, fractionOfPeriod: 0.5, claimType: ClaimType.SINGLE)
        Claim claim102 = new Claim(lineOfBusiness: lob['hull'], peril: perilA, value: 6000, fractionOfPeriod: 0.0, claimType: ClaimType.ATTRITIONAL)
        Claim claim111 = new Claim(lineOfBusiness: lob['hull'], peril: perilB, value: 7000, fractionOfPeriod: 0.9, claimType: ClaimType.SINGLE)
        Claim claim112 = new Claim(lineOfBusiness: lob['hull'], peril: perilB, value: 8000, fractionOfPeriod: 0.7, claimType: ClaimType.ATTRITIONAL)

        UnderwritingInfo uInfo001 = new UnderwritingInfo(lineOfBusiness: lob['fire'], origin: perilA, premium: 120, commission: 11)
        UnderwritingInfo uInfo002 = new UnderwritingInfo(lineOfBusiness: lob['fire'], origin: perilA, premium: 220, commission: 13)
        UnderwritingInfo uInfo011 = new UnderwritingInfo(lineOfBusiness: lob['fire'], origin: perilB, premium: 130, commission: 17)
        UnderwritingInfo uInfo012 = new UnderwritingInfo(lineOfBusiness: lob['fire'], origin: perilB, premium: 230, commission: 19)
        UnderwritingInfo uInfo101 = new UnderwritingInfo(lineOfBusiness: lob['hull'], origin: perilA, premium: 320, commission: 11)
        UnderwritingInfo uInfo102 = new UnderwritingInfo(lineOfBusiness: lob['hull'], origin: perilA, premium: 420, commission: 13)
        UnderwritingInfo uInfo111 = new UnderwritingInfo(lineOfBusiness: lob['hull'], origin: perilB, premium: 330, commission: 17)
        UnderwritingInfo uInfo112 = new UnderwritingInfo(lineOfBusiness: lob['hull'], origin: perilB, premium: 430, commission: 19)

        // wire all out channels
        List outChannelsWired = [
                'outFilteredClaims',
                'outFilteredUnderwritingInfo',
                'outClaimsGrossInCoveredPeriod',
                'outUncoveredClaims',
                'outCoveredClaims',
                'outNetAfterCoverUnderwritingInfo',
                'outCoverUnderwritingInfo',
        ].collect {new TestProbe(contract, it)}

        contract.inClaims << claim001 << claim002 << claim011 << claim012 << claim101 << claim102 << claim111 << claim112
        contract.inUnderwritingInfo << uInfo001 << uInfo002 << uInfo011 << uInfo012 << uInfo101 << uInfo102 << uInfo111 << uInfo112
        contract.doCalculation()

        assertEquals "# of outPackets", "2, 4, 2, 2, 2, 0, 0, 0, 4, 4", ([
                contract.outFilteredClaims,                 // 2
                contract.outFilteredUnderwritingInfo,       // 4
                contract.outClaimsGrossInCoveredPeriod,     // 2
                contract.outUncoveredClaims,                // 2
                contract.outCoveredClaims,                  // 2
                contract.outClaimsDevelopmentGross,         // 0
                contract.outClaimsDevelopmentCeded,         // 0
                contract.outClaimsDevelopmentNet,           // 0
                contract.outNetAfterCoverUnderwritingInfo,  // 4
                contract.outCoverUnderwritingInfo,          // 4
        ].collect {it.size()}).join(", ")

        // check that (hull/B claims, hull/AB uwinfo) packets were processed correctly:
        // claims are filtered by peril and lob (=> hull/B),
        assertEquals "filtered claims", "7000.0, 8000.0", (contract.outFilteredClaims.collect {it.value}).join(", ") // Q: should fractionOfPeriod force the reverse order of packets?
        // underwriting by lob but not by peril (=> hull/A, hull/B)
        assertEquals "filtered uwinfo", "320.0, 420.0, 330.0, 430.0", (contract.outFilteredUnderwritingInfo.collect {it.premium}).join(", ")
        assertEquals "ceded premium written", "80.0, 105.0, 82.5, 107.5", (contract.outCoverUnderwritingInfo.collect {it.premium}).join(", ")
        assertEquals "ceded commission", "-16.0, -21.0, -16.5, -21.5", (contract.outCoverUnderwritingInfo.collect {it.commission}).join(", ")
        assertEquals "net premium written", "240.0, 315.0, 247.5, 322.5", (contract.outNetAfterCoverUnderwritingInfo.collect {it.premium}).join(", ")
        assertEquals "net commission", "16.0, 21.0, 16.5, 21.5", (contract.outNetAfterCoverUnderwritingInfo.collect {it.commission}).join(", ")
    }
}
