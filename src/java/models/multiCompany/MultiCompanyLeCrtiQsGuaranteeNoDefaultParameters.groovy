package models.multiCompany

model=models.multiCompany.MultiCompanyModel
periodCount=1
displayName='LE-CRTI: QS, Guarantee, no default'
applicationVersion='1.2'
components {
	linesOfBusiness {
		subSon {
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker, 'subSon')
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["son"], [1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
		}
		subDaughter {
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker, 'subDaughter')
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["daughter"], [1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
		}
	}
	creditDefault {
		parmDefaultCC[0]=0.0475
		parmDefaultAAA[0]=2.0E-5
		parmDefaultAA[0]=1.0E-4
		parmDefaultB[0]=0.0475
		parmDefaultCCC[0]=0.0475
		parmDefaultBBB[0]=0.0024
		parmDefaultA[0]=5.0E-4
		parmDefaultC[0]=0.0475
		parmDefaultBB[0]=0.012
	}
	reinsuranceMarket {
		subContracts {
			subCrti {
				parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["son"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COMPANY_PORTION'))
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CompanyCoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CompanyCoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["daughter"]]),["Covered Segments"], org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker),])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5,"limit":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.NONE, [:]),"coveredByReinsurer":1.0,])
				parmInuringPriority[0]=0
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
			}
			subCrtiQuarantee {
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.STOPLOSS, ["stopLossContractBase":org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase.ABSOLUTE,"premium":0.0,"attachmentPoint":40.0,"premiumAllocation":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType.PREMIUM_SHARES, [:]),"limit":50.0,"coveredByReinsurer":1.0,])
				parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["parent"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COMPANY_PORTION'))
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
				parmInuringPriority[0]=1
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CompanyCoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CompanyCoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["daughter"]]),["Covered Segments"], org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker),])
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
			}
		}
	}
	claimsGenerators {
		subSon {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
			parmPeriodPaymentPortion[0]=1.0
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:90.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
		}
		subDaughter {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:70.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmPeriodPaymentPortion[0]=1.0
		}
	}
	companies {
		subDaughter {
			parmRating[0]=org.pillarone.riskanalytics.domain.assets.constants.Rating.NO_DEFAULT
		}
		subParent {
			parmRating[0]=org.pillarone.riskanalytics.domain.assets.constants.Rating.NO_DEFAULT
		}
		subSon {
			parmRating[0]=org.pillarone.riskanalytics.domain.assets.constants.Rating.NO_DEFAULT
		}
	}
}
comments=["""[path:'MultiCompany', period:-1, lastChange:new org.joda.time.DateTime(1298019477717),user:null, comment: \"\"\"Quota Share 50% daughter -> son
guarantee 50 xs 40 daughter -> parent
no default therefore the guarantee never gets triggered and the quota share is equal to &rdquo;LE-CRTI: QS, no default&rdquo;\"\"\"]"""]
