package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author shartmann (at) munichre (dot) com
 */
class NoCommissionStrategyTests extends GroovyTestCase {

    void testUsage() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 0)
        UnderwritingInfo underwritingInfo100plus5 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        UnderwritingInfo underwritingInfo200plus50 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        List underwritingInfos = [underwritingInfo100, underwritingInfo100plus5, underwritingInfo200plus50]

        commissionStrategy.calculateCommission null, underwritingInfos, false

        assertEquals '# outUnderwritingInfo packets', 3, underwritingInfos.size()
        assertEquals 'underwritingInfo100', 0, underwritingInfos[0].commission
        assertEquals 'underwritingInfo100plus5', 5, underwritingInfos[1].commission
        assertEquals 'underwritingInfo200plus50', 50, underwritingInfos[2].commission

    }
}