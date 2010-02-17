package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.util.TestProbe

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FixedCommissionStrategyTests extends GroovyTestCase {
    
    void testUsage() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d])

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        List underwritingInfos = [underwritingInfo200, underwritingInfo100]

        commissionStrategy.calculateCommission null, underwritingInfos

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 50+200*0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 5+100*0.3, underwritingInfos[1].commission

    }
}