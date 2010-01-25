package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase


class ExposureInfoTests extends GroovyTestCase {

    static ExposureInfo getExposureInfo() {
        return new ExposureInfo(
                premiumWrittenAsIf: 2000,
                numberOfPolicies: 100,
                sumInsured: 200,
                maxSumInsured: 1000)
    }

    void testScaleValue() {
        assertEquals "absolute", 1, getExposureInfo().scaleValue(Exposure.ABSOLUTE)
        assertEquals "premium written", 2000, getExposureInfo().scaleValue(Exposure.PREMIUM_WRITTEN)

        assertEquals "absolute (freq)", 1, getExposureInfo().scaleValue(FrequencyBase.ABSOLUTE)
        assertEquals "number of policies", 100, getExposureInfo().scaleValue(FrequencyBase.NUMBER_OF_POLICIES)
    }

    void testPlus() {
        ExposureInfo exposureInfo0 = new ExposureInfo()
        ExposureInfo exposureInfo1 = new ExposureInfo(
                premiumWrittenAsIf: 2000,
                numberOfPolicies: 100,
                sumInsured: 10000,
                maxSumInsured: 100000,
                exposureDefinition: Exposure.ABSOLUTE,
        )
        exposureInfo0.plus exposureInfo1
        assertEquals "premium written as if", exposureInfo1.premiumWrittenAsIf, exposureInfo0.premiumWrittenAsIf
        assertEquals "number of policies", exposureInfo1.numberOfPolicies, exposureInfo0.numberOfPolicies
    }

    void testPlusNull() {
        ExposureInfo exposureInfo0 = new ExposureInfo(exposureInfo)
        exposureInfo0.plus null
        assertEquals "premium written as if", exposureInfo.premiumWrittenAsIf, exposureInfo0.premiumWrittenAsIf
        assertEquals "number of policies", exposureInfo.numberOfPolicies, exposureInfo0.numberOfPolicies
        assertEquals "sum insured", exposureInfo.sumInsured, exposureInfo0.sumInsured
        assertEquals "max sum insured", exposureInfo.maxSumInsured, exposureInfo0.maxSumInsured
    }

    void testPlusDifferentExposureDefinition() {
        ExposureInfo exposureInfo0 = new ExposureInfo()
        exposureInfo0.exposureDefinition = Exposure.ABSOLUTE
        ExposureInfo exposureInfo1 = new ExposureInfo()
        exposureInfo1.exposureDefinition = Exposure.PREMIUM_WRITTEN
        exposureInfo0.plus exposureInfo1
        assertNull "exposure definition", exposureInfo0.exposureDefinition
    }

    void testMinusDifferentExposureDefinition() {
        ExposureInfo exposureInfo0 = new ExposureInfo()
        exposureInfo0.exposureDefinition = Exposure.ABSOLUTE
        ExposureInfo exposureInfo1 = new ExposureInfo()
        exposureInfo1.exposureDefinition = Exposure.PREMIUM_WRITTEN
        exposureInfo0.minus exposureInfo1
        assertNull "exposure definition", exposureInfo0.exposureDefinition
    }
}