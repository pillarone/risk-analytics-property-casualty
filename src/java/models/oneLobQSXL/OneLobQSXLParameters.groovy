package models.oneLobQSXL

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategy
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

model = OneLobQSXLModel
periodCount = 2
allPeriods = 0..<periodCount

components {
    frequencyGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 1])
        parmBase[allPeriods] = FrequencyBase.ABSOLUTE
    }
    claimsGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 20, "stDev": 10])
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["alpha": 1, "beta": 2])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
    quotaShare {
        parmContractStrategy[allPeriods] = new QuotaShareContractStrategy("quotaShare": 0.2, "coveredByReinsurer": 1d)
//        parmCommissionStrategy[allPeriods] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
    }
    wxl {
        parmContractStrategy[allPeriods] = new WXLContractStrategy(
                "attachmentPoint": 50, "limit": 10, "aggregateLimit": 100,
                "premiumBase": PremiumBase.ABSOLUTE, "premium": 70,
                "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']),
                "coveredByReinsurer": 1d)
    }
}