package models.sparrow

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

model = SparrowModel
periodCount = 2
allPeriods = 0..<periodCount

mean = 10d
stDev = 0.5
lambda = 2.3

components {
    frequencyGenerator {
        // assigning to an index enables to use different instances in periods
        // (see ParameterInjectorTests.testMultiplePeriodParameterization())
        parmDistribution[0] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": lambda])
        parmDistribution[1] = DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["alpha": 1d, "beta": 2d])
        parmBase[0..<periodCount] = FrequencyBase.ABSOLUTE

    }
    claimsGenerator {
        // for assignments with a range, the same instance of parmGenerator is used. parmGenerator[0].is(parmGenerator[n])
        // (see ParameterInjectorTests.testMultiplePeriodParameterization())
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": mean, "stDev": stDev])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
}
