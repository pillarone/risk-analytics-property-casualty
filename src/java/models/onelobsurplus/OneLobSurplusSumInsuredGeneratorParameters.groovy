package models.onelobsurplus

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

model=models.onelobsurplus.OneLobSurplusModel
periodCount=2
allPeriods=0..1
components {
	underwriting {
		parmUnderwritingInformation[0]=new TableMultiDimensionalParameter([[1000.0,10000.0,100000.0],[800.0,8000.0,80000.0],[50000.0,50000.0,50000.0],[1000,100,10],[''],['']],["maximum sum insured","average sum insured","premium","number of policies/risks","custom allocation number of single claims","custom allocation attritional claims"])
		parmUnderwritingInformation[1]=new TableMultiDimensionalParameter([[1000.0,10000.0,100000.0],[800.0,8000.0,80000.0],[50000.0,50000.0,50000.0],[1000,100,10],[''],['']],["maximum sum insured","average sum insured","premium","number of policies/risks","custom allocation number of single claims","custom allocation attritional claims"])
		parmAllocationBaseAttritionalClaims[0]=RiskBandAllocationBase.PREMIUM
		parmAllocationBaseAttritionalClaims[1]=RiskBandAllocationBase.PREMIUM
		parmAllocationBaseSingleClaims[0]=RiskBandAllocationBase.NUMBER_OF_POLICIES
		parmAllocationBaseSingleClaims[1]=RiskBandAllocationBase.NUMBER_OF_POLICIES
	}
	frequencyGenerator {
		parmDistribution[0]=DistributionType.getStrategy(DistributionType.POISSON, ["lambda":1.0])
		parmDistribution[1]=DistributionType.getStrategy(DistributionType.POISSON, ["lambda":1.0])
		parmBase[0]=FrequencyBase.ABSOLUTE
		parmBase[1]=FrequencyBase.ABSOLUTE
	}
	singleClaimsGenerator {
		parmDistribution[0]=DistributionType.getStrategy(DistributionType.PARETO, ["beta":5000.0, "alpha":1.2])
		parmDistribution[1]=DistributionType.getStrategy(DistributionType.PARETO, ["beta":5000.0, "alpha":1.2])
		parmBase[0]=Exposure.ABSOLUTE
		parmBase[1]=Exposure.ABSOLUTE
		parmModification[0]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
		parmModification[1]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
	}
	attritionalClaimsGenerator {
		parmDistribution[0]=DistributionType.getStrategy(DistributionType.NORMAL, ["stDev":5000.0, "mean":100000.0])
		parmDistribution[1]=DistributionType.getStrategy(DistributionType.NORMAL, ["stDev":5000.0, "mean":100000.0])
		parmBase[0]=Exposure.ABSOLUTE
		parmBase[1]=Exposure.ABSOLUTE
		parmModification[0]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
		parmModification[1]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
	}
	claimsAllocator {
		parmRiskAllocatorStrategy[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.SUMINSUREDGENERATOR, [
                distribution: DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 0d, "stDev": 1d]),
                modification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                bandMean: 1d/3d])
		parmRiskAllocatorStrategy[1]=RiskAllocatorType.getStrategy(RiskAllocatorType.SUMINSUREDGENERATOR, [
                distribution: DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 0d, "stDev": 1d]),
                modification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                bandMean: 1d/3d])
	}
    quotaShare {
		parmContractStrategy[allPeriods]= new QuotaShareContractStrategy("quotaShare":0.2, "coveredByReinsurer": 1d)
        parmCommissionStrategy[allPeriods] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
	}
    surplus {
		parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.SURPLUS, ["retention":2000.0,"lines":4,"defaultCededLossShare":0.0,"coveredByReinsurer":1.0,])
        parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
		parmContractStrategy[1]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.SURPLUS, ["retention":2000.0,"lines":4,"defaultCededLossShare":0.0,"coveredByReinsurer":1.0,])
        parmCommissionStrategy[1] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
		parmInuringPriority[0]=0
		parmInuringPriority[1]=0
	}
	wxl {
		parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase":PremiumBase.ABSOLUTE,"premium":1000.0,"reinstatementPremiums":new TableMultiDimensionalParameter([0.5],["Reinstatement Premium"]),"attachmentPoint":2000.0,"limit":20000.0,"aggregateLimit":100000.0,"coveredByReinsurer":1.0,])
		parmContractStrategy[1]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase":PremiumBase.ABSOLUTE,"premium":1000.0,"reinstatementPremiums":new TableMultiDimensionalParameter([0.5],["Reinstatement Premium"]),"attachmentPoint":2000.0,"limit":20000.0,"aggregateLimit":100000.0,"coveredByReinsurer":1.0,])
		parmInuringPriority[0]=0
		parmInuringPriority[1]=0
	}
}
