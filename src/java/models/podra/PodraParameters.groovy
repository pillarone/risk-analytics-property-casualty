package models.podra

model=models.podra.PodraModel
periodCount=1
displayName='One Line Example'
applicationVersion='1.1.3'
components {
	reserveGenerators {
		subMotorHull {
			parmReservesModel[0]=org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.INITIAL_RESERVES, ["basedOnClaimsGenerators":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotorHullAttritional", "subMotorHullSingle"]]),["Claims Generators"], org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker),])
			parmModification[0]=org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
			parmInitialReserves[0]=200.0
			parmDistribution[0]=org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:100.0, stDev:10.0])
			parmPeriodPaymentPortion[0]=0.8
		}
	}
	claimsGenerators {
		subMotorHullSingle {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda:1.0]),"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha:0.523, beta:500000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.TRUNCATED, ["min":500000.0,"max":2.0E7,]),"produceClaim":org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType.SINGLE,])
			parmPeriodPaymentPortion[0]=0.6
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotorHull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
		}
		subMotorHullAttritional {
			parmPeriodPaymentPortion[0]=0.8
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotorHull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:0.8, stDev:0.054]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
		}
	}
	linesOfBusiness {
		subMotorHull {
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotorHull"], [1.0]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotorHullAttritional", "subMotorHullSingle"], [1.0, 1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotorHull"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
		}
	}
	reinsurance {
		subContracts {
			subQuotaShareMotorHull {
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.FIXEDCOMMISSION, ["commission":0.0,])
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.PERILS, ["perils":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotorHullAttritional", "subMotorHullSingle"]]),["Covered Perils"], org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker),])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5,"limit":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.NONE, [:]),"coveredByReinsurer":1.0,])
				parmInuringPriority[0]=0
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
		}
	}
	dependencies {
		subAttritional {
			parmCopulaStrategy[0]=org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory.getCopulaStrategy(org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType.NORMAL, ["dependencyMatrix":new org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0, 0.25], [0.25, 1.0]]),["subMotorHullAttritional","subMotorHullSingle"],org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker),])
		}
	}
	almGenerators {
		subLoans {
			parmInitialVolume[0]=0.0
			parmModification[0]=org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
			parmDistribution[0]=org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:100000.0, stDev:20000.0])
			parmAssetLiabilityMismatchModel[0]=org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.AssetLiabilityMismatchGeneratorStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.AssetLiabilityMismatchGeneratorStrategyType.RESULTRELATIVETOINITIALVOLUME, [:])
		}
	}
	underwritingSegments {
		subMotorHull {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [9.7014E7], [0.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
	}
}
comments=[]
