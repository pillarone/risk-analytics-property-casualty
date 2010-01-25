package models.test

import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory


displayName = 'ImportTest'
model = models.test.StructureTestModel
periodCount = 1
allPeriods = 0..0
components {
    mtpl {
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmDistribution[0] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["stDev": 20.0, "mean": 100.0])
                parmModification[0] = DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
                parmBase[0] = Exposure.ABSOLUTE
            }
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 10])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.LOGNORMAL, ["stDev": 10, "mean": 5])
                    parmModification[0] = DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
                    parmBase[0] = Exposure.ABSOLUTE
                }
            }
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["commission": 0.1, "quotaShare": 0.1, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
            }
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["commission": 0.1, "quotaShare": 0.2, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["commission": 0.1, "quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmInuringPriority[0] = 0
            }
        }
        subUnderwriting {
            parmPricePerExposureUnit[0] = 100
            parmWrittenExposure[0] = 1000
            parmExposureDefinition[0] = Exposure.ABSOLUTE
        }
    }
}

