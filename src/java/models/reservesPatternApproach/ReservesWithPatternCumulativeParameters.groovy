package models.reservesPatternApproach

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.HistoricClaimsStrategyType
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.PatternStrategyType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

model=models.reservesPatternApproach.ReservesWithPatternModel
periodCount=4
displayName='Cumulative Patterns'
periodLabels=["2009","2010","2011","2012"]
components {
	claimDevelopment {
		parmReportedPattern[1]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
		parmReportedPattern[3]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
		parmReportedPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.8,0.9,0.95,1.0],["Cumulative"]),])
		parmReportedPattern[2]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
		parmPayoutPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new TableMultiDimensionalParameter([0.4,0.7,0.9,1.0],["Cumulative"]),])
		parmPayoutPattern[1]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
		parmPayoutPattern[3]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
		parmPayoutPattern[2]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
		parmHistoricClaims[0]=HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.NONE, [:])
		parmHistoricClaims[2]=HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.NONE, [:])
		parmHistoricClaims[1]=HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.NONE, [:])
		parmHistoricClaims[3]=HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.NONE, [:])
	}
	claimsGenerators {
		subAttritional {
			parmAssociateExposureInfo[2]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[1]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[3]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[2]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:0.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmClaimsModel[1]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:0.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmClaimsModel[3]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:0.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmUnderwritingInformation[2]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[3]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[1]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subSingle {
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[3]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[2]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmAssociateExposureInfo[1]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:2.0]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
			parmClaimsModel[1]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:0.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmClaimsModel[2]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:0.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmClaimsModel[3]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:0.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[3]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[2]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmUnderwritingInformation[1]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
	}
}
