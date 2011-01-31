package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.components.IterationStore
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

class RiskBandsTests extends GroovyTestCase {

    static UnderwritingInfo getUnderwritingInfo0() {
        return new UnderwritingInfo(
                numberOfPolicies: 1000d,
                sumInsured: 80d,
                maxSumInsured: 100d,
                premium: 5000d)
    }
    // second band: ceded = (200-100)/200 = 0.5
    static UnderwritingInfo getUnderwritingInfo1() {
        return new UnderwritingInfo(
                numberOfPolicies: 100d,
                sumInsured: 200d,
                maxSumInsured: 400d,
                premium: 2000d)
    }
    // third band: ceded = (400-100)/500 = 0.6
    static UnderwritingInfo getUnderwritingInfo2() {
        return new UnderwritingInfo(
                numberOfPolicies: 50d,
                sumInsured: 500d,
                maxSumInsured: 800d,
                premium: 4000d)
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

        IterationScope iterationScope = new IterationScope(periodScope: new PeriodScope())
        iterationScope.prepareNextIteration()
        RiskBands bands = new RiskBands(
                parmUnderwritingInformation: underwritingInformation1,
                iterationScope: iterationScope,
                iterationStore: new IterationStore(iterationScope)
        )
        bands.doCalculation()

        bands.outUnderwritingInfo.eachWithIndex {UnderwritingInfo underwritingInfo, idx ->
            UnderwritingInfo uwInfo = underwritingInfos[idx]
            uwInfo.origin = bands
            assertTrue "max sum insured info$idx", UnderwritingInfoUtilities.sameContent(uwInfo, underwritingInfo)
        }

    }


}