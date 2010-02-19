package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints

/**
 * @author shartmann (at) munichre (dot) com
 */
class CommissionTests extends GroovyTestCase {
    
    void testFixedCommission() {
        Commission commission = new Commission(parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d]))

        PeriodScope periodScope = new PeriodScope()
        periodScope.periodCounter = new ContinuousPeriodCounter(new DateTime(2010,1,1,0,0,0,0), Period.years(1))
        commission.setPeriodScope(periodScope)

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
            Commission commission = new Commission(parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(CommissionStrategyType.SLIDINGCOMMISSION,
                    ['commissionBands': new ConstrainedMultiDimensionalParameter(
                            [[0.0d, 0.1d, 0.2d, 0.5d], [0.2d, 0.10d, 0.05d, 0d]],
                            [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
                    ]))
            PeriodScope periodScope = new PeriodScope()
            periodScope.periodCounter = new ContinuousPeriodCounter(new DateTime(2010,1,1,0,0,0,0), Period.years(1))
            commission.setPeriodScope(periodScope)
            return commission
        }

        private UnderwritingInfo getUnderwritingInfo(double aPremiumWritten, double aPriorCommission) {
            new UnderwritingInfo(premiumWritten: aPremiumWritten, commission: aPriorCommission)
        }
    
        //todo(bgi): delete this test case since shartmann will implement it
        void testProfitCommission() {
            Commission commission = new Commission(parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                            [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d]))

            PeriodScope periodScope = new PeriodScope()
            periodScope.periodCounter = new ContinuousPeriodCounter(new DateTime(2010,1,1,0,0,0,0), Period.years(1))
            commission.setPeriodScope(periodScope)

            commission.inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)

            commission.inUnderwritingInfo << new UnderwritingInfo(premiumWritten: 100, commission: 0)

            commission.doCalculation()

            assertEquals '# outUnderwritingInfo packets', 1, commission.outUnderwritingInfo.size()
            assertEquals 'underwritingInfo100', 0.03*(100*(1d-0.2)-50-20), commission.outUnderwritingInfo[0].commission
        }
}