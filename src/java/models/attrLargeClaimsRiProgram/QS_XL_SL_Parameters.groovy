package models.attrLargeClaimsRiProgram

import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.StopLossContractStrategy

model = QS_XL_SL_Model
periodCount = 4
allPeriods = 0..<periodCount

components {
    frequencyGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 1])
        parmBase[allPeriods] = FrequencyBase.ABSOLUTE
    }
    claimsGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 100, "stDev": 30])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
    attritionalClaimsGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 300, "stDev": 20])
    }
    quotaShare {
        parmContractStrategy[allPeriods] = new QuotaShareContractStrategy("quotaShare": 0.3, "commission": 0d, "coveredByReinsurer": 1d)
    }
    wxl {
        parmContractStrategy[allPeriods] = new WXLContractStrategy(
                "attachmentPoint": 80, "limit": 50, "aggregateLimit": 200, "coveredByReinsurer": 1d,
                "premiumBase": PremiumBase.ABSOLUTE, "premium": 70, "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']))
    }
    stopLoss {
        parmContractStrategy[allPeriods] = new StopLossContractStrategy(
                "attachmentPoint": 800, "limit": 1000, "premiumBase": PremiumBase.ABSOLUTE, "premium": 800, "coveredByReinsurer": 1d)
    }
}
