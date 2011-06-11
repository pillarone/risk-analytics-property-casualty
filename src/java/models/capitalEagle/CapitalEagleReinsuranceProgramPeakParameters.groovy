package models.capitalEagle

model = models.capitalEagle.CapitalEagleModel
periodCount = 1
displayName = 'CapitalEagle PEAK'
applicationVersion = '1.4-ALPHA-1.3.2'
components {
    motorHull {
        subAllocator {
            parmRiskAllocatorStrategy[0] = org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.NONE, [:])
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN
                parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean: 0.8, stDev: 0.054])
                parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
            }
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha: 0.523, beta: 500000.0])
                    parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.TRUNCATED, ["min": 500000.0, "max": 2.0E7,])
                }
                subFrequencyGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda: 1.0])
                }
            }
        }
        subRiProgram {
            subContract1 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.WXL, ["premiumBase": org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.GNPI, "premium": 0.01, "premiumAllocation": org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]), "reinstatementPremiums": new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]), ["Reinstatement Premium"]), "attachmentPoint": 1.0E7, "limit": 1.0E7, "aggregateDeductible": 0.0, "aggregateLimit": 1.0E8, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 1
            }
            subContract2 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.WXL, ["premiumBase": org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.GNPI, "premium": 0.0017, "premiumAllocation": org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]), "reinstatementPremiums": new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0, 1.0]]), ["Reinstatement Premium"]), "attachmentPoint": 2000000.0, "limit": 1000000.0, "aggregateDeductible": 0.0, "aggregateLimit": 3000000.0, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 1
            }
            subContract3 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract4 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract5 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
        }
        subUnderwriting {
            parmAllocationBaseAttritionalClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.CUSTOM
            parmUnderwritingInformation[0] = new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [9.7014E7], [0.0], [0.0], [0.0]]), ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
        }
    }
    mtpl {
        subAllocator {
            parmRiskAllocatorStrategy[0] = org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.NONE, [:])
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN
                parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean: 0.823, stDev: 0.07])
                parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
            }
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha: 1.416, beta: 1000000.0])
                    parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.CENSORED, ["min": 1000000.0, "max": 1.0E8,])
                }
                subFrequencyGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda: 4.874])
                }
            }
        }
        subRiProgram {
            subContract1 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.WXL, ["premiumBase": org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.GNPI, "premium": 0.0049, "premiumAllocation": org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]), "reinstatementPremiums": new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0]]), ["Reinstatement Premium"]), "attachmentPoint": 5000000.0, "limit": 9.5E7, "aggregateDeductible": 0.0, "aggregateLimit": 9.5E8, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 1
            }
            subContract2 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract4 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract5 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
        }
        subUnderwriting {
            parmAllocationBaseAttritionalClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.CUSTOM
            parmUnderwritingInformation[0] = new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [1.89242E8], [0.0], [0.0], [0.0]]), ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
        }
    }
    personalAccident {
        subAllocator {
            parmRiskAllocatorStrategy[0] = org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.NONE, [:])
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN
                parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean: 0.39, stDev: 0.09])
                parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
            }
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha: 1.1, beta: 200000.0])
                    parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.TRUNCATED, ["min": 200000.0, "max": 3000000.0,])
                }
                subFrequencyGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda: 0.5])
                }
            }
        }
        subRiProgram {
            subContract1 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.CXL, ["premiumBase": org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.GNPI, "premium": 0.0018, "premiumAllocation": org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]), "reinstatementPremiums": new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0]]), ["Reinstatement Premium"]), "attachmentPoint": 2000000.0, "limit": 1.0E7, "aggregateDeductible": 0.0, "aggregateLimit": 2.0E7, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 2
            }
            subContract2 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract4 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract5 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
        }
        subUnderwriting {
            parmAllocationBaseAttritionalClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmUnderwritingInformation[0] = new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [5.3906E7], [0.0], [0.0], [0.0]]), ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
        }
    }
    property {
        subAllocator {
            parmRiskAllocatorStrategy[0] = org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType.NONE, [:])
        }
        subClaimsGenerator {
            subAttritionalFrequencyGenerator {
                parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant: 0.0])
            }
            subAttritionalSeverityClaimsGenerator {
                parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN
                parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean: 0.563, stDev: 0.1137])
                parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
            }
            subEQGenerator {
                subClaimsGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.UNIFORM, [a: 0, b: 1])
                }
                subEventGenerator {
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.UNIFORM, [a: 0, b: 1])
                    parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant: 0.0])
                }
            }
            subFloodGenerator {
                subClaimsGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.UNIFORM, [a: 0, b: 1])
                }
                subEventGenerator {
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.UNIFORM, [a: 0, b: 1])
                    parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant: 0.0])
                }
            }
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha: 1.76, beta: 70000.0])
                    parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.TRUNCATED, ["min": 70000.0, "max": 1000000.0,])
                }
                subFrequencyGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda: 13.32])
                }
            }
            subStormGenerator {
                subClaimsGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.UNIFORM, [a: 0, b: 1])
                }
                subEventGenerator {
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.UNIFORM, [a: 0, b: 1])
                    parmModification[0] = org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[0] = org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE
                    parmDistribution[0] = org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant: 0.0])
                }
            }
        }
        subRiProgram {
            subContract1 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.CXL, ["premiumBase": org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.GNPI, "premium": 0.1287, "premiumAllocation": org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]), "reinstatementPremiums": new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0]]), ["Reinstatement Premium"]), "attachmentPoint": 1.0E7, "limit": 1.7E8, "aggregateDeductible": 0.0, "aggregateLimit": 3.4E8, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 0
            }
            subContract2 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract4 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract5 {
                parmCommissionStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmContractStrategy[0] = org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
        }
        subUnderwriting {
            parmAllocationBaseAttritionalClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[0] = org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmUnderwritingInformation[0] = new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [7.2939E7], [0.0], [0.0], [0.0]]), ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
        }
    }
}
comments = []