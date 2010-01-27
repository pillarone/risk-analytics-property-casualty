package models.capitalEagle

import models.capitalEagle.CapitalEagleModel
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase

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
                    parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(DistributionType.POISSON, ["lambda": 4.874])
                    parmBase[allPeriods] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(DistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
                    parmModification[allPeriods] = DistributionModifierFactory.getModifier(DistributionModifier.CENSORED, ["min": 1000000.0, "max": 100000000.0])
                    parmBase[allPeriods] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                }
            }
            subAttritionalClaimsGenerator {
                parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, ["stDev": 0.07, "mean": 0.823])
                parmBase[allPeriods] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN
                parmModification[allPeriods] = DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[allPeriods] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "commission": 0.167, "coveredByReinsurer": 1d])
                parmInuringPriority[allPeriods] = 0
            }
            subContract2 {
                parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.WXL, ["premiumBase": PremiumBase.GNPI, "premium": 0.0573, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 1000000.0, "limit": 9.9E7, "aggregateLimit": 9.9E8, "coveredByReinsurer": 1d])
                parmInuringPriority[allPeriods] = 0
            }
            subContract3 {
                parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[allPeriods] = 0
            }
        }
    }
}