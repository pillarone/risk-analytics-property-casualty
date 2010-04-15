package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

class StopLossContractStrategyTests extends GroovyTestCase {

    void testUsage150XS150ClaimDevelopmentPacket() {
        StopLossContractStrategy stoploss = new StopLossContractStrategy(attachmentPoint: 150,
                        limit: 150,
                        termLimit: 150,
                        premiumBase: PremiumBase.ABSOLUTE,
                        premium: 10)
        double coveredByReinsurer = 1

        ClaimDevelopmentPacket claim1P0 = new ClaimDevelopmentPacket(incurred: 250, paid: 100, reserved: 150)
        ClaimDevelopmentPacket claim2P0 = new ClaimDevelopmentPacket(incurred: 100, paid:  20, reserved:  80)
        ClaimDevelopmentPacket claim3P0 = new ClaimDevelopmentPacket(incurred: 150, paid:  80, reserved:  70)
        List claimsP0 = [claim1P0, claim2P0, claim3P0]
        List underwritingInfos = []
        underwritingInfos << new UnderwritingInfo(premiumWritten: 0)

        stoploss.initBookKeepingFiguresForIteration(claimsP0, underwritingInfos)
        stoploss.initBookKeepingFiguresOfPeriod(claimsP0, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P0ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P0, coveredByReinsurer)
        assertEquals 'claim1P0ceded.incurred', 75, claim1P0ceded.incurred
        assertEquals 'claim1P0ceded.paid',     25, claim1P0ceded.paid
        assertEquals 'claim1P0ceded.reserved', 50, claim1P0ceded.reserved

        ClaimDevelopmentPacket claim2P0ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P0, coveredByReinsurer)
        assertEquals 'claim2P0ceded.incurred', 30, claim2P0ceded.incurred
        assertEquals 'claim2P0ceded.paid',      5, claim2P0ceded.paid
        assertEquals 'claim2P0ceded.reserved', 25, claim2P0ceded.reserved

        ClaimDevelopmentPacket claim3P0ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P0, coveredByReinsurer)
        assertEquals 'claim3P0ceded.incurred', 45, claim3P0ceded.incurred
        assertEquals 'claim3P0ceded.paid',     20, claim3P0ceded.paid
        assertEquals 'claim3P0ceded.reserved', 25, claim3P0ceded.reserved


        ClaimDevelopmentPacket claim1P1 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:  80, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P1 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  40, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P1 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  40, originalClaim: claim3P0)
        List claimsP1 = [claim1P1, claim2P1, claim3P1]

        stoploss.initBookKeepingFiguresOfPeriod(claimsP1, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P1ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P1, coveredByReinsurer)
        assertEquals 'claim1P1ceded.incurred',  0, claim1P1ceded.incurred
//        assertEquals 'claim1P1ceded.paid',     20, claim1P1ceded.paid
//        assertEquals 'claim1P1ceded.reserved', 30, claim1P1ceded.reserved

        ClaimDevelopmentPacket claim2P1ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P1, coveredByReinsurer)
        assertEquals 'claim2P1ceded.incurred',  0, claim2P1ceded.incurred
//        assertEquals 'claim2P1ceded.paid',     30, claim2P1ceded.paid
//        assertEquals 'claim2P1ceded.reserved', -5, claim2P1ceded.reserved

        ClaimDevelopmentPacket claim3P1ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P1, coveredByReinsurer)
        assertEquals 'claim3P1ceded.incurred',  0, claim3P1ceded.incurred
//        assertEquals 'claim3P1ceded.paid',     30, claim3P1ceded.paid
//        assertEquals 'claim3P1ceded.reserved', -5, claim3P1ceded.reserved


        ClaimDevelopmentPacket claim1P2 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  50, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P2 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  10, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P2 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:  20, originalClaim: claim3P0)
        List claimsP2 = [claim1P2, claim2P2, claim3P2]

        stoploss.initBookKeepingFiguresOfPeriod(claimsP2, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P2ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P2, coveredByReinsurer)
        assertEquals 'claim1P2ceded.incurred',  0, claim1P2ceded.incurred
//        assertEquals 'claim1P2ceded.paid',      7.5, claim1P2ceded.paid
//        assertEquals 'claim1P2ceded.reserved', 22.5, claim1P2ceded.reserved

        ClaimDevelopmentPacket claim2P2ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P2, coveredByReinsurer)
        assertEquals 'claim2P2ceded.incurred',  0, claim2P2ceded.incurred
//        assertEquals 'claim2P2ceded.paid',      7.5, claim2P2ceded.paid
//        assertEquals 'claim2P2ceded.reserved',-12.5, claim2P2ceded.reserved

        ClaimDevelopmentPacket claim3P2ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P2, coveredByReinsurer)
        assertEquals 'claim3P2ceded.incurred',  0, claim3P2ceded.incurred
//        assertEquals 'claim3P2ceded.paid',      5, claim3P2ceded.paid
//        assertEquals 'claim3P2ceded.reserved',-10, claim3P2ceded.reserved


        ClaimDevelopmentPacket claim1P3 = new ClaimDevelopmentPacket(incurred:   0, paid:  50, reserved:   0, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P3 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:  10, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P3 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:   0, originalClaim: claim3P0)
        List claimsP3 = [claim1P3, claim2P3, claim3P3]

        stoploss.initBookKeepingFiguresOfPeriod(claimsP3, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P3ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P3, coveredByReinsurer)
        assertEquals 'claim1P3ceded.incurred',  0, claim1P3ceded.incurred
//        assertEquals 'claim1P3ceded.paid',      0, claim1P3ceded.paid
//        assertEquals 'claim1P3ceded.reserved', 22.5, claim1P3ceded.reserved

        ClaimDevelopmentPacket claim2P3ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P3, coveredByReinsurer)
        assertEquals 'claim2P3ceded.incurred',  0, claim2P3ceded.incurred
//        assertEquals 'claim2P3ceded.paid',      0, claim2P3ceded.paid
//        assertEquals 'claim2P3ceded.reserved',-12.5, claim2P3ceded.reserved

        ClaimDevelopmentPacket claim3P3ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P3, coveredByReinsurer)
        assertEquals 'claim3P3ceded.incurred',  0, claim3P3ceded.incurred
//        assertEquals 'claim3P3ceded.paid',      0, claim3P3ceded.paid
//        assertEquals 'claim3P3ceded.reserved',-10, claim3P3ceded.reserved
    }

    void testUsage100XS200ClaimDevelopmentPacket() {
        StopLossContractStrategy stoploss = new StopLossContractStrategy(attachmentPoint: 200,
                        limit: 100,
                        termLimit: 100,
                        premiumBase: PremiumBase.ABSOLUTE,
                        premium: 10)
        double coveredByReinsurer = 1

        ClaimDevelopmentPacket claim1P0 = new ClaimDevelopmentPacket(incurred: 250, paid: 100, reserved: 150)
        ClaimDevelopmentPacket claim2P0 = new ClaimDevelopmentPacket(incurred: 100, paid:  20, reserved:  80)
        ClaimDevelopmentPacket claim3P0 = new ClaimDevelopmentPacket(incurred: 150, paid:  80, reserved:  70)
        List claimsP0 = [claim1P0, claim2P0, claim3P0]
        List underwritingInfos = []
        underwritingInfos << new UnderwritingInfo(premiumWritten: 0)

        stoploss.initBookKeepingFiguresForIteration(claimsP0, underwritingInfos)
        stoploss.initBookKeepingFiguresOfPeriod(claimsP0, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P0ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P0, coveredByReinsurer)
        assertEquals 'claim1P0ceded.incurred', 50, claim1P0ceded.incurred
        assertEquals 'claim1P0ceded.paid',      0, claim1P0ceded.paid
        assertEquals 'claim1P0ceded.reserved', 50, claim1P0ceded.reserved

        ClaimDevelopmentPacket claim2P0ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P0, coveredByReinsurer)
        assertEquals 'claim2P0ceded.incurred', 20, claim2P0ceded.incurred
        assertEquals 'claim2P0ceded.paid',      0, claim2P0ceded.paid
        assertEquals 'claim2P0ceded.reserved', 20, claim2P0ceded.reserved

        ClaimDevelopmentPacket claim3P0ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P0, coveredByReinsurer)
        assertEquals 'claim3P0ceded.incurred', 30, claim3P0ceded.incurred
        assertEquals 'claim3P0ceded.paid',      0, claim3P0ceded.paid
        assertEquals 'claim3P0ceded.reserved', 30, claim3P0ceded.reserved


        ClaimDevelopmentPacket claim1P1 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:  80, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P1 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  40, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P1 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  40, originalClaim: claim3P0)
        List claimsP1 = [claim1P1, claim2P1, claim3P1]

        stoploss.initBookKeepingFiguresOfPeriod(claimsP1, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P1ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P1, coveredByReinsurer)
        assertEquals 'claim1P1ceded.incurred',  0, claim1P1ceded.incurred
//        assertEquals 'claim1P1ceded.paid',     20, claim1P1ceded.paid
//        assertEquals 'claim1P1ceded.reserved', 30, claim1P1ceded.reserved

        ClaimDevelopmentPacket claim2P1ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P1, coveredByReinsurer)
        assertEquals 'claim2P1ceded.incurred',  0, claim2P1ceded.incurred
//        assertEquals 'claim2P1ceded.paid',     30, claim2P1ceded.paid
//        assertEquals 'claim2P1ceded.reserved',-10, claim2P1ceded.reserved

        ClaimDevelopmentPacket claim3P1ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P1, coveredByReinsurer)
        assertEquals 'claim3P1ceded.incurred',  0, claim3P1ceded.incurred
//        assertEquals 'claim3P1ceded.paid',     30, claim3P1ceded.paid
//        assertEquals 'claim3P1ceded.reserved',  0, claim3P1ceded.reserved


        ClaimDevelopmentPacket claim1P2 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  50, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P2 = new ClaimDevelopmentPacket(incurred:   0, paid:  30, reserved:  10, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P2 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:  20, originalClaim: claim3P0)
        List claimsP2 = [claim1P2, claim2P2, claim3P2]

        stoploss.initBookKeepingFiguresOfPeriod(claimsP2, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P2ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P2, coveredByReinsurer)
        assertEquals 'claim1P2ceded.incurred',  0, claim1P2ceded.incurred
//        assertEquals 'claim1P2ceded.paid',      7.5, claim1P2ceded.paid
//        assertEquals 'claim1P2ceded.reserved', 22.5, claim1P2ceded.reserved

        ClaimDevelopmentPacket claim2P2ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P2, coveredByReinsurer)
        assertEquals 'claim2P2ceded.incurred',  0, claim2P2ceded.incurred
//        assertEquals 'claim2P2ceded.paid',      7.5, claim2P2ceded.paid
//        assertEquals 'claim2P2ceded.reserved',-17.5, claim2P2ceded.reserved

        ClaimDevelopmentPacket claim3P2ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P2, coveredByReinsurer)
        assertEquals 'claim3P2ceded.incurred',  0, claim3P2ceded.incurred
//        assertEquals 'claim3P2ceded.paid',      5, claim3P2ceded.paid
//        assertEquals 'claim3P2ceded.reserved',  0, claim3P2ceded.reserved


        ClaimDevelopmentPacket claim1P3 = new ClaimDevelopmentPacket(incurred:   0, paid:  50, reserved:   0, originalClaim: claim1P0)
        ClaimDevelopmentPacket claim2P3 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:  10, originalClaim: claim2P0)
        ClaimDevelopmentPacket claim3P3 = new ClaimDevelopmentPacket(incurred:   0, paid:  20, reserved:   0, originalClaim: claim3P0)
        List claimsP3 = [claim1P3, claim2P3, claim3P3]

        stoploss.initBookKeepingFiguresOfPeriod(claimsP3, underwritingInfos, coveredByReinsurer)
        ClaimDevelopmentPacket claim1P3ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim1P3, coveredByReinsurer)
        assertEquals 'claim1P3ceded.incurred',  0, claim1P3ceded.incurred
//        assertEquals 'claim1P3ceded.paid',      0, claim1P3ceded.paid
//        assertEquals 'claim1P3ceded.reserved', 22.5, claim1P3ceded.reserved

        ClaimDevelopmentPacket claim2P3ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim2P3, coveredByReinsurer)
        assertEquals 'claim2P3ceded.incurred',  0, claim2P3ceded.incurred
//        assertEquals 'claim2P3ceded.paid',      0, claim2P3ceded.paid
//        assertEquals 'claim2P3ceded.reserved',-17.5  , claim2P3ceded.reserved

        ClaimDevelopmentPacket claim3P3ceded = (ClaimDevelopmentPacket) stoploss.calculateCededClaim(claim3P3, coveredByReinsurer)
        assertEquals 'claim3P3ceded.incurred',  0, claim3P3ceded.incurred
//        assertEquals 'claim3P3ceded.paid',      0, claim3P3ceded.paid
//        assertEquals 'claim3P3ceded.reserved',  0, claim3P3ceded.reserved
    }
}