package models.finiteRe

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase

model = models.finiteRe.FiniteReModel
periodCount = 3
allPeriods = 0..<periodCount
displayName = 'Example EAB'
periodLabels = ['2009', '2010', '2011']
components {
    claimsGenerators {
        subHull {
			parmClaimsModel[allPeriods]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:3000000.0, stDev:6000000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
		}
		subMotor {
			parmClaimsModel[allPeriods]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:4000000.0, stDev:7000000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subPropertyCat {
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmClaimsModel[allPeriods]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.POISSON, [lambda:2.0]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:6000000.0, stDev:6000000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.AGGREGATED_EVENT,])
		}
		subMarine {
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[allPeriods]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:6000000.0, stDev:1.2E7]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
		}
		subPropertyAttritional {
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmClaimsModel[allPeriods]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:4000000.0, stDev:4000000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
		}    }
    finiteRe {
        parmFractionExperienceAccount[allPeriods] = 0.75
        parmPremium[allPeriods] = 3.5E6
    }
    wholeAccountStopLoss {
        parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 2E7, "limit": 3E7, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
        parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
        parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["perils"], PerilMarker)
        parmInuringPriority[allPeriods] = 0
    }
    lineOfBusinessReinsurance {
        subPropertyStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["property attritional", "property cat"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
        subMotorStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["motor"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
        subMarineStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["marine"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
        subHullStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["hull"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
    }
}
