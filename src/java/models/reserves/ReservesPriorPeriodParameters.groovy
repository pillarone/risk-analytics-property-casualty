package models.reserves

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

model=models.reserves.ReservesModel
periodCount=3
displayName='Prior Period'
components {
	claimsGenerators {
		subMotorAttritional {
			parmAssociateExposureInfo[1]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[2]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[2]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmClaimsModel[1]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[2]=0.8
			parmPeriodPaymentPortion[1]=0.8
			parmPeriodPaymentPortion[0]=0.8
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[2]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[1]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subMotorSingle {
			parmAssociateExposureInfo[1]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[2]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[2]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:5.0]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:50.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:5.0]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:50.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
			parmClaimsModel[1]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:5.0]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:50.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
			parmPeriodPaymentPortion[1]=0.7
			parmPeriodPaymentPortion[2]=0.7
			parmPeriodPaymentPortion[0]=0.7
			parmUnderwritingInformation[2]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[1]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
	}
    reinsurance {
        subQuotaShare {
            parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.4,"coveredByReinsurer":1.0,])
            parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.0])
            parmContractStrategy[2]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.2,"coveredByReinsurer":1.0,])
            parmCommissionStrategy[2] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.0])
            parmContractStrategy[1]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.6,"coveredByReinsurer":1.0,])
            parmCommissionStrategy[1] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.0])
            //todo (sha) the following lines need to be corrected by sku
//            parmCoveredLines[0]=new ComboBoxTableMultiDimensionalParameter([""],["Covered Lines"], LobMarker)
//            parmCoveredLines[2]=new ComboBoxTableMultiDimensionalParameter([""],["Covered Lines"], LobMarker)
//            parmCoveredLines[1]=new ComboBoxTableMultiDimensionalParameter([""],["Covered Lines"], LobMarker)
//            parmAppliedOnPerils[1]=new ComboBoxTableMultiDimensionalParameter([""],["perils"], PerilMarker)
//            parmAppliedOnPerils[2]=new ComboBoxTableMultiDimensionalParameter([""],["perils"], PerilMarker)
//            parmAppliedOnPerils[0]=new ComboBoxTableMultiDimensionalParameter([""],["perils"], PerilMarker)
            parmInuringPriority[0]=0
            parmInuringPriority[2]=0
            parmInuringPriority[1]=0
        }
    }
	reserveGenerators {
		subMotor {
			parmDistribution[0]=DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1.0])
			parmDistribution[2]=DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1.0])
			parmDistribution[1]=DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1.0])
			parmInitialReserves[2]=1000.0
			parmInitialReserves[1]=1000.0
			parmInitialReserves[0]=1000.0
			parmModification[2]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
			parmModification[1]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
			parmModification[0]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
			parmPeriodPaymentPortion[0]=0.8
			parmPeriodPaymentPortion[2]=0.8
			parmPeriodPaymentPortion[1]=0.8
			parmReservesModel[2]=ReservesGeneratorStrategyType.getStrategy(ReservesGeneratorStrategyType.PRIOR_PERIOD, ["basedOnClaimsGenerators":new ComboBoxTableMultiDimensionalParameter(["motor attritional","motor single"],["Claims Generators"], PerilMarker),])
			parmReservesModel[0]=ReservesGeneratorStrategyType.getStrategy(ReservesGeneratorStrategyType.PRIOR_PERIOD, ["basedOnClaimsGenerators":new ComboBoxTableMultiDimensionalParameter(["motor attritional","motor single"],["Claims Generators"], PerilMarker),])
			parmReservesModel[1]=ReservesGeneratorStrategyType.getStrategy(ReservesGeneratorStrategyType.PRIOR_PERIOD, ["basedOnClaimsGenerators":new ComboBoxTableMultiDimensionalParameter(["motor attritional","motor single"],["Claims Generators"], PerilMarker),])
		}
	}
}
