package models.podra

model=models.podra.PodraModel
periodCount=1
displayName='Surplus Example'
applicationVersion='1.2'
components {
	reinsurance {
		subContracts {
			subSurplus {
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.SURPLUS, ["retention":2000.0,"lines":4.0,"defaultCededLossShare":0.0,"coveredByReinsurer":1.0,])
				parmInuringPriority[0]=10
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.ALL, ["reserves":org.pillarone.riskanalytics.domain.pc.constants.IncludeType.NOTINCLUDED,])
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
			}
			subQuotaShare {
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
				parmInuringPriority[0]=0
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.ALL, ["reserves":org.pillarone.riskanalytics.domain.pc.constants.IncludeType.NOTINCLUDED,])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.2,"limit":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.NONE, [:]),"coveredByReinsurer":1.0,])
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
			subWXL {
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.WXL, ["premiumBase":org.pillarone.riskanalytics.domain.pc.constants.PremiumBase.ABSOLUTE,"premium":1000.0,"premiumAllocation":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.5, 0.5, 0.5, 0.5]]),["Reinstatement Premium"]),"attachmentPoint":2000.0,"limit":20000.0,"aggregateDeductible":0.0,"aggregateLimit":100000.0,"coveredByReinsurer":1.0,])
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.ALL, ["reserves":org.pillarone.riskanalytics.domain.pc.constants.IncludeType.NOTINCLUDED,])
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
				parmInuringPriority[0]=20
			}
		}
	}
	claimsGenerators {
		subMotorAttritional {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.RISKTOBAND, ["allocationBase":org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited.PREMIUM,])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.NORMAL, [mean:100000.0, stDev:5000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[0]=1.0
		}
		subMotorSingle {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda:1.0]),"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha:1.2, beta:5000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"produceClaim":org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType.SINGLE,])
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.RISKTOBAND, ["allocationBase":org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited.NUMBER_OF_POLICIES,])
		}
	}
	linesOfBusiness {
		subMotor {
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor attritional", "motor single"], [1.0, 1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
		}
	}
	underwritingSegments {
		subMotor {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1000.0, 10000.0, 100000.0], [800.0, 8000.0, 80000.0], [50000.0, 50000.0, 50000.0], [1000.0, 100.0, 10.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
	}
}
comments=["""[path:'Podra', period:-1, lastChange:new org.joda.time.DateTime(1298019251670),user:null, comment: \"\"\"This parametrization corresponds to the surplus example mentioned within the paper of Martin Melchior and Michael Noe.\"\"\"]"""]
