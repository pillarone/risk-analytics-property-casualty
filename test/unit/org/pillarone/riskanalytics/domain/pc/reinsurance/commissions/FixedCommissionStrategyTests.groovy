package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FixedCommissionStrategyTests extends GroovyTestCase {

    void testUsage() {
        ICommissionStrategy commissionStrategy =
        CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d])

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: -5)
        List underwritingInfos = [underwritingInfo200, underwritingInfo100]

        commissionStrategy.calculateCommission null, underwritingInfos, false, false

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -100 * 0.3, underwritingInfos[1].commission
    }

    /**
     * Additive: commission is added to prior commission. In a Reinsurance with Bouquet Commission, for example,
     * where all the commissions are wired in series and only one may be applicable to any given uwinfo packet,
     * each reinsurance contract should add its commission to the packet's prior commission.
     */
    void testUsageAdditive() {
        ICommissionStrategy commissionStrategy =
        CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d])

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: -5)
        List underwritingInfos = [underwritingInfo200, underwritingInfo100]

        commissionStrategy.calculateCommission null, underwritingInfos, false, true

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -50 - 200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -5 - 100 * 0.3, underwritingInfos[1].commission
    }
}