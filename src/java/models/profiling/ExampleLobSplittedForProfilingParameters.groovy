package models.profiling

import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase


model=models.profiling.ExampleLobSplittedForProfilingModel
periodCount=1
allPeriods=0..0

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
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 4.874])
        parmBase[0]=FrequencyBase.ABSOLUTE
    }
    singleClaimsGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ["beta":1000000.0, "alpha":1.416])
        parmModification[allPeriods] = DistributionModifierFactory.getModifier(DistributionModifier.CENSORED, ["min":1000000.0, "max":100000000.0])
        parmBase[0]=Exposure.ABSOLUTE
    }
    attritionalClaimsGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.DISCRETEEMPIRICAL, ["discreteEmpiricalValues":new TableMultiDimensionalParameter([[100.0,200.0],[0.5,0.5]],["observations","probabilities"])])
        parmBase[0]=Exposure.ABSOLUTE
    }
    contract1 {
        parmContractStrategy[0]=ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["commission":0.0,"quotaShare":0.5, "coveredByReinsurer": 1d])
        parmInuringPriority[0]=0
    }
    contract2 {
        parmContractStrategy[0]=ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.WXL, ["reinstatementPremiums":new TableMultiDimensionalParameter([0.5],["Reinstatement Premium"]),"limit":9.9E7,"aggregateLimit":9.223372036854776E18,"attachmentPoint":1000000.0,"premium":1.0E7,"premiumBase":PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
        parmInuringPriority[0]=0
    }
    contract3 {
        parmContractStrategy[0]=ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.TRIVIAL, [:])
        parmInuringPriority[0]=0
    }
}