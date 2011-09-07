package models.podraFac

model=models.podraFac.PodraFacModel
periodCount=1
displayName='Test Example'
applicationVersion='1.4-BETA-5'
components {
	almGenerators {
		subLoans {
			parmAssetLiabilityMismatchModel[0]=org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.AssetLiabilityMismatchGeneratorStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.AssetLiabilityMismatchGeneratorStrategyType.RESULTRELATIVETOINITIALVOLUME, [:])
			parmDistribution[0]=org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:100000.0, stDev:20000.0])
			parmInitialVolume[0]=0.0
			parmModification[0]=org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
		}
	}
	claimsGenerators {
		subMotorHullAttritional {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.RISKTOBAND, ["allocationBase":org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited.PREMIUM,])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:0.8, stDev:0.054]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[0]=0.8
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subMotorHullSingle {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.RISKTOBAND, ["allocationBase":org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited.PREMIUM,])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda:1.0]),"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha:0.523, beta:500000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.TRUNCATED, ["min":500000.0,"max":2.0E7,]),"produceClaim":org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType.SINGLE,])
			parmPeriodPaymentPortion[0]=0.6
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
	}
	dependencies {
		subAttritional {
			parmCopulaStrategy[0]=org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory.getCopulaStrategy(org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType.NORMAL, ["dependencyMatrix":new org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0, 0.25], [0.25, 1.0]]),["motor hull attritional","motor hull single"],org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker),])
		}
	}
	linesOfBusiness {
		subMotorHull {
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull attritional", "motor hull single"], [1.0, 1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"], [1.0]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
		}
	}
	treatyAllocationForFAC {
		subMotorHull {
			parmAllocation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[8.0E7, 1.5E8], [1.0, 2.0], [0.2, 0.21], [0.02, 0.03]]),["Lines Sum Insured","Count of Policies","Total Treaty","Retention"])
			parmLinkedUnderwritingInfo[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker, 'subMotorHull')
		}
	}
	reinsurance {
		subContracts {
			subQuotaShareMotorHull {
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.FIXEDCOMMISSION, ["commission":0.0,])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5,"limit":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.NONE, [:]),"coveredByReinsurer":1.0,])
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.PERILS, ["perils":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull attritional", "motor hull single"]]),["Covered Perils"], org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker),])
				parmInuringPriority[0]=0
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
		}
	}
	reserveGenerators {
		subMotorHull {
			parmDistribution[0]=org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:100.0, stDev:10.0])
			parmInitialReserves[0]=200.0
			parmModification[0]=org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
			parmPeriodPaymentPortion[0]=0.8
			parmReservesModel[0]=org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.INITIAL_RESERVES, ["basedOnClaimsGenerators":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull attritional", "motor hull single"]]),["Claims Generators"], org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker),])
		}
	}
	treatyAllocationForFAC {
		subMotorHull {
			parmAllocation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[2.0E8, 4.0E8, 4.0E8], [1.0, 2.0, 3.0], [0.2, 0.21, 0.25], [0.4, 0.5, 0.55], [0.02, 0.03, 0.3]]),["Max Sum Insured","Count of Policies","Quota Share %","Surplus %","Retention %"])
			parmLinkedUnderwritingInfo[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker, 'subMotorHull')
		}
	}
	underwritingSegments {
		subMotorHull {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[2.0E8, 4.0E8], [8.0E7, 1.5E8], [9.7014E7, 9.8E7], [20.0, 8.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
	}
}
comments=[]
tags=[]
