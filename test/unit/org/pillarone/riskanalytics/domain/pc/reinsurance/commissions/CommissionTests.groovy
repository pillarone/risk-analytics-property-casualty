package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author shartmann (at) munichre (dot) com
 */
class CommissionTests extends GroovyTestCase{
    
    void testFixedCommission() {
        Commission commission = new Commission(parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d]))

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        commission.doCalculation()

        assertEquals '# outUnderwritingInfo packets', 2, commission.outUnderwritingInfo.size()
        assertEquals 'underwritingInfo200', 50+200*0.3, commission.outUnderwritingInfo[0].commission
        assertEquals 'underwritingInfo200', 5+100*0.3, commission.outUnderwritingInfo[1].commission
    }
}