package models.sparrow

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

model = SparrowModel
periodCount = 1
allPeriods = 0..<periodCount

components {
    frequencyGenerator {
        // assigning to an index enables to use different instances in periods
        // (see ParameterInjectorTests.testMultiplePeriodParameterization())
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ["constant": 10])
        parmBase[allPeriods] = FrequencyBase.ABSOLUTE
    }
    claimsGenerator {
        // for assignments with a range, the same instance of parmGenerator is used. parmGenerator[0].is(parmGenerator[n])
        // (see ParameterInjectorTests.testMultiplePeriodParameterization())
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ["constant": 10])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
}

