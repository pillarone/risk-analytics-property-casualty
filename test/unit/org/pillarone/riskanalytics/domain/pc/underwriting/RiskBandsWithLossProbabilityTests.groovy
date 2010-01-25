package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author: Michael-Noe (at) Web (dot) de
 */
class RiskBandsWithLossProbabilityTests extends GroovyTestCase {

    static UnderwritingInfoWithLossProbability getUnderwritingInfo0() {
        return new UnderwritingInfoWithLossProbability(
            premiumWrittenAsIf: 5000,
            numberOfPolicies: 1000,
            sumInsured: 80,
            maxSumInsured: 100,
            lossProbability: 0.1,
            premiumWritten: 5000)
    }
    // second band: ceded = (200-100)/200 = 0.5
    static UnderwritingInfoWithLossProbability getUnderwritingInfo1() {
        return new UnderwritingInfoWithLossProbability(
            premiumWrittenAsIf: 2000,
            numberOfPolicies: 100,
            sumInsured: 200,
            maxSumInsured: 400,
            lossProbability: 0.05,
            premiumWritten: 2000)
    }
    // third band: ceded = (400-100)/500 = 0.6
    static UnderwritingInfoWithLossProbability getUnderwritingInfo2() {
        return new UnderwritingInfoWithLossProbability(
            premiumWrittenAsIf: 4000,
            numberOfPolicies: 50,
            sumInsured: 500,
            maxSumInsured: 800,
            lossProbability: 0.01,
            premiumWritten: 4000)
    }

    static List<UnderwritingInfoWithLossProbability> getUnderwritingInfos() {
        return [getUnderwritingInfo0(), getUnderwritingInfo1(), getUnderwritingInfo2()]
    }

    void testUsage() {
        AbstractMultiDimensionalParameter underwritingInformation1 = new TableMultiDimensionalParameter(
            [[100, 400, 800], [80, 200, 500], [5000, 2000, 4000], [1000, 100, 50], [0.1, 0.05, 0.001]],
            ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks', 'loss probability'],
        )

        AbstractMultiDimensionalParameter underwritingInformation2 = new TableMultiDimensionalParameter(
            [[100, 400, 800], [80, 200, 500], [5000, 2000, 4000], [1000, 100, 50], [0.1, 0.05, 0.001]],
            ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks', 'loss probability'],
        )

        RiskBandsWithLossProbability bands = new RiskBandsWithLossProbability(parmUnderwritingInformation: underwritingInformation1)
        bands.doCalculation()

        bands.outUnderwritingInfo.eachWithIndex {UnderwritingInfoWithLossProbability underwritingInfo, idx ->
            assertTrue "max sum insured info$idx", UnderwritingInfoUtilities.sameContent(underwritingInfos[idx], underwritingInfo)
        }

        assertEquals "attritional allocation table: max sum insured",
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
            underwritingInformation2.getColumnByName('number of policies/risks')
    }


}