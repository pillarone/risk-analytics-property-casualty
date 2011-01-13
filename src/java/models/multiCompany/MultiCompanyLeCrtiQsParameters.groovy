package models.multiCompany

model=models.multiCompany.MultiCompanyModel
periodCount=1
displayName='LE-CRTI: QS'
applicationVersion='1.1.3'
components {
	reinsuranceMarket {
		subContracts {
			subCrti {
				parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["son"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COMPANY_PORTION'))
				parmPremiumBase[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase.COMPLETESEGMENT
				parmInuringPriority[0]=0
				parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["daughter"]]),["Covered Lines"], org.pillarone.riskanalytics.domain.pc.lob.LobMarker),])
				parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
				parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5,"limit":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.NONE, [:]),"coveredByReinsurer":1.0,])
				parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
			}
		}
	}
	creditDefault {
		parmDefaultBB[0]=0.012
		parmDefaultA[0]=5.0E-4
		parmDefaultAA[0]=1.0E-4
		parmDefaultCCC[0]=0.0475
		parmDefaultB[0]=0.0475
		parmDefaultBBB[0]=0.0024
		parmDefaultC[0]=0.0475
		parmDefaultAAA[0]=2.0E-5
		parmDefaultCC[0]=0.0475
	}
	claimsGenerators {
		subSon {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
			parmPeriodPaymentPortion[0]=1.0
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:90.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
		}
		subDaughter {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:70.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
		}
	}
	linesOfBusiness {
		subSon {
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker, 'subSon')
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["son"], [1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
		}
		subDaughter {
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker, 'subDaughter')
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["daughter"], [1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
		}
	}
	companies {
		subDaughter {
			parmRating[0]=org.pillarone.riskanalytics.domain.assets.constants.Rating.C
		}
		subSon {
			parmRating[0]=org.pillarone.riskanalytics.domain.assets.constants.Rating.C
		}
		subParent {
			parmRating[0]=org.pillarone.riskanalytics.domain.assets.constants.Rating.C
		}
	}
}
comments=["""[path:'MultiCompany', period:-1, lastChange:new Date(1293101120250),user:null, comment: \"\"\"Quota Share 50% daughter -> son
default which has an impact as the daughter can not always ceed its share to the son and therefore the net claims burden of the daughter is larger than 50% of the gross\"\"\"]"""]
