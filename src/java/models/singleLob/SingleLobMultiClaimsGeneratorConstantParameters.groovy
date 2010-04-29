package models.singleLob

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

model = models.singleLob.SingleLobMultiClaimsGeneratorModel
periodCount = 1
displayName = 'Constant Generators'
components {
    mtpl {
        subClaimsGenerator {
            subSingleClaimsGenerator4 {
                subClaimsGenerator {
                    parmBase[0] = Exposure.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, [stDev: 145.0, mean: 25.0])
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 10.0])
                }
            }
            subSingleClaimsGenerator2 {
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean: 125.0, stDev: 0.69])
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmBase[0] = Exposure.ABSOLUTE
                }
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 3.67])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
            }
            subSingleClaimsGenerator6 {
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 123.0])
                }
                subClaimsGenerator {
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, [stDev: 0.36, mean: 25.0])
                    parmBase[0] = Exposure.ABSOLUTE
                }
            }
            subSingleClaimsGenerator1 {
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, [stDev: 10.0, mean: 10000.0])
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmBase[0] = Exposure.ABSOLUTE
                }
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 1.25])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
            }
            subSingleClaimsGenerator7 {
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.0])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
            }
            subSingleClaimsGenerator3 {
                subClaimsGenerator {
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, [stDev: 1.0, mean: 10.0])
                    parmBase[0] = Exposure.ABSOLUTE
                }
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 4.256])
                }
            }
            subSingleClaimsGenerator5 {
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 65.0])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean: 25.0, stDev: 145.0])
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
        subUnderwriting {
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[0.0], [0.0], [0.0], [0.0], [''], ['']], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
        }
        subRiProgram {
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
            subContract5 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract4 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
}
