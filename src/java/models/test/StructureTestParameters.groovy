package models.test

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

model = models.test.StructureTestModel
periodCount = 1
allPeriods = 0..0
components {
    dateComponent {
        parmStartDate[0] = new org.joda.time.DateTime(1972, 6, 14, 0, 0, 0, 0)
    }
    mtpl {
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmDistribution[0] = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["stDev": 20.0, "mean": 100.0])
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                parmBase[0] = Exposure.ABSOLUTE
            }
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["stDev": 10, "mean": 5])
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                    parmBase[0] = Exposure.ABSOLUTE
                }
            }
        }
        subRiProgram {
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.1, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.1])
                parmInuringPriority[0] = 0
            }
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.2, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.1])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.3, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.1])
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
