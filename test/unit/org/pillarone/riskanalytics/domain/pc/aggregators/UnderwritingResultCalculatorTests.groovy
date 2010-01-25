package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class UnderwritingResultCalculatorTests extends GroovyTestCase {

    void testUsage() {
        UnderwritingResultCalculator underwritingResultCalculator = new UnderwritingResultCalculator()
        underwritingResultCalculator.inClaims << new Claim(value: 50)
        underwritingResultCalculator.inUnderwritingInfo << new UnderwritingInfo(premiumWritten: 80, commission: 15)

        underwritingResultCalculator.doCalculation()
        assertEquals "result", 45, underwritingResultCalculator.outUnderwritingResult[0].underwritingResult
    }

    void testMultiplePacketInput() {
        UnderwritingResultCalculator underwritingResultCalculator = new UnderwritingResultCalculator()
        underwritingResultCalculator.inClaims << new Claim(value: 50) << new Claim(value: 60)
        underwritingResultCalculator.inUnderwritingInfo << new UnderwritingInfo(premiumWritten: 80, commission: 15)
        underwritingResultCalculator.inUnderwritingInfo << new UnderwritingInfo(premiumWritten: 100, commission: 20)

        underwritingResultCalculator.doCalculation()
        assertEquals "premium", 180, underwritingResultCalculator.outUnderwritingResult[0].premium
        assertEquals "commission", 35, underwritingResultCalculator.outUnderwritingResult[0].commission
        assertEquals "claim", 110, underwritingResultCalculator.outUnderwritingResult[0].claim
        assertEquals "result", 105, underwritingResultCalculator.outUnderwritingResult[0].underwritingResult
    }

}