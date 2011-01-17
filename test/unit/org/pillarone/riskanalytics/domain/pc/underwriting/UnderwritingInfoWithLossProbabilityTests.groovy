package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.Exposure

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class UnderwritingInfoWithLossProbabilityTests extends GroovyTestCase {

    static UnderwritingInfoWithLossProbability getUnderwritingInfo() {
        return new UnderwritingInfoWithLossProbability(
                premium: 2000,
                numberOfPolicies: 100,
                lossProbability: 0.1,
                exposureDefinition: Exposure.ABSOLUTE)
    }

    static UnderwritingInfoWithLossProbability getUnderwritingInfo2() {
        return new UnderwritingInfoWithLossProbability(
                premium: 500,
                numberOfPolicies: 100,
                lossProbability: 0.05,
                exposureDefinition: Exposure.ABSOLUTE)
    }

    static UnderwritingInfoWithLossProbability getUnderwritingInfo3() {
        return new UnderwritingInfoWithLossProbability(
                premium: 1500,
                numberOfPolicies: 100,
                lossProbability: 0.03,
                exposureDefinition: Exposure.ABSOLUTE)
    }

    void testScaleValue() {
        assertEquals "absolute", 1, getUnderwritingInfo().scaleValue(Exposure.ABSOLUTE)
        assertEquals "premium written", 2000, getUnderwritingInfo().scaleValue(Exposure.PREMIUM_WRITTEN)

        assertEquals "absolute (freq)", 1, getUnderwritingInfo().scaleValue(FrequencyBase.ABSOLUTE)
        assertEquals "number of policies", 100, getUnderwritingInfo().scaleValue(FrequencyBase.NUMBER_OF_POLICIES)
    }

    void testAdd() {
        UnderwritingInfoWithLossProbability underwritingInfo0 = new UnderwritingInfoWithLossProbability()
        UnderwritingInfoWithLossProbability underwritingInfo1 = new UnderwritingInfoWithLossProbability(
                premium: 2000,
                numberOfPolicies: 100,
                lossProbability: 0.02,
                exposureDefinition: Exposure.ABSOLUTE,
        )
        underwritingInfo0.plus underwritingInfo1
        assertEquals "premium written", underwritingInfo1.premium, underwritingInfo0.premium
        assertEquals "numberOfPolicies", underwritingInfo1.numberOfPolicies, underwritingInfo0.numberOfPolicies
       // assertEquals "lossProbability", underwritingInfo1.lossProbability, underwritingInfo0.lossProbability
    }
}