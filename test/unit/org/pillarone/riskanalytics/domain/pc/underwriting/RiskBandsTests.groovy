package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

class RiskBandsTests extends GroovyTestCase {

    static UnderwritingInfo getUnderwritingInfo0() {
        return new UnderwritingInfo(
                premiumWrittenAsIf: 5000d,
                numberOfPolicies: 1000d,
                sumInsured: 80d,
                maxSumInsured: 100d,
                premiumWritten: 5000d)
    }
    // second band: ceded = (200-100)/200 = 0.5
    static UnderwritingInfo getUnderwritingInfo1() {
        return new UnderwritingInfo(
                premiumWrittenAsIf: 2000d,
                numberOfPolicies: 100d,
                sumInsured: 200d,
                maxSumInsured: 400d,
                premiumWritten: 2000d)
    }
    // third band: ceded = (400-100)/500 = 0.6
    static UnderwritingInfo getUnderwritingInfo2() {
        return new UnderwritingInfo(
                premiumWrittenAsIf: 4000d,
                numberOfPolicies: 50d,
                sumInsured: 500d,
                maxSumInsured: 800d,
                premiumWritten: 4000d)
    }

    static List<UnderwritingInfo> getUnderwritingInfos() {
        return [getUnderwritingInfo0(), getUnderwritingInfo1(), getUnderwritingInfo2()]
    }

    void testUsage() {
        AbstractMultiDimensionalParameter underwritingInformation1 = new TableMultiDimensionalParameter(
                [[100d, 400d, 800d], [80d, 200d, 500d], [5000d, 2000d, 4000d], [1000d, 100d, 50d]],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies'],
        )

        AbstractMultiDimensionalParameter underwritingInformation2 = new TableMultiDimensionalParameter(
                [[100d, 400d, 800d], [80d, 200d, 500d], [5000d, 2000d, 4000d], [1000d, 100d, 50d]],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies'],
        )

        RiskBands bands = new RiskBands(parmUnderwritingInformation: underwritingInformation1)
        bands.doCalculation()

        bands.outUnderwritingInfo.eachWithIndex {UnderwritingInfo underwritingInfo, idx ->
            UnderwritingInfo uwInfo = underwritingInfos[idx]
            uwInfo.origin = bands
            assertTrue "max sum insured info$idx", UnderwritingInfoUtilities.sameContent(uwInfo, underwritingInfo)
        }

/*        assertEquals "attritional allocation table: max sum insured",
            bands.outAttritionalTargetDistribution[0].table.getColumnByName('maximum sum insured'),
            underwritingInformation2.getColumnByName('maximum sum insured')
        assertEquals "attritional allocation table: premium",
            bands.outAttritionalTargetDistribution[0].table.getColumnByName('portion'),
            underwritingInformation2.getColumnByName('premium')
        assertEquals "single allocation table: max sum insured",
            bands.outSingleTargetDistribution[0].table.getColumnByName('maximum sum insured'),
            underwritingInformation2.getColumnByName('maximum sum insured')
        assertEquals "single allocation table: number of policies/risks",
            bands.outSingleTargetDistribution[0].table.getColumnByName('portion'),
            underwritingInformation2.getColumnByName('number of policies/risks')*/
    }


}