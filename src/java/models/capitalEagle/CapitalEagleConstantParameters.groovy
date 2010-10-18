package models.capitalEagle

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

model = models.capitalEagle.CapitalEagleModel
periodCount = 1
displayName = 'One Reinsurance Program (constant)'
components {
    personalAccident {
        subRiProgram {
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.0178, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 200000.0, "limit": 2800000.0, "aggregateLimit": 8400000.0, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 0
            }
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0, "limit": LimitStrategyType.noLimit])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.4])
                parmInuringPriority[0] = 0
            }
            subContract5 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
            subContract4 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
        }
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                }
            }
            subAttritionalClaimsGenerator {
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                parmBase[0] = Exposure.ABSOLUTE
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subUnderwriting {
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[0], [0], [53906000], [0],[0],[0]], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
        }
    }
    mtpl {
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                }
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
            }
            subAttritionalClaimsGenerator {
                parmBase[0] = Exposure.ABSOLUTE
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
            }
        }
        subRiProgram {
            subContract3 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
            subContract5 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.0573, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 1000000.0, "limit": 9.9E7, "aggregateLimit": 9.9E8, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 0
            }
            subContract1 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0, "limit": LimitStrategyType.noLimit])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.167])
            }
            subContract4 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
        }
        subUnderwriting {
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[0], [0], [189242000], [0],[0],[0]], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
    property {
        subUnderwriting {
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[100000, 200000, 300000, 400000, 500000, 750000, 1000000, 1250000, 1500000], [78479, 167895, 265488, 378455, 445783, 645789, 835481, 1202547, 1363521], [8706931, 25985915, 22805313, 9315291, 5547769, 564420, 8021, 2886, 2454], [92455, 128979, 71583, 24614, 12445, 874, 12, 4, 3],[0]*9, [0]*9], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
        }
        subClaimsGenerator {
            subFloodGenerator {
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = Exposure.ABSOLUTE
                }
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                }
                subEventGenerator {
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                }
            }
            subStormGenerator {
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmBase[0] = Exposure.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                }
                subEventGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
            }
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                }
            }
            subAttritionalSeverityClaimsGenerator {
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                parmBase[0] = Exposure.ABSOLUTE
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
            }
            subEQGenerator {
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = Exposure.ABSOLUTE
                }
                subEventGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                }
            }
            subAttritionalFrequencyGenerator {
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1.0])
                parmBase[0] = FrequencyBase.ABSOLUTE
            }
        }
        subRiProgram {
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0, "limit": LimitStrategyType.noLimit])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.284])
                parmInuringPriority[0] = 0
            }
            subContract5 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
            subContract4 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
            subContract3 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["stopLossContractBase": StopLossContractBase.GNPI, "premium": 0.1207, "attachmentPoint": 3.3960398E7, "limit": 1.01881194E8, "coveredByReinsurer": 1.0,])
            }
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.0688, "reinstatementPremiums": new TableMultiDimensionalParameter([1.0, 1.0, 1.0], ["Reinstatement Premium"]), "attachmentPoint": 1000000.0, "limit": 1.4E7, "aggregateLimit": 5.6E7, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 0
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
    motorHull {
        subRiProgram {
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.043, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 500000.0, "limit": 1.95E7, "aggregateLimit": 1.95E8, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 0
            }
            subContract1 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0, "limit": LimitStrategyType.noLimit])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.189])
            }
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract5 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract4 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                parmBase[0] = Exposure.ABSOLUTE
            }
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmBase[0] = Exposure.ABSOLUTE
                }
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
            }
        }
        subUnderwriting {
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[0], [0], [97014000], [0],[0],[0]], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
}
