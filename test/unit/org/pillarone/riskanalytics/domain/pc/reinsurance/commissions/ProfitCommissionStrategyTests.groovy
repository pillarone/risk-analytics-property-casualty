package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class ProfitCommissionStrategyTests extends GroovyTestCase {

    void testUsage() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                    [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d])
        // lossCarriedForwardEnabled is true by default

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 0)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100', 0.03*(100*(1d-0.2)-50-20), underwritingInfo[0].commission
    }

    /**
     *  Test that the total written premium is correctly distributed/apportioned/split/partitioned across individual premiums
     */
    void testApportioning() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                    [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d])
        // lossCarriedForwardEnabled is true by default

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo20 = new UnderwritingInfo(premiumWritten: 20, commission: 0)
        UnderwritingInfo underwritingInfo30 = new UnderwritingInfo(premiumWritten: 30, commission: 0)
        UnderwritingInfo underwritingInfo50 = new UnderwritingInfo(premiumWritten: 50, commission: 0)
        List underwritingInfo = [underwritingInfo20, underwritingInfo30, underwritingInfo50]

        commissionStrategy.calculateCommission claims, underwritingInfo, true

        double totalPremiumWritten = 0.03*(100*(1d-0.2)-50-20)

        assertEquals '# outUnderwritingInfo packets', 3, underwritingInfo.size()
        assertEquals 'underwritingInfo20', 0.2*totalPremiumWritten, underwritingInfo[0].commission
        assertEquals 'underwritingInfo30', 0.3*totalPremiumWritten, underwritingInfo[1].commission
        assertEquals 'underwritingInfo50', 0.5*totalPremiumWritten, underwritingInfo[2].commission
    }

    void testAddition1() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                    [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d])
        // lossCarriedForwardEnabled is true by default

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo100plus1 = new UnderwritingInfo(premiumWritten: 100, commission: 1)
        List underwritingInfo = [underwritingInfo100plus1]

        commissionStrategy.calculateCommission claims, underwritingInfo, true

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100plus1', 1+0.03*(100*(1d-0.2)-50-20), underwritingInfo[0].commission
    }

    void testAddition2() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                    [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d])
        // lossCarriedForwardEnabled is true by default

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo200plus7 = new UnderwritingInfo(premiumWritten: 200, commission: 7)
        List underwritingInfo = [underwritingInfo200plus7]

        commissionStrategy.calculateCommission claims, underwritingInfo, true

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo200plus7', 7+0.03*(200*(1d-0.2)-50-20), underwritingInfo[0].commission
    }

    void testUnderflowProtectionBoundary() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                    [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d])

        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim claim2 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 10d)
        List<Claim> claims = [claim1, claim2]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 0)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true
        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100', 0.03*(100*(1d-0.2)-60-20), underwritingInfo[0].commission
    }

    void testUnderflowProtectionPastBoundary() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                    [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d])

        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim claim2 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 10d)
        Claim claim3 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 1d)
        List<Claim> claims = [claim1, claim2, claim3]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 0)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true
        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100', 0.03*(100*(1d-0.2)-60-20), underwritingInfo[0].commission
    }

    void testUsageWithClaimDevelopmentLeanPacket() {
        Map parameters = new HashMap<String, Double>(
            [profitCommissionRatio: 0.03d, costRatio: 0.2d, lossCarriedForwardEnabled: true, initialLossCarriedForward: 20d]);

        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION, parameters)

        ClaimDevelopmentLeanPacket claim1 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 30, paid: 5, reserved: 25)
        ClaimDevelopmentLeanPacket claim2 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 10, paid: 3, reserved: 7)
        ClaimDevelopmentLeanPacket claim3 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 10, paid: 2, reserved: 8)
        List<ClaimDevelopmentLeanPacket> claims = [claim1, claim2, claim3]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 0)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100', 0.03*(100*(1d-0.2)-50-20), underwritingInfo[0].commission
    }

}