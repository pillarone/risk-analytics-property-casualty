package models.oneLobQSXL

import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter


model = OneLobQSXLModel
periodCount = 2
allPeriods = 0..<periodCount

components {
    frequencyGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 1])
        parmBase[allPeriods] = FrequencyBase.ABSOLUTE
    }
    claimsGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 20, "stDev": 10])
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ["alpha": 1, "beta": 2])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
    quotaShare {
        parmContractStrategy[allPeriods] = new QuotaShareContractStrategy("quotaShare": 0.2, "commission": 0d, "coveredByReinsurer": 1d)
    }
    wxl {
        parmContractStrategy[allPeriods] = new WXLContractStrategy(
                "attachmentPoint": 50, "limit": 10, "aggregateLimit": 100,
                "premiumBase": PremiumBase.ABSOLUTE, "premium": 70,
                "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']),
                "coveredByReinsurer": 1d)
    }
}