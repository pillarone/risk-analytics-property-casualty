package models.mark

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

model = MarkModel
periodCount = 2

allPeriods = 0..<periodCount
mean = 1.0
stDev = 0.5
lambda = 0.3

components {
    frequencyGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": lambda])
        parmBase[allPeriods] = FrequencyBase.ABSOLUTE
    }
    claimsGeneratorFire {
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 10.4d, "stDev": 2.1d])
        parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min": 5d, "max": 10d])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
    claimsGeneratorMotor {
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": (2 * mean), "stDev": (0.75 * stDev)])
    }
}
