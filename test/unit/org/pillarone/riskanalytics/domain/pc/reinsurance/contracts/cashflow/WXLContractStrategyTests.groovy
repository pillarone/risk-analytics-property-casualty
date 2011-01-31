package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class WXLContractStrategyTests extends GroovyTestCase {

    void testUsage50XS70ClaimDevelopmentPacket() {
        WXLContractStrategy wxl = new WXLContractStrategy(attachmentPoint: 70,
                        limit: 50,
                        annualLimit: 50,
                        termLimit: 50,
                        premiumBase: PremiumBase.ABSOLUTE,
                        premium: 10,
                        reinstatementPremiums: new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium']))
        double coveredByReinsurer = 1
        List underwritingInfos = []
        ClaimDevelopmentPacket claim1P0 = new ClaimDevelopmentPacket(incurred: 100, paid:  20, reserved:  80, claimType: ClaimType.SINGLE)
        ClaimDevelopmentPacket claim2P0 = new ClaimDevelopmentPacket(incurred:  150, paid:  80, reserved:  70, claimType: ClaimType.SINGLE)
        ClaimDevelopmentPacket claim3P0 = new ClaimDevelopmentPacket(incurred:  200, paid:  100, reserved:  100, claimType: ClaimType.SINGLE)
        List claimsP0 = [claim1P0, claim2P0, claim3P0]
        underwritingInfos << new UnderwritingInfo(premium: 0)
        wxl.initBookKeepingFiguresForIteration(claimsP0, underwritingInfos)

        wxl.initBookKeepingFiguresOfPeriod(claimsP0, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P0ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P0, coveredByReinsurer)
        assertEquals 'claim1P0ceded.incurred', 30, claim1P0ceded.incurred
        assertEquals 'claim1P0ceded.paid',  0, claim1P0ceded.paid
        assertEquals 'claim1P0ceded.reserved', 30, claim1P0ceded.reserved

        ClaimDevelopmentPacket claim2P0ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P0, coveredByReinsurer)
        assertEquals 'claim2P0ceded.incurred', 20, claim2P0ceded.incurred
        assertEquals 'claim2P0ceded.paid', 10, claim2P0ceded.paid
        assertEquals 'claim2P0ceded.reserved', 10, claim2P0ceded.reserved

        ClaimDevelopmentPacket claim3P0ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P0, coveredByReinsurer)
        assertEquals 'claim3P0ceded.incurred',  0, claim3P0ceded.incurred
        assertEquals 'claim3P0ceded.paid',  0, claim3P0ceded.paid
        assertEquals 'claim3P0ceded.reserved',  0, claim3P0ceded.reserved

        ClaimDevelopmentPacket claim1P1 = new ClaimDevelopmentPacket(incurred: 0, paid:  30, reserved:  50, claimType: ClaimType.SINGLE, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P1 = new ClaimDevelopmentPacket(incurred:  0, paid:  30, reserved:  40, claimType: ClaimType.SINGLE, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P1 = new ClaimDevelopmentPacket(incurred:  0, paid:  20, reserved:  80, claimType: ClaimType.SINGLE, originalClaim: claim3P0)
        List claimsP1 = [claim1P1, claim2P1, claim3P1]
        wxl.initBookKeepingFiguresOfPeriod(claimsP1, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P1ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P1, coveredByReinsurer)
        assertEquals 'claim1P1ceded.incurred',  0, claim1P1ceded.incurred
        assertEquals 'claim1P1ceded.paid',  0, claim1P1ceded.paid
        assertEquals 'claim1P1ceded.reserved', 30, claim1P1ceded.reserved

        ClaimDevelopmentPacket claim2P1ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P1, coveredByReinsurer)
        assertEquals 'claim2P1ceded.incurred',  0, claim2P1ceded.incurred
        assertEquals 'claim2P1ceded.paid', 10, claim2P1ceded.paid
        assertEquals 'claim2P1ceded.reserved',  0, claim2P1ceded.reserved

        ClaimDevelopmentPacket claim3P1ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P1, coveredByReinsurer)
        assertEquals 'claim3P1ceded.incurred',  0, claim3P1ceded.incurred
        assertEquals 'claim3P1ceded.paid',  0, claim3P1ceded.paid
        assertEquals 'claim3P1ceded.reserved',  0, claim3P1ceded.reserved

        ClaimDevelopmentPacket claim1P2 = new ClaimDevelopmentPacket(incurred: 0, paid:  10, reserved:  40, claimType: ClaimType.SINGLE, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P2 = new ClaimDevelopmentPacket(incurred:  0, paid:  20, reserved:  20, claimType: ClaimType.SINGLE, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P2 = new ClaimDevelopmentPacket(incurred:  0, paid:  30, reserved:  50, claimType: ClaimType.SINGLE, originalClaim: claim3P0)
        List claimsP2 = [claim1P2, claim2P2, claim3P2]
        wxl.initBookKeepingFiguresOfPeriod(claimsP2, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P2ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P2, coveredByReinsurer)
        assertEquals 'claim1P2ceded.incurred', 0, claim1P2ceded.incurred
        assertEquals 'claim1P2ceded.paid', 0, claim1P2ceded.paid
        assertEquals 'claim1P2ceded.reserved', 30, claim1P2ceded.reserved

        ClaimDevelopmentPacket claim2P2ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P2, coveredByReinsurer)
        assertEquals 'claim2P2ceded.incurred', 0, claim2P2ceded.incurred
        assertEquals 'claim2P2ceded.paid', 0, claim2P2ceded.paid
        assertEquals 'claim2P2ceded.reserved', 0, claim2P2ceded.reserved

        ClaimDevelopmentPacket claim3P2ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P2, coveredByReinsurer)
        assertEquals 'claim3P2ceded.incurred',  0, claim3P2ceded.incurred
        assertEquals 'claim3P2ceded.paid',  0, claim3P2ceded.paid
        assertEquals 'claim3P2ceded.reserved',  0, claim3P2ceded.reserved

        ClaimDevelopmentPacket claim1P3 = new ClaimDevelopmentPacket(incurred: 0, paid:  20, reserved:  20, claimType: ClaimType.SINGLE, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P3 = new ClaimDevelopmentPacket(incurred:  0, paid:  20, reserved:  0, claimType: ClaimType.SINGLE, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P3 = new ClaimDevelopmentPacket(incurred:  0, paid:  50, reserved:  0, claimType: ClaimType.SINGLE, originalClaim: claim3P0)
        List claimsP3 = [claim1P3, claim2P3, claim3P3]
        wxl.initBookKeepingFiguresOfPeriod(claimsP3, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P3ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P3, coveredByReinsurer)
        assertEquals 'claim1P3ceded.incurred', 0, claim1P3ceded.incurred
        assertEquals 'claim1P3ceded.paid', 10, claim1P3ceded.paid
        assertEquals 'claim1P3ceded.reserved', 20, claim1P3ceded.reserved

        ClaimDevelopmentPacket claim2P3ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P3, coveredByReinsurer)
        assertEquals 'claim2P3ceded.incurred', 0, claim2P3ceded.incurred
        assertEquals 'claim2P3ceded.paid', 0, claim2P3ceded.paid
        assertEquals 'claim2P3ceded.reserved', 0, claim2P3ceded.reserved

        ClaimDevelopmentPacket claim3P3ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P3, coveredByReinsurer)
        assertEquals 'claim3P3ceded.incurred',  0, claim3P3ceded.incurred
        assertEquals 'claim3P3ceded.paid',  0, claim3P3ceded.paid
        assertEquals 'claim3P3ceded.reserved',  0, claim3P3ceded.reserved
    }

    void testUsage80XS120ClaimDevelopmentPacket() {
        WXLContractStrategy wxl = new WXLContractStrategy(attachmentPoint: 120,
                        limit: 80,
                        annualLimit: 80,
                        termLimit: 80,
                        premiumBase: PremiumBase.ABSOLUTE,
                        premium: 10,
                        reinstatementPremiums: new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium']))
        double coveredByReinsurer = 1

        List underwritingInfos = [new UnderwritingInfo(premium: 0)]

        ClaimDevelopmentPacket claim1P0 = new ClaimDevelopmentPacket(incurred: 100, paid:  20, reserved:  80, claimType: ClaimType.SINGLE)
        ClaimDevelopmentPacket claim2P0 = new ClaimDevelopmentPacket(incurred:  150, paid:  80, reserved:  70, claimType: ClaimType.SINGLE)
        ClaimDevelopmentPacket claim3P0 = new ClaimDevelopmentPacket(incurred:  200, paid:  100, reserved:  100, claimType: ClaimType.SINGLE)
        List claimsP0 = [claim1P0, claim2P0, claim3P0]

        wxl.initBookKeepingFiguresForIteration(claimsP0, underwritingInfos)
        wxl.initBookKeepingFiguresOfPeriod(claimsP0, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P0ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P0, coveredByReinsurer)
        assertEquals 'claim1P0ceded.incurred', 0, claim1P0ceded.incurred
        assertEquals 'claim1P0ceded.paid',  0, claim1P0ceded.paid
        assertEquals 'claim1P0ceded.reserved', 0, claim1P0ceded.reserved

        ClaimDevelopmentPacket claim2P0ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P0, coveredByReinsurer)
        assertEquals 'claim2P0ceded.incurred', 30, claim2P0ceded.incurred
        assertEquals 'claim2P0ceded.paid', 0, claim2P0ceded.paid
        assertEquals 'claim2P0ceded.reserved', 30, claim2P0ceded.reserved

        ClaimDevelopmentPacket claim3P0ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P0, coveredByReinsurer)
        assertEquals 'claim3P0ceded.incurred',  50, claim3P0ceded.incurred
        assertEquals 'claim3P0ceded.paid',  0, claim3P0ceded.paid
        assertEquals 'claim3P0ceded.reserved',  50, claim3P0ceded.reserved

        ClaimDevelopmentPacket claim1P1 = new ClaimDevelopmentPacket(incurred: 0, paid:  30, reserved:  50, claimType: ClaimType.SINGLE, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P1 = new ClaimDevelopmentPacket(incurred:  0, paid:  30, reserved:  40, claimType: ClaimType.SINGLE, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P1 = new ClaimDevelopmentPacket(incurred:  0, paid:  20, reserved:  80, claimType: ClaimType.SINGLE, originalClaim: claim3P0)
        List claimsP1 = [claim1P1, claim2P1, claim3P1]
        wxl.initBookKeepingFiguresOfPeriod(claimsP1, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P1ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P1, coveredByReinsurer)
        assertEquals 'claim1P1ceded.incurred',  0, claim1P1ceded.incurred
        assertEquals 'claim1P1ceded.paid',  0, claim1P1ceded.paid
        assertEquals 'claim1P1ceded.reserved', 0, claim1P1ceded.reserved

        ClaimDevelopmentPacket claim2P1ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P1, coveredByReinsurer)
        assertEquals 'claim2P1ceded.incurred',  0, claim2P1ceded.incurred
        assertEquals 'claim2P1ceded.paid', 0, claim2P1ceded.paid
        assertEquals 'claim2P1ceded.reserved',  30, claim2P1ceded.reserved

        ClaimDevelopmentPacket claim3P1ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P1, coveredByReinsurer)
        assertEquals 'claim3P1ceded.incurred',  0, claim3P1ceded.incurred
        assertEquals 'claim3P1ceded.paid',  0, claim3P1ceded.paid
        assertEquals 'claim3P1ceded.reserved',  50, claim3P1ceded.reserved

        ClaimDevelopmentPacket claim1P2 = new ClaimDevelopmentPacket(incurred: 0, paid:  10, reserved:  40, claimType: ClaimType.SINGLE, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P2 = new ClaimDevelopmentPacket(incurred:  0, paid:  20, reserved:  20, claimType: ClaimType.SINGLE, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P2 = new ClaimDevelopmentPacket(incurred:  0, paid:  30, reserved:  50, claimType: ClaimType.SINGLE, originalClaim: claim3P0)
        List claimsP2 = [claim1P2, claim2P2, claim3P2]
        wxl.initBookKeepingFiguresOfPeriod(claimsP2, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P2ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P2, coveredByReinsurer)
        assertEquals 'claim1P2ceded.incurred', 0, claim1P2ceded.incurred
        assertEquals 'claim1P2ceded.paid', 0, claim1P2ceded.paid
        assertEquals 'claim1P2ceded.reserved', 0, claim1P2ceded.reserved

        ClaimDevelopmentPacket claim2P2ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P2, coveredByReinsurer)
        assertEquals 'claim2P2ceded.incurred', 0, claim2P2ceded.incurred
        assertEquals 'claim2P2ceded.paid', 10, claim2P2ceded.paid
        assertEquals 'claim2P2ceded.reserved', 20, claim2P2ceded.reserved

        ClaimDevelopmentPacket claim3P2ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P2, coveredByReinsurer)
        assertEquals 'claim3P2ceded.incurred',  0, claim3P2ceded.incurred
        assertEquals 'claim3P2ceded.paid',  30, claim3P2ceded.paid
        assertEquals 'claim3P2ceded.reserved',  20, claim3P2ceded.reserved

        ClaimDevelopmentPacket claim1P3 = new ClaimDevelopmentPacket(incurred: 0, paid:  20, reserved:  20, claimType: ClaimType.SINGLE, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P3 = new ClaimDevelopmentPacket(incurred:  0, paid:  20, reserved:  0, claimType: ClaimType.SINGLE, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P3 = new ClaimDevelopmentPacket(incurred:  0, paid:  50, reserved:  0, claimType: ClaimType.SINGLE, originalClaim: claim3P0)
        List claimsP3 = [claim1P3, claim2P3, claim3P3]
        wxl.initBookKeepingFiguresOfPeriod(claimsP3, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P3ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim1P3, coveredByReinsurer)
        assertEquals 'claim1P3ceded.incurred', 0, claim1P3ceded.incurred
        assertEquals 'claim1P3ceded.paid', 0, claim1P3ceded.paid
        assertEquals 'claim1P3ceded.reserved', 0, claim1P3ceded.reserved

        ClaimDevelopmentPacket claim2P3ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim2P3, coveredByReinsurer)
        assertEquals 'claim2P3ceded.incurred', 0, claim2P3ceded.incurred
        assertEquals 'claim2P3ceded.paid', 20, claim2P3ceded.paid
        assertEquals 'claim2P3ceded.reserved', 0, claim2P3ceded.reserved

        ClaimDevelopmentPacket claim3P3ceded = (ClaimDevelopmentPacket) wxl.calculateCededClaim(claim3P3, coveredByReinsurer)
        assertEquals 'claim3P3ceded.incurred',  0, claim3P3ceded.incurred
        assertEquals 'claim3P3ceded.paid',  20, claim3P3ceded.paid
        assertEquals 'claim3P3ceded.reserved',  0, claim3P3ceded.reserved
    }
}
