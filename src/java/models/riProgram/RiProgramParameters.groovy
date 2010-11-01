package models.riProgram

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

model = models.riProgram.RiProgramModel
periodCount = 2
allPeriods = 0..1
applicationVersion='1.1.1'
components {
    claimsGenerator {
        subSingleClaimsGenerator {
            subFrequencyGenerator {
                parmDistribution[0] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10])
                parmDistribution[1] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 12])
                parmBase[0] = FrequencyBase.ABSOLUTE
                parmBase[1] = FrequencyBase.ABSOLUTE
            }
            subClaimsGenerator {
                parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10])
                parmBase[0] = Exposure.ABSOLUTE
                parmBase[1] = Exposure.ABSOLUTE
            }
        }
        subAttritionalClaimsGenerator {
            parmDistribution[0] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 20, "mean": 100])
            parmDistribution[1] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 25, "mean": 200])
            parmBase[0] = Exposure.ABSOLUTE
            parmBase[1] = Exposure.ABSOLUTE
        }
    }
    reinsuranceProgram {
        subContract0 {
            parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
            parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
            parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
            parmCommissionStrategy[1] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
            parmInuringPriority[0] = 1
            parmInuringPriority[1] = 1
        }
        subContract1 {
            parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"limit": 100.0, "aggregateLimit": 3000.0, "attachmentPoint": 100.0, "premium": 70.0, "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']), "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
            parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"limit": 100.0, "aggregateLimit": 3000.0, "attachmentPoint": 100.0, "premium": 70.0, "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']), "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
            parmInuringPriority[0] = 2
            parmInuringPriority[1] = 2
        }
        subContract2 {
            parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
            parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
            parmInuringPriority[0] = 3
            parmInuringPriority[1] = 3
        }
    }
}