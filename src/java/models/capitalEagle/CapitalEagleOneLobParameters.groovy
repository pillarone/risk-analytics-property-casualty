package models.capitalEagle

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

model = models.capitalEagle.CapitalEagleOneLobModel
displayName = 'QS and NP Cover'
periodCount = 1
allPeriods = 0..0

components {
    mtpl {
        subUnderwriting {
            parmUnderwritingInformation[allPeriods] = new TableMultiDimensionalParameter(
                    [[0], [0], [189242000], [0], [0], [0]],
                    ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks',
                            'custom allocation number of single claims', 'custom allocation attritional claims'],
            )
            parmAllocationBaseAttritionalClaims[allPeriods] = RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[allPeriods] = RiskBandAllocationBase.NUMBER_OF_POLICIES
        }
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.POISSON, ["lambda": 4.874])
                    parmBase[allPeriods] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
                    parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 1000000.0, "max": 100000000.0])
                    parmBase[allPeriods] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                }
            }
            subAttritionalClaimsGenerator {
                parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["stDev": 0.07, "mean": 0.823])
                parmBase[allPeriods] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN
                parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[allPeriods] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0, "limit": LimitStrategyType.noLimit])
                parmCommissionStrategy[allPeriods] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.167])
                parmInuringPriority[allPeriods] = 0
            }
            subContract2 {
                parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.0573, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 1000000.0, "limit": 9.9E7, "aggregateLimit": 9.9E8, "coveredByReinsurer": 1d])
                parmInuringPriority[allPeriods] = 0
            }
            subContract3 {
                parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[allPeriods] = 0
            }
        }
    }
}