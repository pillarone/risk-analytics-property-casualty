package models.exampleCompany

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

model = models.exampleCompany.ExampleCompanyModel
periodCount = 2
allPeriods = 0..1
applicationVersion='1.1.1'
components {
    mtpl {
        subUnderwriting {
           parmUnderwritingInformation[allPeriods] = new TableMultiDimensionalParameter(
                [[1000d, 10000d, 100000d], [800d, 8000d, 80000d], [50000d, 70000d, 90000d], [1000, 200, 30]],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks']
                )
            parmAllocationBaseAttritionalClaims[allPeriods] = RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[allPeriods] = RiskBandAllocationBase.NUMBER_OF_POLICIES
        }
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmBase[1] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 1.0, "mean": 10.0])
                    parmDistribution[1] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 1.0, "mean": 100.0])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmBase[1] = Exposure.ABSOLUTE
                }
            }
            subAttritionalClaimsGenerator {
                parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 20, "mean": 100])
                parmBase[0] = Exposure.ABSOLUTE
                parmBase[1] = Exposure.ABSOLUTE
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[allPeriods] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.05])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[1] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.05])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 210.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 210.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
        }
    }
    motorHull {
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmBase[1] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["stDev": 10, "mean": 5])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmBase[1] = Exposure.ABSOLUTE
                }
            }
            subAttritionalClaimsGenerator {
                parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 20, "mean": 100])
                parmBase[0] = Exposure.ABSOLUTE
                parmBase[1] = Exposure.ABSOLUTE
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[allPeriods] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[1] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 200.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 200.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
        }
        subUnderwriting {
            parmUnderwritingInformation[allPeriods] = new TableMultiDimensionalParameter(
                [[1000d, 10000d, 100000d], [800d, 8000d, 80000d], [50000d, 70000d, 90000d], [1000, 200, 30]],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks']
                )
        }
    }
    personalAccident {
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmBase[1] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["stDev": 10, "mean": 5])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmBase[1] = Exposure.ABSOLUTE
                }
            }
            subAttritionalClaimsGenerator {
                parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 20, "mean": 100])
                parmBase[0] = Exposure.ABSOLUTE
                parmBase[1] = Exposure.ABSOLUTE
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[allPeriods] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[1] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 200.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 200.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
        }
        subUnderwriting {
            parmUnderwritingInformation[allPeriods] = new TableMultiDimensionalParameter(
                [[1000d, 10000d, 100000d], [800d, 8000d, 80000d], [50000d, 70000d, 90000d], [1000, 200, 30]],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks']
                )
        }
    }
    property {
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmBase[1] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["stDev": 10, "mean": 5])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmBase[1] = Exposure.ABSOLUTE
                }
            }
            subAttritionalClaimsGenerator {
                parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 20, "mean": 100])
                parmBase[0] = Exposure.ABSOLUTE
                parmBase[1] = Exposure.ABSOLUTE
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[allPeriods] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["commission": 0.0, "quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[1] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 200.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 50.0, "aggregateLimit": 200.0, "attachmentPoint": 80.0, "premium": 70.0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmContractStrategy[1] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit": 1000.0, "attachmentPoint": 800.0, "premium": 800.0, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
                parmInuringPriority[1] = 0
            }
        }
        subUnderwriting {
            parmUnderwritingInformation[allPeriods] = new TableMultiDimensionalParameter(
                [[1000d, 10000d, 100000d], [800d, 8000d, 80000d], [50000d, 70000d, 90000d], [1000, 200, 30]],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks']
                )
            parmAllocationBaseAttritionalClaims[allPeriods] = RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[allPeriods] = RiskBandAllocationBase.NUMBER_OF_POLICIES
        }
    }
}
