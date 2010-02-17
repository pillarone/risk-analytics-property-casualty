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
class CommissionStrategyTests extends GroovyTestCase {

    void testFixedCommission() {
        Commission commission = new Commission()
        commission.setParmCommissionStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commissionRatio':0.3d])

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        commission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        commission.doCalculation()

        assertEquals 'number of UnderwritingInfo packets ceded after contract', 2, contract.outFilteredUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets ceded after contract', 2, contract.outCoverUnderwritingInfo.size()
        assertEquals 'number of UnderwritingInfo packets net after contract', 2, contract.outNetAfterCoverUnderwritingInfo.size()

        // these values were not present in testUnderwritingInfoCededButNotNetWired (since Net was not wired, only Ceded)
        assertEquals 'underwritinginfo200 net premium written after contract', 150, contract.outNetAfterCoverUnderwritingInfo[0].premiumWritten
        assertEquals 'underwritinginfo200 net commission after contract', 5, contract.outNetAfterCoverUnderwritingInfo[0].commission
        assertEquals 'underwritinginfo100 net premium written after contract', 75, contract.outNetAfterCoverUnderwritingInfo[1].premiumWritten
        assertEquals 'underwritinginfo100 net commission after contract', 2.5, contract.outNetAfterCoverUnderwritingInfo[1].commission
    }
}