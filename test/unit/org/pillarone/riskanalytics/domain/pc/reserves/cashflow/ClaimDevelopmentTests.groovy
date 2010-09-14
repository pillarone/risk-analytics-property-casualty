package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.assets.VoidTestModel

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimDevelopmentTests extends GroovyTestCase {

    void testIncrementalPayoutPattern() {
        ClaimDevelopment claimDevelopment = new ClaimDevelopment(
                parmPayoutPattern : PatternStrategyType.getStrategy(PatternStrategyType.INCREMENTAL,
                        ["incrementalPattern":new TableMultiDimensionalParameter([0.7d, 0.3d],["Increments"]),]),
                parmReportedPattern : PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:]),
                parmAppliedOnPerils : new ComboBoxTableMultiDimensionalParameter(
                        Collections.emptyList(), Arrays.asList("peril"), PerilMarker)
        )
        claimDevelopment.simulationScope = new SimulationScope(model: new VoidTestModel())
        claimDevelopment.periodScope = new PeriodScope()
        claimDevelopment.periodStore = new PeriodStore(claimDevelopment.periodScope)
        Claim claim1000 =new Claim(ultimate : 1000)
        Claim claim200 =new Claim(ultimate : 200)
        claimDevelopment.inClaims << claim1000 << claim200

        claimDevelopment.doCalculation()

        assertEquals 'period 0, #packets', claimDevelopment.inClaims.size(), claimDevelopment.outClaims.size()
        assertEquals 'period 0, incurred 1000', 1000, claimDevelopment.outClaims[0].getIncurred()
        assertEquals 'period 0, incurred 200', 200, claimDevelopment.outClaims[1].getIncurred()
        assertEquals 'period 0, paid 1000', 700, claimDevelopment.outClaims[0].getPaid()
        assertEquals 'period 0, paid 200', 140, claimDevelopment.outClaims[1].getPaid()
        assertEquals 'period 0, reserved 1000', 300, claimDevelopment.outClaims[0].getReserved()
        assertEquals 'period 0, reserved 200', 60, claimDevelopment.outClaims[1].getReserved()
        assertEquals 'period 0, change in reserves 1000', 700, claimDevelopment.outClaims[0].getChangeInReserves()
        assertEquals 'period 0, change in reserves 200', 140, claimDevelopment.outClaims[1].getChangeInReserves()

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 1, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 1, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred()
        assertEquals 'period 1, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred()
        assertEquals 'period 1, paid 1000', 300, claimDevelopment.outClaims[0].getPaid(), 1E10
        assertEquals 'period 1, paid 200', 60, claimDevelopment.outClaims[1].getPaid(), 1E10
        assertEquals 'period 1, reserved 1000', 0, claimDevelopment.outClaims[0].getReserved(), 1E10
        assertEquals 'period 1, reserved 200', 0, claimDevelopment.outClaims[1].getReserved(), 1E10
        assertEquals 'period 1, change in reserves 1000', -300, claimDevelopment.outClaims[0].getChangeInReserves(), 1E10
        assertEquals 'period 1, change in reserves 200', -60, claimDevelopment.outClaims[1].getChangeInReserves(), 1E10
    }

    void testCumulativePayoutPattern() {
        ClaimDevelopment claimDevelopment = new ClaimDevelopment(
                parmPayoutPattern : PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE,
                        ["cumulativePattern":new TableMultiDimensionalParameter([0.7d, 1d],["Cumulative"]),]),
                parmReportedPattern : PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:]),
                parmAppliedOnPerils : new ComboBoxTableMultiDimensionalParameter(
                        Collections.emptyList(), Arrays.asList("peril"), PerilMarker)
        )
        claimDevelopment.simulationScope = new SimulationScope(model: new VoidTestModel())
        claimDevelopment.periodScope = new PeriodScope()
        claimDevelopment.periodStore = new PeriodStore(claimDevelopment.periodScope)
        Claim claim1000 =new Claim(ultimate : 1000)
        Claim claim200 =new Claim(ultimate : 200)
        claimDevelopment.inClaims << claim1000 << claim200

        claimDevelopment.doCalculation()

        assertEquals 'period 0, #packets', claimDevelopment.inClaims.size(), claimDevelopment.outClaims.size()
        assertEquals 'period 0, incurred 1000', 1000, claimDevelopment.outClaims[0].getIncurred()
        assertEquals 'period 0, incurred 200', 200, claimDevelopment.outClaims[1].getIncurred()
        assertEquals 'period 0, paid 1000', 700, claimDevelopment.outClaims[0].getPaid()
        assertEquals 'period 0, paid 200', 140, claimDevelopment.outClaims[1].getPaid()
        assertEquals 'period 0, reserved 1000', 300, claimDevelopment.outClaims[0].getReserved()
        assertEquals 'period 0, reserved 200', 60, claimDevelopment.outClaims[1].getReserved()
        assertEquals 'period 0, change in reserves 1000', 700, claimDevelopment.outClaims[0].getChangeInReserves()
        assertEquals 'period 0, change in reserves 200', 140, claimDevelopment.outClaims[1].getChangeInReserves()

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 1, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 1, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred()
        assertEquals 'period 1, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred()
        assertEquals 'period 1, paid 1000', 300, claimDevelopment.outClaims[0].getPaid(), 1E10
        assertEquals 'period 1, paid 200', 60, claimDevelopment.outClaims[1].getPaid(), 1E10
        assertEquals 'period 1, reserved 1000', 0, claimDevelopment.outClaims[0].getReserved(), 1E10
        assertEquals 'period 1, reserved 200', 0, claimDevelopment.outClaims[1].getReserved(), 1E10
        assertEquals 'period 1, change in reserves 1000', -300, claimDevelopment.outClaims[0].getChangeInReserves(), 1E10
        assertEquals 'period 1, change in reserves 200', -60, claimDevelopment.outClaims[1].getChangeInReserves(), 1E10
    }

    void testNoPayoutPattern() {
        ClaimDevelopment claimDevelopment = new ClaimDevelopment(
                parmPayoutPattern : PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:]),
                parmReportedPattern : PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:]),
                parmAppliedOnPerils : new ComboBoxTableMultiDimensionalParameter(
                        Collections.emptyList(), Arrays.asList("peril"), PerilMarker)
        )
        claimDevelopment.simulationScope = new SimulationScope(model: new VoidTestModel())
        claimDevelopment.periodScope = new PeriodScope()
        claimDevelopment.periodStore = new PeriodStore(claimDevelopment.periodScope)
        Claim claim1000 =new Claim(ultimate : 1000)
        Claim claim200 =new Claim(ultimate : 200)
        claimDevelopment.inClaims << claim1000 << claim200

        claimDevelopment.doCalculation()

        assertEquals 'period 0, #packets', claimDevelopment.inClaims.size(), claimDevelopment.outClaims.size()
        assertEquals 'period 0, incurred 1000', 1000, claimDevelopment.outClaims[0].getIncurred()
        assertEquals 'period 0, incurred 200', 200, claimDevelopment.outClaims[1].getIncurred()
        assertEquals 'period 0, paid 1000', 1000, claimDevelopment.outClaims[0].getPaid()
        assertEquals 'period 0, paid 200', 200, claimDevelopment.outClaims[1].getPaid()
        assertEquals 'period 0, reserved 1000', 0, claimDevelopment.outClaims[0].getReserved()
        assertEquals 'period 0, reserved 200', 0, claimDevelopment.outClaims[1].getReserved()
        assertEquals 'period 0, change in reserves 1000', 1000, claimDevelopment.outClaims[0].getChangeInReserves()
        assertEquals 'period 0, change in reserves 200', 200, claimDevelopment.outClaims[1].getChangeInReserves()

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 1, #packets', 0, claimDevelopment.outClaims.size()
    }

    void testCumulativePatterns() {
        ClaimDevelopment claimDevelopment = new ClaimDevelopment(
                parmPayoutPattern : PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.4d, 0.7d, 0.9d, 1d],["Cumulative"]),]),
                parmReportedPattern : PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.8d, 0.9d, 0.95d, 1d],["Cumulative"]),]),
                parmAppliedOnPerils : new ComboBoxTableMultiDimensionalParameter(
                        Collections.emptyList(), Arrays.asList("peril"), PerilMarker)
        )
        claimDevelopment.simulationScope = new SimulationScope(model: new VoidTestModel())
        claimDevelopment.periodScope = new PeriodScope()
        claimDevelopment.periodStore = new PeriodStore(claimDevelopment.periodScope)
        Claim claim800 =new Claim(ultimate : 800)
        Claim claim200 =new Claim(ultimate : 200)
        claimDevelopment.inClaims << claim800 << claim200

        claimDevelopment.doCalculation()

        assertEquals 'period 0, #packets', claimDevelopment.inClaims.size(), claimDevelopment.outClaims.size()
        assertEquals 'period 0, incurred 800', 800, claimDevelopment.outClaims[0].getIncurred()
        assertEquals 'period 0, incurred 200', 200, claimDevelopment.outClaims[1].getIncurred()
        assertEquals 'period 0, paid 800', 256, claimDevelopment.outClaims[0].getPaid()
        assertEquals 'period 0, paid 200', 64, claimDevelopment.outClaims[1].getPaid()
        assertEquals 'period 0, reported 800', 640, claimDevelopment.outClaims[0].getReported()
        assertEquals 'period 0, reported 200', 160, claimDevelopment.outClaims[1].getReported()
        assertEquals 'period 0, reserved 800', 384, claimDevelopment.outClaims[0].getReserved()
        assertEquals 'period 0, reserved 200', 96, claimDevelopment.outClaims[1].getReserved()
        assertEquals 'period 0, ibnr 800', 160, claimDevelopment.outClaims[0].getIbnr()
        assertEquals 'period 0, ibnr 200', 40, claimDevelopment.outClaims[1].getIbnr()
        assertEquals 'period 0, change in reserves 800', -256, claimDevelopment.outClaims[0].getChangeInReserves()
        assertEquals 'period 0, change in reserves 200', -64, claimDevelopment.outClaims[1].getChangeInReserves()

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 1, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 1, incurred 800', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 1, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred(), 1E-8
        assertEquals 'period 1, paid 800', 248, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 1, paid 200', 62, claimDevelopment.outClaims[1].getPaid(), 1E-8
        assertEquals 'period 1, reported 800', 720, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 1, reported 200', 180, claimDevelopment.outClaims[1].getReported(), 1E-8
        assertEquals 'period 1, reserved 800', 216, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 1, reserved 200', 54, claimDevelopment.outClaims[1].getReserved(), 1E-8
        assertEquals 'period 1, ibnr 800', 80, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 1, ibnr 200', 20, claimDevelopment.outClaims[1].getIbnr(), 1E-8
        assertEquals 'period 1, change in reserves 800', -248, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
        assertEquals 'period 1, change in reserves 200', -62, claimDevelopment.outClaims[1].getChangeInReserves(), 1E-8

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 2, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 2, incurred 800', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 2, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred(), 1E-8
        assertEquals 'period 2, paid 800', 180, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 2, paid 200', 45, claimDevelopment.outClaims[1].getPaid(), 1E-8
        assertEquals 'period 2, reported 800', 760, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 2, reported 200', 190, claimDevelopment.outClaims[1].getReported(), 1E-8
        assertEquals 'period 2, reserved 800', 76, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 2, reserved 200', 19, claimDevelopment.outClaims[1].getReserved(), 1E-8
        assertEquals 'period 2, ibnr 800', 40, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 2, ibnr 200', 10, claimDevelopment.outClaims[1].getIbnr(), 1E-8
        assertEquals 'period 2, change in reserves 800', -180, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
        assertEquals 'period 2, change in reserves 200', -45, claimDevelopment.outClaims[1].getChangeInReserves(), 1E-8

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 3, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 3, incurred 800', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 3, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred(), 1E-8
        assertEquals 'period 3, paid 800', 116, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 3, paid 200', 29, claimDevelopment.outClaims[1].getPaid(), 1E-8
        assertEquals 'period 3, reported 800', 800, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 3, reported 200', 200, claimDevelopment.outClaims[1].getReported(), 1E-8
        assertEquals 'period 3, reserved 800', 0, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 3, reserved 200', 0, claimDevelopment.outClaims[1].getReserved(), 1E-8
        assertEquals 'period 3, ibnr 800', 0, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 3, ibnr 200', 0, claimDevelopment.outClaims[1].getIbnr(), 1E-8
        assertEquals 'period 3, change in reserves 800', -116, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
        assertEquals 'period 3, change in reserves 200', -29, claimDevelopment.outClaims[1].getChangeInReserves(), 1E-8 
    }

    void testHistoricLastPaid() {
        ClaimDevelopment claimDevelopment = new ClaimDevelopment(
                parmPayoutPattern : PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.4d, 0.7d, 0.9d, 1d],["Cumulative"]),]),
                parmReportedPattern : PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.8d, 0.9d, 0.95d, 1d],["Cumulative"]),]),
                parmActualClaims : HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.LAST_PAID,
                    [paidByDevelopmentPeriod :  new TableMultiDimensionalParameter([[320d, 62d],[1, 2]], ['Paid', 'Development Periods']),]),
                parmAppliedOnPerils : new ComboBoxTableMultiDimensionalParameter(
                        Collections.emptyList(), Arrays.asList("peril"), PerilMarker)
        )
        claimDevelopment.simulationScope = new SimulationScope(model: new VoidTestModel())
        claimDevelopment.periodScope = new PeriodScope()
        claimDevelopment.periodStore = new PeriodStore(claimDevelopment.periodScope)

        claimDevelopment.doCalculation()

        assertEquals 'period 0, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 0, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 0, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred(), 1E-8
        assertEquals 'period 0, paid 320', 310, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 0, paid 64', 45, claimDevelopment.outClaims[1].getPaid(), 1E-8
        assertEquals 'period 0, reported 800', 900, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 0, reported 160', 190, claimDevelopment.outClaims[1].getReported(), 1E-8
        assertEquals 'period 0, reserved 480', 0, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 0, reserved 96', 0, claimDevelopment.outClaims[1].getReserved(), 1E-8
        assertEquals 'period 0, ibnr 200', 100, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 0, ibnr 40', 10, claimDevelopment.outClaims[1].getIbnr(), 1E-8
        assertEquals 'period 0, change in reserves -320', 0, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
        assertEquals 'period 0, change in reserves -64', 0, claimDevelopment.outClaims[1].getChangeInReserves(), 1E-8

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 1, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 1, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 1, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred(), 1E-8
        assertEquals 'period 1, paid 320', 225, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 1, paid 64', 29, claimDevelopment.outClaims[1].getPaid(), 1E-8
        assertEquals 'period 1, reported 800', 950, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 1, reported 160', 200, claimDevelopment.outClaims[1].getReported(), 1E-8
        assertEquals 'period 1, reserved 480', 0, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 1, reserved 96', 0, claimDevelopment.outClaims[1].getReserved(), 1E-8
        assertEquals 'period 1, ibnr 200', 50, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 1, ibnr 40', 0, claimDevelopment.outClaims[1].getIbnr(), 1E-8
        assertEquals 'period 1, change in reserves -320', 0, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
        assertEquals 'period 1, change in reserves -64', 0, claimDevelopment.outClaims[1].getChangeInReserves(), 1E-8

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 2, #packets', 1, claimDevelopment.outClaims.size()
        assertEquals 'period 2, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 2, paid 320', 145, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 2, reported 800', 1000, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 2, reserved 480', 0, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 2, ibnr 200', 0, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 2, change in reserves -320', 0, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
    }

    void testHistoricLastReported() {
        ClaimDevelopment claimDevelopment = new ClaimDevelopment(
                parmPayoutPattern : PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.4d, 0.7d, 0.9d, 1d],["Cumulative"]),]),
                parmReportedPattern : PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.8d, 0.9d, 0.95d, 1d],["Cumulative"]),]),
                parmActualClaims : HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.LAST_REPORTED,
                    [reportedByDevelopmentPeriod :  new TableMultiDimensionalParameter([[800d, 180d],[1, 2]], ['Paid', 'Development Periods']),]),
                parmAppliedOnPerils : new ComboBoxTableMultiDimensionalParameter(
                        Collections.emptyList(), Arrays.asList("peril"), PerilMarker)
        )
        claimDevelopment.simulationScope = new SimulationScope(model: new VoidTestModel())
        claimDevelopment.periodScope = new PeriodScope()
        claimDevelopment.periodStore = new PeriodStore(claimDevelopment.periodScope)

        claimDevelopment.doCalculation()

        assertEquals 'period 0, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 0, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 0, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred(), 1E-8
        assertEquals 'period 0, paid 320', 310, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 0, paid 64', 45, claimDevelopment.outClaims[1].getPaid(), 1E-8
        assertEquals 'period 0, reported 800', 900, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 0, reported 160', 190, claimDevelopment.outClaims[1].getReported(), 1E-8
        assertEquals 'period 0, reserved 480', 0, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 0, reserved 96', 0, claimDevelopment.outClaims[1].getReserved(), 1E-8
        assertEquals 'period 0, ibnr 200', 100, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 0, ibnr 40', 10, claimDevelopment.outClaims[1].getIbnr(), 1E-8
        assertEquals 'period 0, change in reserves -320', 0, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
        assertEquals 'period 0, change in reserves -64', 0, claimDevelopment.outClaims[1].getChangeInReserves(), 1E-8

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 1, #packets', 2, claimDevelopment.outClaims.size()
        assertEquals 'period 1, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 1, incurred 200', 0, claimDevelopment.outClaims[1].getIncurred(), 1E-8
        assertEquals 'period 1, paid 320', 225, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 1, paid 64', 29, claimDevelopment.outClaims[1].getPaid(), 1E-8
        assertEquals 'period 1, reported 800', 950, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 1, reported 160', 200, claimDevelopment.outClaims[1].getReported(), 1E-8
        assertEquals 'period 1, reserved 480', 0, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 1, reserved 96', 0, claimDevelopment.outClaims[1].getReserved(), 1E-8
        assertEquals 'period 1, ibnr 200', 50, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 1, ibnr 40', 0, claimDevelopment.outClaims[1].getIbnr(), 1E-8
        assertEquals 'period 1, change in reserves -320', 0, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
        assertEquals 'period 1, change in reserves -64', 0, claimDevelopment.outClaims[1].getChangeInReserves(), 1E-8

        claimDevelopment.inClaims.clear()
        claimDevelopment.outClaims.clear()
        claimDevelopment.periodScope.prepareNextPeriod()
        claimDevelopment.doCalculation()

        assertEquals 'period 2, #packets', 1, claimDevelopment.outClaims.size()
        assertEquals 'period 2, incurred 1000', 0, claimDevelopment.outClaims[0].getIncurred(), 1E-8
        assertEquals 'period 2, paid 320', 145, claimDevelopment.outClaims[0].getPaid(), 1E-8
        assertEquals 'period 2, reported 800', 1000, claimDevelopment.outClaims[0].getReported(), 1E-8
        assertEquals 'period 2, reserved 480', 0, claimDevelopment.outClaims[0].getReserved(), 1E-8
        assertEquals 'period 2, ibnr 200', 0, claimDevelopment.outClaims[0].getIbnr(), 1E-8
        assertEquals 'period 2, change in reserves -320', 0, claimDevelopment.outClaims[0].getChangeInReserves(), 1E-8
    }
}