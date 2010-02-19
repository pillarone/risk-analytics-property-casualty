package models.profiling

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType




model=models.profiling.ExampleLobModel
periodCount=1
allPeriods=0..0

components {
	mtpl {
		subUnderwriting {
			parmUnderwritingInformation[0] = new TableMultiDimensionalParameter(
                    [[1000d, 10000d, 100000d], [800d, 8000d, 80000d], [50000d, 70000d, 90000d], [1000, 200, 30], [''], ['']],
                    ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks',
                    'custom allocation number of single claims', 'custom allocation attritional claims'],
                    )
                parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
                parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
		}
		subClaimsGenerator {
			subSingleClaimsGenerator {
				subFrequencyGenerator {
                    parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 4.874])
					parmBase[0]=FrequencyBase.ABSOLUTE
				}
				subClaimsGenerator {
                    parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ["beta":1000000.0, "alpha":1.416])
                    parmModification[allPeriods] = DistributionModifierFactory.getModifier(DistributionModifier.CENSORED, ["min":1000000.0, "max":100000000.0])
                    parmBase[0]=Exposure.ABSOLUTE
				}
			}
			subAttritionalClaimsGenerator {
                parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.DISCRETEEMPIRICAL, ["discreteEmpiricalValues":new TableMultiDimensionalParameter([[100.0,200.0],[0.5,0.5]],["observations","probabilities"])])
				parmBase[0]=Exposure.ABSOLUTE
			}
		}
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
        }
        subRiProgram {
			subContract1 {
				parmContractStrategy[0]=ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5, "coveredByReinsurer": 1d])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
				parmInuringPriority[0]=0
			}
			subContract2 {
				parmContractStrategy[0]=ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.WXL, ["reinstatementPremiums":new TableMultiDimensionalParameter([0.5],["Reinstatement Premium"]),"limit":9.9E7,"aggregateLimit":9.223372036854776E18,"attachmentPoint":1000000.0,"premium":1.0E7,"premiumBase":PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
				parmInuringPriority[0]=0
			}
			subContract3 {
				parmContractStrategy[0]=ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.TRIVIAL, [:])
				parmInuringPriority[0]=0
			}
		}
	}
}
