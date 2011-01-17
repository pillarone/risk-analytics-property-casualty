package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class ProfitCommissionStrategyTests extends GroovyTestCase {

    static ICommissionStrategy getProfitCommission(double profitCommissionRatio = 0.03d,
                                                   double costRatio = 0.2d,
                                                   boolean lossCarriedForwardEnabled = true, // this is the default value
                                                   double initialLossCarriedForward = 20d,
                                                   double commissionRatio = 0d /* "prior" fixed commission; default=0 */) {
        CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION, [
                profitCommissionRatio: profitCommissionRatio,
                costRatio: costRatio,
                lossCarriedForwardEnabled: lossCarriedForwardEnabled,
                initialLossCarriedForward: initialLossCarriedForward,
                commissionRatio: commissionRatio,
        ])
    }

    void testUsage() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 0)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, false

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100 commission', -0.3, underwritingInfo[0].commission
        assertEquals 'underwritingInfo100 variable commission', -0.3, underwritingInfo[0].variableCommission
        assertEquals 'underwritingInfo100 fixed commission', -0d, underwritingInfo[0].fixedCommission

    }

    /**
     *  Test that the total written premium is correctly distributed/apportioned/split/partitioned across individual premiums
     */
    void testApportioning() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo20 = new UnderwritingInfo(premiumWritten: 20, commission: -1, fixedCommission: -0.2, variableCommission: -0.8)
        UnderwritingInfo underwritingInfo30 = new UnderwritingInfo(premiumWritten: 30, commission: -1, fixedCommission: -0.1, variableCommission: -0.9)
        UnderwritingInfo underwritingInfo50 = new UnderwritingInfo(premiumWritten: 50, commission: -1, fixedCommission: -0.3, variableCommission: -0.7)
        List underwritingInfo = [underwritingInfo20, underwritingInfo30, underwritingInfo50]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, true // test that prior commission is added

        assertEquals '# outUnderwritingInfo packets', 3, underwritingInfo.size()
        assertEquals 'underwritingInfo20 commission', -1.06, underwritingInfo[0].commission
        assertEquals 'underwritingInfo20 variable commission', -0.8 - 0.06, underwritingInfo[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo20 fixed commission', -0.2, underwritingInfo[0].fixedCommission
        assertEquals 'underwritingInfo30 commission', -1.09, underwritingInfo[1].commission
        assertEquals 'underwritingInfo30 variable commission', -0.9 - 0.09, underwritingInfo[1].variableCommission, 1E-14
        assertEquals 'underwritingInfo30 fixed commission', -0.1, underwritingInfo[1].fixedCommission
        assertEquals 'underwritingInfo50 commission', -1.15, underwritingInfo[2].commission
        assertEquals 'underwritingInfo50 variable commission', -0.7 - 0.15, underwritingInfo[2].variableCommission, 1E-14
        assertEquals 'underwritingInfo50 fixed commission', -0.3, underwritingInfo[2].fixedCommission
    }

    void testAddition1() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: -1)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, false

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100plus1 commission', -0.3, underwritingInfo[0].commission
        assertEquals 'underwritingInfo100plus1 variable commission', -0.3, underwritingInfo[0].variableCommission
        assertEquals 'underwritingInfo100plus1 fixed commission', -0d, underwritingInfo[0].fixedCommission
    }

    void testAddition2() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 7)
        List underwritingInfo = [underwritingInfo200]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, false

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo200plus7 commission', -2.7, underwritingInfo[0].commission, 1E-12
    }

    void testProfitCommissionWithPriorFixedCommission() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0.02d)

        Claim claim = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        List<Claim> claims = [claim]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: -1, variableCommission: -0.9, fixedCommission: -0.1)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, true

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100 commission', -1 - 2.24, underwritingInfo[0].commission, 1E-14
        assertEquals 'underwritingInfo100 variable commission', -0.9 - 0.24, underwritingInfo[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100 fixed commission', -0.1 - 2d, underwritingInfo[0].fixedCommission, 1E-14
    }

    void testUnderflowProtectionBoundary() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim claim2 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 10d)
        List<Claim> claims = [claim1, claim2]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: -5)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, false
        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100 commission', -0, underwritingInfo[0].commission, 1E-10
    }

    void testUnderflowProtectionPastBoundary() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        Claim claim1 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim claim2 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 10d)
        Claim claim3 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 1d)
        List<Claim> claims = [claim1, claim2, claim3]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: -1)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, false
        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100', 0, underwritingInfo[0].commission, 1E-10
    }

    void testUsageWithClaimDevelopmentLeanPacket() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimDevelopmentLeanPacket claim1 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 30, paid: 5, reserved: 25)
        ClaimDevelopmentLeanPacket claim2 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 10, paid: 3, reserved: 7)
        ClaimDevelopmentLeanPacket claim3 = new ClaimDevelopmentLeanPacket(claimType: ClaimType.ATTRITIONAL, incurred: 10, paid: 2, reserved: 8)
        List claims = [claim1, claim2, claim3]

        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 0)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.calculateCommission claims, underwritingInfo, true, false

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100', -0.3, underwritingInfo[0].commission
        assertEquals 'underwritingInfo100', -0.3, underwritingInfo[0].variableCommission
        assertEquals 'underwritingInfo100', -0d, underwritingInfo[0].fixedCommission
    }
}