package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase


class UnderwritingInfoTests extends GroovyTestCase {

    static UnderwritingInfo getUnderwritingInfo() {
        return new UnderwritingInfo(
                premium: 2000,
                numberOfPolicies: 100,
                exposureDefinition: Exposure.ABSOLUTE)
    }

    static UnderwritingInfo getUnderwritingInfo2() {
        return new UnderwritingInfo(
                premium: 500,
                numberOfPolicies: 100,
                exposureDefinition: Exposure.ABSOLUTE)
    }

    static UnderwritingInfo getUnderwritingInfo3() {
        return new UnderwritingInfo(
                premium: 1500,
                numberOfPolicies: 100,
                exposureDefinition: Exposure.ABSOLUTE)
    }

  static UnderwritingInfo getUnderwritingInfo4() {
      return new UnderwritingInfo(
              premium: 1000,
              numberOfPolicies: 100,
              exposureDefinition: Exposure.ABSOLUTE)
  }

    void testScaleValue() {
        assertEquals "absolute", 1, getUnderwritingInfo().scaleValue(Exposure.ABSOLUTE)
        assertEquals "premium written", 2000, getUnderwritingInfo().scaleValue(Exposure.PREMIUM_WRITTEN)

        assertEquals "absolute (freq)", 1, getUnderwritingInfo().scaleValue(FrequencyBase.ABSOLUTE)
        assertEquals "number of policies", 100, getUnderwritingInfo().scaleValue(FrequencyBase.NUMBER_OF_POLICIES)
    }

    void testAdd() {
        UnderwritingInfo underwritingInfo0 = new UnderwritingInfo()
        UnderwritingInfo underwritingInfo1 = new UnderwritingInfo(
                premium: 2000,
                numberOfPolicies: 100,
                exposureDefinition: Exposure.ABSOLUTE,
        )
        underwritingInfo0.plus underwritingInfo1
        assertEquals "premium written", underwritingInfo1.premium, underwritingInfo0.premium
        assertEquals "numberOfPolicies", underwritingInfo1.numberOfPolicies, underwritingInfo0.numberOfPolicies
    }
}