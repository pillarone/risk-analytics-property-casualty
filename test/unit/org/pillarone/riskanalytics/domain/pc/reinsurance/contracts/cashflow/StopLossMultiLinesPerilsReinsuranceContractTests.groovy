package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.joda.time.Period
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class StopLossMultiLinesPerilsReinsuranceContractTests extends GroovyTestCase {

        static MultiLinesPerilsReinsuranceContract getContractFullCoverAllLinesPerils() {
        MultiLinesPerilsReinsuranceContract contract = new MultiLinesPerilsReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getContractStrategy(
                        ReinsuranceContractType.STOPLOSS, ['attachmentPoint': 150, 'limit': 150, 'termLimit': 300,
                                                           'premiumBase': PremiumBase.ABSOLUTE, 'premium': 10]),
                parmCoverPeriod : CoverPeriodType.getStrategy(
                        CoverPeriodType.PERIOD,
                        ['start': new DateTime(2011,1,1,0,0,0,0), 'end': new DateTime(2013,12,31,0,0,0,0)]),
                parmCoveredByReinsurer : 1d,
                parmInuringPriority : 0d)
        SimulationScope simulationScope= new SimulationScope()
        simulationScope.model = new VoidTestModel()
        simulationScope.iterationScope = MultiLinesPerilsReinsuranceContractTests.getNewFirstIterationScope(new DateTime(2011,1,1,0,0,0,0), Period.months(3))
        contract.simulationScope = simulationScope

        return contract
    }

    void testLimitUpdating() {
        MultiLinesPerilsReinsuranceContract stopLoss = getContractFullCoverAllLinesPerils()
        ClaimDevelopmentPacket claim1000P0 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalPeriod: 0)
        ClaimDevelopmentPacket claim800P1 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalPeriod: 1)
        ClaimDevelopmentPacket claim800P2 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalPeriod: 2)
        ClaimDevelopmentPacket claim800P3 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalPeriod: 3)
        ClaimDevelopmentPacket claim200P4 = new ClaimDevelopmentPacket(incurred: 200, paid: 120, reserved: 80, changeInReserves: 120, originalPeriod: 4)
        ClaimDevelopmentPacket claim200P5 = new ClaimDevelopmentPacket(incurred: 200, paid: 80, reserved: 120, changeInReserves: 80, originalPeriod: 5)
        ClaimDevelopmentPacket claim800P6 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalPeriod: 6)
        stopLoss.inClaims << claim1000P0 //<< claim800P1 << claim200P5 << claim200P6

        List netClaims = new TestProbe(stopLoss, 'outUncoveredClaims').result
        List cededClaims = new TestProbe(stopLoss, 'outCoveredClaims').result

        stopLoss.start()

        assertEquals "claim1000P0 ceded incurred", 150, cededClaims[0].incurred
        assertEquals "claim1000P0 ceded paid", 150, cededClaims[0].paid
        assertEquals "claim1000P0 ceded reserved", 0, cededClaims[0].reserved
        assertEquals "claim1000P0 net incurred", 850, netClaims[0].incurred
        assertEquals "claim1000P0 net paid", 350, netClaims[0].paid
        assertEquals "claim1000P0 net reserved", 500, netClaims[0].reserved

        netClaims.clear()
        cededClaims.clear()
        stopLoss.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        stopLoss.inClaims << claim800P1
        stopLoss.start()

        assertEquals "claim800P1 ceded incurred", 0, cededClaims[0].incurred
        assertEquals "claim800P1 ceded paid", 0, cededClaims[0].paid
        assertEquals "claim800P1 ceded reserved", 0, cededClaims[0].reserved
        assertEquals "claim800P1 net incurred", 800, netClaims[0].incurred
        assertEquals "claim800P1 net paid", 480, netClaims[0].paid
        assertEquals "claim800P1 net reserved", 320, netClaims[0].reserved

        netClaims.clear()
        cededClaims.clear()
        stopLoss.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        stopLoss.inClaims << claim800P2
        stopLoss.start()

        assertEquals "claim800P2 ceded incurred", 0, cededClaims[0].incurred
        assertEquals "claim800P2 ceded paid", 0, cededClaims[0].paid
        assertEquals "claim800P2 ceded reserved", 0, cededClaims[0].reserved
        assertEquals "claim800P2 net incurred", 800, netClaims[0].incurred
        assertEquals "claim800P2 net paid", 480, netClaims[0].paid
        assertEquals "claim800P2 net reserved", 320, netClaims[0].reserved

        netClaims.clear()
        cededClaims.clear()
        stopLoss.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        stopLoss.inClaims << claim800P3
        stopLoss.start()

        assertEquals "claim800P3 ceded incurred", 0, cededClaims[0].incurred
        assertEquals "claim800P3 ceded paid", 0, cededClaims[0].paid
        assertEquals "claim800P3 ceded reserved", 0, cededClaims[0].reserved
        assertEquals "claim800P3 net incurred", 800, netClaims[0].incurred
        assertEquals "claim800P3 net paid", 480, netClaims[0].paid
        assertEquals "claim800P3 net reserved", 320, netClaims[0].reserved

        netClaims.clear()
        cededClaims.clear()
        stopLoss.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        stopLoss.inClaims << claim200P4
        stopLoss.start()

        assertEquals "claim200P4 ceded incurred", 50, cededClaims[0].incurred
        assertEquals "claim200P4 ceded paid", 0, cededClaims[0].paid
        assertEquals "claim200P4 ceded reserved", 50, cededClaims[0].reserved
        assertEquals "claim200P4 net incurred", 150, netClaims[0].incurred
        assertEquals "claim200P4 net paid", 120, netClaims[0].paid
        assertEquals "claim200P4 net reserved", 30, netClaims[0].reserved

        netClaims.clear()
        cededClaims.clear()
        stopLoss.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        stopLoss.inClaims << claim200P5
        stopLoss.start()

        assertEquals "claim200P5 ceded incurred", 100, cededClaims[0].incurred
        assertEquals "claim200P5 ceded paid", 50, cededClaims[0].paid
        assertEquals "claim200P5 ceded reserved", 50, cededClaims[0].reserved
        assertEquals "claim200P5 net incurred", 100, netClaims[0].incurred
        assertEquals "claim200P5 net paid", 30, netClaims[0].paid
        assertEquals "claim200P5 net reserved", 70, netClaims[0].reserved

        netClaims.clear()
        cededClaims.clear()
        stopLoss.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        stopLoss.inClaims << claim800P6
        stopLoss.start()

        assertEquals "claim800P6 ceded incurred", 0, cededClaims[0].incurred
        assertEquals "claim800P6 ceded paid", 100, cededClaims[0].paid
        assertEquals "claim800P6 ceded reserved", 100, cededClaims[0].reserved
        assertEquals "claim800P6 net incurred", 800, netClaims[0].incurred
        assertEquals "claim800P6 net paid", 380, netClaims[0].paid
        assertEquals "claim800P6 net reserved", 220, netClaims[0].reserved
    }
}
