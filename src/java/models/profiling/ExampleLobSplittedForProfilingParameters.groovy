package models.profiling

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

model=models.profiling.ExampleLobSplittedForProfilingModel
periodCount=1
allPeriods=0..0
applicationVersion='1.1.1'
components {
    underwriting {
        parmPricePerExposureUnit[0]=100
        parmPricePerExposureUnit[1]=100
        parmWrittenExposure[0]=1000
        parmWrittenExposure[1]=1000
        parmExposureDefinition[0]=Exposure.ABSOLUTE
        parmExposureDefinition[1]=Exposure.ABSOLUTE
    }
    frequencyGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        parmBase[0]=FrequencyBase.ABSOLUTE
    }
    singleClaimsGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["beta":1000000.0, "alpha":1.416])
        parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min":1000000.0, "max":100000000.0])
        parmBase[0]=Exposure.ABSOLUTE
    }
    attritionalClaimsGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(ClaimSizeDistributionType.DISCRETEEMPIRICAL, ["discreteEmpiricalValues":new TableMultiDimensionalParameter([[100.0,200.0],[0.5,0.5]],["observations","probabilities"])])
        parmBase[0]=Exposure.ABSOLUTE
    }
    contract1 {
        parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5, "coveredByReinsurer": 1d])
        parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
        parmInuringPriority[0]=0
    }
    contract2 {
        parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new TableMultiDimensionalParameter([0.5],["Reinstatement Premium"]),"limit":9.9E7,"aggregateLimit":9.223372036854776E18,"attachmentPoint":1000000.0,"premium":1.0E7,"premiumBase":PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
        parmInuringPriority[0]=0
    }
    contract3 {
        parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
        parmInuringPriority[0]=0
    }
}