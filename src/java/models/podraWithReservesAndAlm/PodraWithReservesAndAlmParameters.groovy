package models.podraWithReservesAndAlm

model=models.podraWithReservesAndAlm.PodraWithReservesAndAlmModel
periodCount=1
displayName='One Line Example'
applicationVersion='0.5'
components {
	reserveGenerators {
		subMotorHull {
			parmReservesModel[0]=org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType.INITIAL_RESERVES, ["basedOnClaimsGenerators":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull attritional", "motor hull single"]]),["Claims Generators"], org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker),])
			parmInitialReserves[0]=200.0
			parmDistribution[0]=org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:100.0, stDev:10.0])
			parmModification[0]=org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:])
			parmPeriodPaymentPortion[0]=0.8
		}
	}
	reinsurance {
		subQuotaShareMotorHull {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory.getContractStrategy(org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5,"commission":0.0,"coveredByReinsurer":1.0,])
			parmCoveredPerils[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull attritional", "motor hull single"]]),["perils"], org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker)
			parmCoveredLines[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Covered Lines"], org.pillarone.riskanalytics.domain.pc.lob.LobMarker)
			parmInuringPriority[0]=0
		}
	}
	claimsGenerators {
		subMotorHullAttritional {
			parmPeriodPaymentPortion[0]=0.8
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:0.8, stDev:0.054]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),])
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory.getAllocatorStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
		}
		subMotorHullSingle {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory.getAllocatorStrategy(org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType.NONE, [:])
			parmPeriodPaymentPortion[0]=0.6
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory.getStrategy(org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase.ABSOLUTE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda:1.0]),"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.constants.Exposure.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha:0.523, beta:500000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(org.pillarone.riskanalytics.domain.utils.DistributionModifier.TRUNCATED, [max:2.0E7, min:500000.0]),"produceClaim":org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType.SINGLE,])
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker)
		}
	}
	underwritingSegments {
		subMotorHull {
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [9.7014E7], [0.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
		}
	}
	linesOfBusiness {
		subMotorHull {
			subUnderwritingFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull"], [1.0]]),["Reserves","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
			subClaimsFilter {
				parmPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor hull attritional", "motor hull single"], [1.0, 1.0]]),["Claims Generator","Portion of Claims"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
		}
	}
	dependencies {
		subAttritional {
			parmCopulaStrategy[0]=org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory.getCopulaStrategy(org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType.NORMAL, ["dependencyMatrix":new org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0, 0.25, 0.25, 0.25], [0.25, 1.0, 0.5, 0.25], [0.25, 0.5, 1, 0.25], [0.25, 0.25, 0.25, 1]]),["personal accident attritional","motor third party liability attritional","motor hull attritional","property attritional"],org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker),])
		}
	}
}
