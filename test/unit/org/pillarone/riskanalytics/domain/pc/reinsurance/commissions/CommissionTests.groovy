package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable.ApplicableStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints

/**
 * @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class CommissionTests extends GroovyTestCase {

    /**
     * Creates & returns a SimulationScope whose model is a VoidTestModel
     * and whose IterationScope's PeriodScope's PeriodCounter
     * starts at a given year(, month & day) and lasts a given number of years.
     * By default the period starts on 1.1.2000 (or 1.1 of a given year) and lasts 1 year.
     */
    SimulationScope getTestSimulationScope(int year = 2000, int month = 1, int day = 1, int years = 1) {
        PeriodScope periodScope = new PeriodScope()
        periodScope.periodCounter = new ContinuousPeriodCounter(new DateTime(year,month,day,0,0,0,0), Period.years(years))
        IterationScope iterationScope = new IterationScope()
        iterationScope.setPeriodScope periodScope
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.setIterationScope iterationScope
        simulationScope.model = new VoidTestModel()
        simulationScope
    }

    void testDefaultStrategy() {
        Commission commission = new Commission()
        // default is no commission (fixed 0%, NONE applicable)

        SimulationScope simulationScope = getTestSimulationScope(2010)
        commission.setSimulationScope simulationScope

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        commission.doCalculation()

        assertEquals '# outUnderwritingInfo packets', 2, commission.outUnderwritingInfo.size()
        assertEquals 'underwritingInfo200', 50, commission.outUnderwritingInfo[0].commission
        assertEquals 'underwritingInfo200', 5, commission.outUnderwritingInfo[1].commission
    }

    void testFixedCommission() {
        Commission commission = new Commission(
            parmCommissionStrategy :
                CommissionStrategyType.getStrategy(
                CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d]),
            parmApplicableStrategy :
                ApplicableStrategyType.getStrategy(
                ApplicableStrategyType.ALL, [:])
        )

        commission.setSimulationScope getTestSimulationScope(2010)

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        commission.doCalculation()

        assertEquals '# outUnderwritingInfo packets', 2, commission.outUnderwritingInfo.size()
        assertEquals 'underwritingInfo200', 50+200*0.3, commission.outUnderwritingInfo[0].commission
        assertEquals 'underwritingInfo200', 5+100*0.3, commission.outUnderwritingInfo[1].commission
    }

    void testSlidingCommission() {

        Claim claim01 = new Claim(value: 1d);
        Claim claim05 = new Claim(value: 5d);
        Claim claim20 = new Claim(value: 20d);
        Commission commission = getEmptySlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, 0d)
        commission.inClaims << claim01
        commission.doCalculation()
        assertEquals 'totalPremiumWritten', 50, commission.outUnderwritingInfo[0].premiumWritten
        assertEquals 'underwritingInfo050 (1)', 50*0.2, commission.outUnderwritingInfo[0].commission, 1E-10

        commission = getEmptySlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, 10d)
        commission.inClaims << claim01
        commission.doCalculation()
        assertEquals 'underwritingInfo100', 10+50*0.2, commission.outUnderwritingInfo[0].commission, 1E-10

        commission = getEmptySlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, 0d)
        commission.inClaims << claim05
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (2)', 50*0.1, commission.outUnderwritingInfo[0].commission, 1E-10

        commission = getEmptySlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, 0d)
        commission.inClaims << claim20
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (3)', 50*0.05, commission.outUnderwritingInfo[0].commission, 1E-10

        commission = getEmptySlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, 0d)
        commission.inClaims << claim01 << claim05 << claim20
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (4)', 0.0, commission.outUnderwritingInfo[0].commission, 1E-10

        commission = getEmptySlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, 0d) << getUnderwritingInfo(60d, 0d)
        commission.inClaims << claim05 << claim20
        commission.doCalculation()
        assertEquals 'underwritingInfo050 uw1 (5)', 50*0.05, commission.outUnderwritingInfo[0].commission, 1E-10
        assertEquals 'underwritingInfo050 uw2 (5)', 60*0.05, commission.outUnderwritingInfo[1].commission, 1E-10

        commission = getEmptySlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, 0d)
        commission.inClaims
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (6)', 50*0.2, commission.outUnderwritingInfo[0].commission, 1E-10
    }

    private Commission getEmptySlidingCommission() {
        Commission commission = new Commission(
            parmCommissionStrategy :
                CommissionStrategyType.getStrategy(
                CommissionStrategyType.SLIDINGCOMMISSION,
                    ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [[0.0d, 0.1d, 0.2d, 0.5d], [0.2d, 0.10d, 0.05d, 0d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))]),
            parmApplicableStrategy :
                ApplicableStrategyType.getStrategy(
                ApplicableStrategyType.ALL, [:])
        )
        commission.setSimulationScope getTestSimulationScope(2010)
        return commission
    }

    private UnderwritingInfo getUnderwritingInfo(double aPremiumWritten, double aPriorCommission) {
        new UnderwritingInfo(premiumWritten: aPremiumWritten, commission: aPriorCommission)
    }

    void testProfitCommission() {
        Commission commission = new Commission(
                parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(
                    CommissionStrategyType.PROFITCOMMISSION,
                        [profitCommissionRatio: 0.03d, costRatio: 0.2d,
                            lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d]),
                parmApplicableStrategy :
                    ApplicableStrategyType.getStrategy(
                    ApplicableStrategyType.ALL, [:])
        )

        commission.setSimulationScope getTestSimulationScope(2010)

        commission.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)

        commission.inUnderwritingInfo << new UnderwritingInfo(premiumWritten: 100, commission: 0)

        commission.doCalculation()

        assertEquals '# outUnderwritingInfo packets', 1, commission.outUnderwritingInfo.size()
        assertEquals 'underwritingInfo100', 0.03*(100*(1d-0.2)-50-20), commission.outUnderwritingInfo[0].commission
    }

    void testFixedCommissionFilteringByContract() {
        ReinsuranceContract contract1 = new ReinsuranceContract(name: 'selected contract')
        ReinsuranceContract contract2 = new ReinsuranceContract(name: 'unused contract')

        SimulationScope simulationScope = getTestSimulationScope(2010)
        simulationScope.model.allComponents << contract1 << contract2

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        underwritingInfo100.setOrigin contract1
        underwritingInfo200.setOrigin contract2

        Commission commission = new Commission(
            parmCommissionStrategy : CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d]),
            parmApplicableStrategy : ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT, [applicableContracts:
                new ComboBoxTableMultiDimensionalParameter(['selected contract'], ['Applicable Contracts'], IReinsuranceContractMarker)]),
            simulationScope : simulationScope
        )
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100
        commission.doCalculation()

        assertEquals '# outUnderwritingInfo packets', 2, commission.outUnderwritingInfo.size()
        assertEquals 'underwritingInfo200', 50, commission.outUnderwritingInfo[1].commission
        assertEquals 'underwritingInfo100', 5+100*0.3, commission.outUnderwritingInfo[0].commission
    }

    void testProfitCommissionFilteringByContract() {
        ReinsuranceContract contract0 = new ReinsuranceContract(name: 'unused contract')
        ReinsuranceContract contract1 = new ReinsuranceContract(name: 'selected contract 1')
        ReinsuranceContract contract2 = new ReinsuranceContract(name: 'selected contract 2')
        List<String> contracts = [contract1.name, contract2.name]

        SimulationScope simulationScope = getTestSimulationScope(2010)
        simulationScope.model.allComponents << contract0 << contract1 << contract2

        Commission commission = new Commission(
            parmCommissionStrategy : CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d]),
            parmApplicableStrategy : ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT, [applicableContracts:
                new ComboBoxTableMultiDimensionalParameter(contracts, ['Applicable Contracts'], IReinsuranceContractMarker)]),
            simulationScope : simulationScope
        )
        // contracts 1 & 2 are covered (an extra 30% commission applies to them) but contract 0 is not (no extra commission)

        UnderwritingInfo underwritingInfo1 = new UnderwritingInfo(origin: contract1, premiumWritten: 60, commission: 1)
        UnderwritingInfo underwritingInfo2 = new UnderwritingInfo(origin: contract2, premiumWritten: 40, commission: 2)
        UnderwritingInfo underwritingInfo3 = new UnderwritingInfo(origin: contract0, premiumWritten: 20, commission: 4)
        commission.inUnderwritingInfo << underwritingInfo1 << underwritingInfo2 << underwritingInfo3
        // An extra 30% commission applies to UWInfo 100 & 200 but not 300

        commission.inClaims << new Claim(reinsuranceContract: contract0, claimType: ClaimType.ATTRITIONAL, ultimate: 10)
        commission.inClaims << new Claim(reinsuranceContract: contract1, claimType: ClaimType.ATTRITIONAL, ultimate: 20)
        commission.inClaims << new Claim(reinsuranceContract: contract2, claimType: ClaimType.ATTRITIONAL, ultimate: 30)
        // the claims of size 20 & 30 belong to "applicable" contracts and therefore affect the profit commission

        commission.doCalculation()

        assertEquals '# outUnderwritingInfo packets', 3, commission.outUnderwritingInfo.size()
        assertEquals 'underwritingInfo1', 1+0.03*(100*(1d-0.2)-50-20)*0.6, commission.outUnderwritingInfo[0].commission
        assertEquals 'underwritingInfo2', 2+0.03*(100*(1d-0.2)-50-20)*0.4, commission.outUnderwritingInfo[1].commission
        assertEquals 'underwritingInfo3', 4, commission.outUnderwritingInfo[2].commission
    }

    void testProfitCommissionFilteringByContract2() {
        ReinsuranceContract contract0 = new ReinsuranceContract(name: 'unused contract')
        ReinsuranceContract contract1 = new ReinsuranceContract(name: 'selected contract 1')
        ReinsuranceContract contract2 = new ReinsuranceContract(name: 'selected contract 2')
        List<String> contracts = [contract1.name, contract2.name]

        SimulationScope simulationScope = getTestSimulationScope(2010)
        simulationScope.model.allComponents << contract0 << contract1 << contract2

        Commission commission = new Commission(
            parmCommissionStrategy : CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                [profitCommissionRatio: 0.3d, costRatio: 0.1d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 5d]),
            parmApplicableStrategy : ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT, [applicableContracts:
                new ComboBoxTableMultiDimensionalParameter(contracts, ['Applicable Contracts'], IReinsuranceContractMarker)]),
            simulationScope : simulationScope
        )
        // contracts 1 & 2 are covered (an extra 30% commission applies to them) but contract 0 is not (no extra commission)

        UnderwritingInfo underwritingInfo1 = new UnderwritingInfo(origin: contract1, premiumWritten: 10, commission: 1)
        UnderwritingInfo underwritingInfo2 = new UnderwritingInfo(origin: contract1, premiumWritten: 20, commission: 2)
        UnderwritingInfo underwritingInfo3 = new UnderwritingInfo(origin: contract1, premiumWritten: 30, commission: 3)
        UnderwritingInfo underwritingInfo4 = new UnderwritingInfo(origin: contract2, premiumWritten: 40, commission: 4)
        UnderwritingInfo underwritingInfo5 = new UnderwritingInfo(origin: contract2, premiumWritten: 50, commission: 5)
        UnderwritingInfo underwritingInfo6 = new UnderwritingInfo(origin: contract0, premiumWritten: 60, commission: 6)
        commission.inUnderwritingInfo << underwritingInfo1 << underwritingInfo2 << underwritingInfo3
        commission.inUnderwritingInfo << underwritingInfo4 << underwritingInfo5 << underwritingInfo6
        // An extra 30% commission applies to UWInfo 1-5 but not UWInfo 6

        commission.inClaims << new Claim(reinsuranceContract: contract1, claimType: ClaimType.ATTRITIONAL, ultimate: 10)
        commission.inClaims << new Claim(reinsuranceContract: contract2, claimType: ClaimType.ATTRITIONAL, ultimate: 10)
        commission.inClaims << new Claim(reinsuranceContract: contract2, claimType: ClaimType.ATTRITIONAL, ultimate: 10)
        commission.inClaims << new Claim(reinsuranceContract: contract0, claimType: ClaimType.ATTRITIONAL, ultimate: 40)
        commission.inClaims << new Claim(reinsuranceContract: contract0, claimType: ClaimType.ATTRITIONAL, ultimate: 50)
        // the claims of size 10 (totalling 30) belong to "applicable" contracts and therefore affect the profit commission

        commission.doCalculation()

        // note: for UWInfo 1, 10/(10+20+30+40+50) * 0.3 = 1/15 * 0.3 = 0.1 * 0.2; similarly for UWInfo 2-5.
        assertEquals '# outUnderwritingInfo packets', 6, commission.outUnderwritingInfo.size()
        assertEquals 'underwritingInfo1', 1+0.1*0.2*(150*(1d-0.1)-30-5), commission.outUnderwritingInfo[0].commission
        assertEquals 'underwritingInfo2', 2+0.2*0.2*(150*(1d-0.1)-30-5), commission.outUnderwritingInfo[1].commission
        assertEquals 'underwritingInfo3', 3+0.3*0.2*(150*(1d-0.1)-30-5), commission.outUnderwritingInfo[2].commission
        assertEquals 'underwritingInfo4', 4+0.4*0.2*(150*(1d-0.1)-30-5), commission.outUnderwritingInfo[3].commission
        assertEquals 'underwritingInfo5', 5+0.5*0.2*(150*(1d-0.1)-30-5), commission.outUnderwritingInfo[4].commission
        assertEquals 'underwritingInfo6', 6, commission.outUnderwritingInfo[5].commission
    }
}