package models.finiteRe

import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker

model = models.finiteRe.FiniteReModel
periodCount = 3
allPeriods = 0..<periodCount
displayName = 'Example EAB'
periodLabels = ['2009', '2010', '2011']
components {
    claimsGenerators {
        subHull {
			parmClaimsModel[allPeriods]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [mean:3000000.0, stDev:6000000.0]),"claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
		}
		subMotor {
			parmClaimsModel[allPeriods]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [mean:4000000.0, stDev:7000000.0]),"claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subPropertyCat {
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmClaimsModel[allPeriods]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":RandomDistributionFactory.getDistribution(DistributionType.POISSON, [lambda:2.0]),"frequencyModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [mean:6000000.0, stDev:6000000.0]),"claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.AGGREGATED_EVENT,])
		}
		subMarine {
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[allPeriods]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [mean:6000000.0, stDev:1.2E7]),"claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
		}
		subPropertyAttritional {
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
			parmClaimsModel[allPeriods]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [mean:4000000.0, stDev:4000000.0]),"claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
		}    }
    finiteRe {
        parmFractionExperienceAccount[allPeriods] = 0.75
        parmPremium[allPeriods] = 3.5E6
    }
    wholeAccountStopLoss {
        parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(
                ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 2E7, "limit": 3E7, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
        parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
        parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["perils"], PerilMarker)
        parmInuringPriority[allPeriods] = 0
    }
    lineOfBusinessReinsurance {
        subPropertyStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["property attritional", "property cat"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
        subMotorStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["motor"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
        subMarineStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["marine"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
        subHullStopLoss {
            parmContractStrategy[allPeriods] = ReinsuranceContractStrategyFactory.getContractStrategy(
                    ReinsuranceContractType.STOPLOSS, ["attachmentPoint": 1E7, "limit": 2E7, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0, "coveredByReinsurer": 1d])
            parmCoveredLines[allPeriods] = new ComboBoxTableMultiDimensionalParameter([""], ["Covered Lines"], LobMarker)
            parmCoveredPerils[allPeriods] = new ComboBoxTableMultiDimensionalParameter(["hull"], ["perils"], PerilMarker)
            parmInuringPriority[allPeriods] = 0
        }
    }
}
