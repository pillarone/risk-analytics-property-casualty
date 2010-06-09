package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
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
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class CommissionTests extends GroovyTestCase {

    /**
     *  Creates a SimulationScope whose model is a VoidTestModel, and whose IterationScope's PeriodScope's
     *  PeriodCounter starts at a given year(, month & day) and lasts a given number of years.
     *  By default the period starts on 1.1.2000 (or 1.1 of a given year) and lasts 1 year.
     */
    static SimulationScope getTestSimulationScope(int year = 2000, int month = 1, int day = 1, int years = 1) {
        new SimulationScope(
            model: new VoidTestModel(),
            iterationScope: new IterationScope(
                periodScope: new PeriodScope(
                    periodCounter: new ContinuousPeriodCounter(
                        new DateTime(year,month,day,0,0,0,0), Period.years(years)
                    )
                )
            )
        )
    }

    static Commission getSlidingCommission(Map<Double, Double> bands = [:]) {
        Commission commission = new Commission(
            parmCommissionStrategy :
                SlidingCommissionStrategyTests.getSlidingCommissionStrategy(bands),
            parmApplicableStrategy :
                ApplicableStrategyType.getStrategy(
                ApplicableStrategyType.ALL, [:])
        )
        commission.setSimulationScope getTestSimulationScope(2010)
        return commission
    }

    static UnderwritingInfo getUnderwritingInfo(double premiumWritten, double priorCommission=0) {
        UnderwritingInfo parent = new UnderwritingInfo(premiumWritten: premiumWritten, commission: priorCommission)
        UnderwritingInfo child = UnderwritingInfoPacketFactory.copy(parent)
        child.originalUnderwritingInfo = parent
        return child
    }

    static UnderwritingInfo getUnderwritingInfoFromOrigin(double premiumWritten, double priorCommission=0, Component origin=null) {
        UnderwritingInfo parent = new UnderwritingInfo(premiumWritten: premiumWritten, commission: priorCommission,
                                                       origin: origin ? origin : new TestComponent())
        UnderwritingInfo child = UnderwritingInfoPacketFactory.copy(parent)
        child.originalUnderwritingInfo = parent
        return child
    }

    static UnderwritingInfo getUnderwritingInfoFromContract(double premiumWritten, double priorCommission=0, IReinsuranceContractMarker contract) {
        UnderwritingInfo parent = new UnderwritingInfo(premiumWritten: premiumWritten, commission: priorCommission,
                                                       reinsuranceContract: contract)
        UnderwritingInfo child = UnderwritingInfoPacketFactory.copy(parent)
        child.originalUnderwritingInfo = parent
        return child
    }

    static UnderwritingInfo getUnderwritingInfoFromSelf(Map parameters) {
        UnderwritingInfo self = new UnderwritingInfo()
        for (Map.Entry entry : parameters.entrySet()) {
            self.putAt((String) entry.getKey(), entry.getValue())
        }
        self.originalUnderwritingInfo = self
        return self
    }

    void testDefaultStrategy() {
        Commission commission = new Commission()
        // default is no commission

        SimulationScope simulationScope = getTestSimulationScope(2010)
        commission.setSimulationScope simulationScope

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        commission.doCalculation()

        assertEquals '# outUnderwritingInfoUnmodified packets', 2, commission.outUnderwritingInfoUnmodified.size()
        assertEquals '# outUnderwritingInfoModified packets', 0, commission.outUnderwritingInfoModified.size()
        assertEquals 'underwritingInfo200', 50, commission.outUnderwritingInfoUnmodified[0].commission
        assertEquals 'underwritingInfo200', 5, commission.outUnderwritingInfoUnmodified[1].commission
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

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: -5)
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        commission.doCalculation()

        assertEquals '# outUnderwritingInfoUnmodified packets', 0, commission.outUnderwritingInfoUnmodified.size()
        assertEquals '# outUnderwritingInfoModified packets', 2, commission.outUnderwritingInfoModified.size()
        assertEquals 'underwritingInfo200', -(50 + 200 * 0.3), commission.outUnderwritingInfoModified[0].commission
        assertEquals 'underwritingInfo200', -(5 + 100 * 0.3), commission.outUnderwritingInfoModified[1].commission
    }

    void testSlidingCommission() {

        Claim claim01 = new Claim(value: 1d);
        Claim claim05 = new Claim(value: 5d);
        Claim claim20 = new Claim(value: 20d);
        Commission commission = getSlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, -0d)
        commission.inClaims << claim01
        commission.doCalculation()
        assertEquals 'totalPremiumWritten', 50, commission.outUnderwritingInfoModified[0].premiumWritten
        assertEquals 'underwritingInfo050 (1)', -50 * 0.2, commission.outUnderwritingInfoModified[0].commission, 1E-10

        commission = getSlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, -10d)
        commission.inClaims << claim01
        commission.doCalculation()
        assertEquals 'underwritingInfo100', -(10 + 50 * 0.2), commission.outUnderwritingInfoModified[0].commission, 1E-10

        commission = getSlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, -0d)
        commission.inClaims << claim05
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (2)', -50 * 0.1, commission.outUnderwritingInfoModified[0].commission, 1E-10

        commission = getSlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, -0d)
        commission.inClaims << claim20
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (3)', -50 * 0.05, commission.outUnderwritingInfoModified[0].commission, 1E-10

        commission = getSlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, -0d)
        commission.inClaims << claim01 << claim05 << claim20
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (4)', -0.0, commission.outUnderwritingInfoModified[0].commission, 1E-10

        commission = getSlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, -0d) << getUnderwritingInfo(60d, -0d)
        commission.inClaims << claim05 << claim20
        commission.doCalculation()
        assertEquals 'underwritingInfo050 uw1 (5)', -50 * 0.05, commission.outUnderwritingInfoModified[0].commission, 1E-10
        assertEquals 'underwritingInfo050 uw2 (5)', -60 * 0.05, commission.outUnderwritingInfoModified[1].commission, 1E-10

        commission = getSlidingCommission()
        commission.inUnderwritingInfo << getUnderwritingInfo(50d, -0d)
        commission.inClaims
        commission.doCalculation()
        assertEquals 'underwritingInfo050 (6)', -50 * 0.2, commission.outUnderwritingInfoModified[0].commission, 1E-10
    }

    void testProfitCommission() {
        Commission commission = new Commission(
                parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(
                    CommissionStrategyType.PROFITCOMMISSION,
                        [commissionRatio: 0d, profitCommissionRatio: 0.03d, costRatio: 0.2d,
                            lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d]),
                parmApplicableStrategy :
                    ApplicableStrategyType.getStrategy(
                    ApplicableStrategyType.ALL, [:])
        )

        commission.setSimulationScope getTestSimulationScope(2010)

        commission.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)

        commission.inUnderwritingInfo << new UnderwritingInfo(premiumWritten: 100, commission: -0)

        commission.doCalculation()

        assertEquals '# outUnderwritingInfoModified packets', 1, commission.outUnderwritingInfoModified.size()
        assertEquals 'underwritingInfo100 commission', -0.3, commission.outUnderwritingInfoModified[0].commission
    }

    void testFixedCommissionFilteringByContract() {
        ReinsuranceContract contract1 = new ReinsuranceContract(name: 'selected contract')
        ReinsuranceContract contract2 = new ReinsuranceContract(name: 'unused contract')

        SimulationScope simulationScope = getTestSimulationScope(2010)
        simulationScope.model.allComponents << contract1 << contract2

        UnderwritingInfo underwritingInfo100 = getUnderwritingInfoFromContract(100, -5, contract1)
        UnderwritingInfo underwritingInfo200 = getUnderwritingInfoFromContract(200, -50, contract2)

        ComboBoxTableMultiDimensionalParameter applicableContracts = new ComboBoxTableMultiDimensionalParameter(
                ['selected contract'], ['Applicable Contracts'], IReinsuranceContractMarker)
        applicableContracts.setSimulationModel simulationScope.model

        Commission commission = new Commission(
                parmCommissionStrategy: CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d]),
                parmApplicableStrategy: ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT,
                [applicableContracts: applicableContracts]),
                simulationScope: simulationScope
        )
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100
        commission.doCalculation()

        assertEquals '# outUnderwritingInfoModified packets', 1, commission.outUnderwritingInfoModified.size()
        assertEquals 'underwritingInfo100', -(5 + 100 * 0.3), commission.outUnderwritingInfoModified[0].commission
    }

    void testProfitCommissionFilteringByContract() {
        ReinsuranceContract contract0 = new ReinsuranceContract(name: 'unused contract')
        ReinsuranceContract contract1 = new ReinsuranceContract(name: 'selected contract 1')
        ReinsuranceContract contract2 = new ReinsuranceContract(name: 'selected contract 2')
        List<String> contracts = [contract1.name, contract2.name]

        SimulationScope simulationScope = getTestSimulationScope(2010)
        simulationScope.model.allComponents << contract0 << contract1 << contract2

        ComboBoxTableMultiDimensionalParameter applicableContracts = new ComboBoxTableMultiDimensionalParameter(
                contracts, ['Applicable Contracts'], IReinsuranceContractMarker)
        applicableContracts.setSimulationModel simulationScope.model

        Commission commission = new Commission(
            parmCommissionStrategy : CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                [commissionRatio: 0d, profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true,
                        initialLossCarriedForward: 20d]),
            parmApplicableStrategy : ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT,
                    [applicableContracts: applicableContracts]),
            simulationScope : simulationScope
        )
        // contracts 1 & 2 are covered (an extra 30% commission applies to them) but contract 0 is not (no extra commission)

        UnderwritingInfo underwritingInfo1 = getUnderwritingInfoFromContract(60, -1, contract1)
        UnderwritingInfo underwritingInfo2 = getUnderwritingInfoFromContract(40, -2, contract2)
        UnderwritingInfo underwritingInfo3 = getUnderwritingInfoFromContract(20, -4, contract0)
        commission.inUnderwritingInfo << underwritingInfo1 << underwritingInfo2 << underwritingInfo3
        // An extra 30% commission applies to UWInfo 100 & 200 but not 300

        commission.inClaims << new Claim(reinsuranceContract: contract0, claimType: ClaimType.ATTRITIONAL, ultimate: 10)
        commission.inClaims << new Claim(reinsuranceContract: contract1, claimType: ClaimType.ATTRITIONAL, ultimate: 20)
        commission.inClaims << new Claim(reinsuranceContract: contract2, claimType: ClaimType.ATTRITIONAL, ultimate: 30)
        // the claims of size 20 & 30 belong to "applicable" contracts and therefore affect the profit commission

        commission.doCalculation()

        assertEquals '# outUnderwritingInfoModified packets', 2, commission.outUnderwritingInfoModified.size()
        assertEquals 'underwritingInfo1', -1.18, commission.outUnderwritingInfoModified[0].commission
        assertEquals 'underwritingInfo2', -2.12, commission.outUnderwritingInfoModified[1].commission
    }

    void testProfitCommissionFilteringByContract2() {
        ReinsuranceContract contract0 = new ReinsuranceContract(name: 'unused contract')
        ReinsuranceContract contract1 = new ReinsuranceContract(name: 'selected contract 1')
        ReinsuranceContract contract2 = new ReinsuranceContract(name: 'selected contract 2')
        List<String> contracts = [contract1.name, contract2.name]

        SimulationScope simulationScope = getTestSimulationScope(2010)
        simulationScope.model.allComponents << contract0 << contract1 << contract2

        ComboBoxTableMultiDimensionalParameter applicableContracts = new ComboBoxTableMultiDimensionalParameter(
                contracts, ['Applicable Contracts'], IReinsuranceContractMarker)
        applicableContracts.setSimulationModel simulationScope.model

        Commission commission = new Commission(
            parmCommissionStrategy : CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                [commissionRatio: 0d, profitCommissionRatio: 0.3d, costRatio: 0.1d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 5d]),
            parmApplicableStrategy : ApplicableStrategyType.getStrategy(ApplicableStrategyType.CONTRACT,
                    [applicableContracts: applicableContracts]),
            simulationScope : simulationScope
        )
        // contracts 1 & 2 are covered (an extra 30% commission applies to them) but contract 0 is not (no extra commission)

        UnderwritingInfo underwritingInfo1 = getUnderwritingInfoFromContract(10, -1, contract1)
        UnderwritingInfo underwritingInfo2 = getUnderwritingInfoFromContract(20, -2, contract1)
        UnderwritingInfo underwritingInfo3 = getUnderwritingInfoFromContract(30, -3, contract1)
        UnderwritingInfo underwritingInfo4 = getUnderwritingInfoFromContract(40, -4, contract2)
        UnderwritingInfo underwritingInfo5 = getUnderwritingInfoFromContract(50, -5, contract2)
        UnderwritingInfo underwritingInfo6 = getUnderwritingInfoFromContract(60, -6, contract0)
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
        assertEquals '# outUnderwritingInfoModified packets', 5, commission.outUnderwritingInfoModified.size()
        assertEquals 'underwritingInfo1', -3, commission.outUnderwritingInfoModified[0].commission
        assertEquals 'underwritingInfo2', -6, commission.outUnderwritingInfoModified[1].commission
        assertEquals 'underwritingInfo3', -9, commission.outUnderwritingInfoModified[2].commission
        assertEquals 'underwritingInfo4', -12, commission.outUnderwritingInfoModified[3].commission
        assertEquals 'underwritingInfo5', -15, commission.outUnderwritingInfoModified[4].commission
    }
}