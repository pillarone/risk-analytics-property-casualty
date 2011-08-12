package models.podra

model=models.podra.PodraModel
periodCount=1
displayName='Drill Down Test Parameter'
applicationVersion='1.4-ALPHA-7-kti'
components {
	claimsGenerators {
		subMotorHullAttritional {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subMotorHullSingle {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:1.0]),"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:1000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"produceClaim":org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType.SINGLE,])
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subPropertyAttritional {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:200.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["property"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subPropertyEarthquake {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:1.0]),"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:500.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"produceClaim":org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType.AGGREGATED_EVENT,])
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["property"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subPropertySingle {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:1.0]),"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:900.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"produceClaim":org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType.SINGLE,])
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["property"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
	}
	linesOfBusiness {
		subMotorHull {
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull attritional", "motor hull single"], [1.0, 1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull reserves"], [1.0]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
		}
		subProperty {
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["property single", "property attritional", "property earthquake"], [1.0, 1.0, 1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["property reserves"], [1.0]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["property"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
		}
	}
	reinsurance {
		subContracts {
			subMotorHullWxl {
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.FIXEDCOMMISSION, ["commission":0.0,])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.WXL, ["premiumBase":org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.ABSOLUTE,"premium":200.0,"premiumAllocation":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]),["Reinstatement Premium"]),"attachmentPoint":500.0,"limit":200.0,"aggregateDeductible":0.0,"aggregateLimit":2200.0,"coveredByReinsurer":1.0,])
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"]]),["Covered Segments"], org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker),])
				parmInuringPriority[0]=1
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
			subPropertyCxl {
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.FIXEDCOMMISSION, ["commission":0.2,])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.CXL, ["premiumBase":org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.ABSOLUTE,"premium":100.0,"premiumAllocation":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0]]),["Reinstatement Premium"]),"attachmentPoint":300.0,"limit":50.0,"aggregateDeductible":0.0,"aggregateLimit":100.0,"coveredByReinsurer":1.0,])
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["property"]]),["Covered Segments"], org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker),])
				parmInuringPriority[0]=1
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
			subPropertyQuotaShare {
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.FIXEDCOMMISSION, ["commission":0.1,])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.2,"limit":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.NONE, [:]),"coveredByReinsurer":1.0,])
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.ALL, ["reserves":org.pillarone.riskanalytics.domain.pc.constants.IncludeType.NOTINCLUDED,])
				parmInuringPriority[0]=0
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
		}
	}
	structures {
		subFrance {
			parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull", "property"], [0.6, 0.3]]),["Segment","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEGMENT_PORTION'))
		}
		subSuisse {
			parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull", "property"], [0.4, 0.7]]),["Segment","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEGMENT_PORTION'))
		}
	}
	underwritingSegments {
		subMotorHull {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [8000.0], [1000.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
		subProperty {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0], [0], [6000], [10]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
	}
}
comments=[]
tags=[]
