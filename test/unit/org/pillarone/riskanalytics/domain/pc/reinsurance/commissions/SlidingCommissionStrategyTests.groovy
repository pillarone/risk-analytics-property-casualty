package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SlidingCommissionStrategyTests extends GroovyTestCase {

    void testUsage() {
        ICommissionStrategy commissionStrategy =
                CommissionStrategyType.getStrategy(CommissionStrategyType.SLIDINGCOMMISSION,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [[0.0d, 0.1d, 0.2d, 0.5d], [0.2d, 0.10d, 0.05d, 0d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
                ])

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        Claim claim05 = new Claim(value: 5)
        Claim claim15 = new Claim(value: 15)
        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim05, claim15]

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200*0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo100', 100*0.2, underwritingInfos[1].commission
    }

    void testUsage2() {
        ICommissionStrategy commissionStrategy =
                CommissionStrategyType.getStrategy(CommissionStrategyType.SLIDINGCOMMISSION,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [[0.0d, 0.1d, 0.2d, 0.5d], [0.2d, 0.10d, 0.05d, 0d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
                ])

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        Claim claim15 = new Claim(value: 25)
        List underwritingInfos = [underwritingInfo200]
        List claims = [claim15]

        commissionStrategy.calculateCommission claims, underwritingInfos, false, true

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 50+200*0.1, underwritingInfos[0].commission
    }
}