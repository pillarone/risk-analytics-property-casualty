package models.attrLargeClaimsRiProgram

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.StopLossContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategy
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType
//import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType

model = QS_XL_SL_Model
periodCount = 4
allPeriods = 0..<periodCount

components {
    frequencyGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 1])
        parmBase[allPeriods] = FrequencyBase.ABSOLUTE
    }
    claimsGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 100, "stDev": 30])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
    attritionalClaimsGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 300, "stDev": 20])
    }
    quotaShare {
        parmContractStrategy[allPeriods] = new QuotaShareContractStrategy("quotaShare": 0.3, "coveredByReinsurer": 1d)
//        parmCommissionStrategy[allPeriods] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
    }
    wxl {
        parmContractStrategy[allPeriods] = new WXLContractStrategy(
                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]), "attachmentPoint": 80, "limit": 50, "aggregateLimit": 200, "coveredByReinsurer": 1d,
                "premiumBase": PremiumBase.ABSOLUTE, "premium": 70, "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']))
    }
    stopLoss {
        parmContractStrategy[allPeriods] = new StopLossContractStrategy(
                "attachmentPoint": 800, "limit": 1000, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "premium": 800, "coveredByReinsurer": 1d)
    }
}
