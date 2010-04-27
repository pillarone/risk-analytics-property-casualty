package models.multiCompany

import org.pillarone.riskanalytics.domain.assets.constants.Rating

model=models.multiCompany.MultiCompanyModel
periodCount=1
displayName='Three Companies'
applicationVersion='0.6.1'
components {
	reserveGenerators {
		subMarsMotor {
			parmReservesModel[0]=org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.INITIAL_RESERVES, ["basedOnClaimsGenerators":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Claims Generators"], org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker),])
			parmPeriodPaymentPortion[0]=0.0
			parmDistribution[0]=org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:0.0])
			parmInitialReserves[0]=0.0
			parmModification[0]=org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
		}
		subVenusMotor {
			parmPeriodPaymentPortion[0]=0.0
			parmModification[0]=org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
			parmInitialReserves[0]=0.0
			parmReservesModel[0]=org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.INITIAL_RESERVES, ["basedOnClaimsGenerators":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Claims Generators"], org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker),])
			parmDistribution[0]=org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:0.0])
		}
	}
	reinsuranceMarket {
        subContracts {
            subQuotaShare {
                parmCommissionStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.NOCOMMISSION, [:])
                parmInuringPriority[0]=0
                parmCover[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType.ALL, ["reserves":org.pillarone.riskanalytics.domain.pc.constants.IncludeType.NOTINCLUDED,])
                parmBasedOn[0]=org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase.NET
                parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["earth re"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COMPANY_PORTION'))
                parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory.getContractStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5,"limit":org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType.NONE, [:]),"coveredByReinsurer":1.0,])
            }
        }
	}
	linesOfBusiness {
		subMarsMotor {
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["mars motor"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["mars motor"], [1.0]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker, 'subMars')
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["mars motor attritional"], [1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
		}
		subVenusMotor {
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["venus motor"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["venus motor"], [1.0]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker, 'subVenus')
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["venus motor attritional"], [1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
		}
	}
	claimsGenerators {
		subMarsMotorAttritional {
			parmPeriodPaymentPortion[0]=1.0
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory.getAllocatorStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:2000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
		}
		subVenusMotorAttritional {
			parmPeriodPaymentPortion[0]=1.0
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory.getAllocatorStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.CONSTANT, [constant:1000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
		}
	}
	underwritingSegments {
		subVenusMotor {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[500000.0], [50000.0], [100000.0], [1000.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
		subMarsMotor {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1000000.0], [500000.0], [50000.0], [100.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
	}
	companies {
		subVenus {
			parmRating[0] = Rating.BBB
		}
		subEarthRe {
			parmRating[0] = Rating.BBB
		}
		subMars {
			parmRating[0] = Rating.BBB
		}
	}
}
