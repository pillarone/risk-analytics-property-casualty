package models.podraPC

import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType

model=models.podraPC.PodraPCModel
periodCount=1
displayName='One Line Example P&C'
applicationVersion='0.6.1'
components {
	claimsGenerators {
		subMotorHullSingle {
			parmPeriodPaymentPortion[0]=0.6
			parmClaimsModel[0]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":RandomDistributionFactory.getDistribution(DistributionType.POISSON, [lambda:1.0]),"frequencyModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.PARETO, [alpha:0.523, beta:500000.0]),"claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.TRUNCATED, [max:2.0E7, min:500000.0]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
			parmAssociateExposureInfo[0]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(["motor hull"],["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subMotorHullAttritional {
			parmAssociateExposureInfo[0]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(["motor hull"],["Underwriting Information"], IUnderwritingInfoMarker)
			parmPeriodPaymentPortion[0]=0.8
			parmClaimsModel[0]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [mean:0.8, stDev:0.054]),"claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
		}
	}
	linesOfBusiness {
		subMotorHull {
			subUnderwritingFilter {
				parmPortions[0]=new ConstrainedMultiDimensionalParameter([["motor hull"],
[1.0]
],["Underwriting","Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
			}
			subClaimsFilter {
				parmPortions[0]=new ConstrainedMultiDimensionalParameter([["motor hull attritional","motor hull single"],
[1.0,1.0]
],["Claims Generator","Portion of Claims"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
			}
			subReservesFilter {
				parmPortions[0]=new ConstrainedMultiDimensionalParameter([["motor hull"],
[1.0]
],["Reserves","Portion of Claims"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			}
		}
	}
	dependencies {
		subAttritional {
			parmCopulaStrategy[0]=CopulaStrategyFactory.getCopulaStrategy(PerilCopulaType.NORMAL, ["dependencyMatrix":new ComboBoxMatrixMultiDimensionalParameter([[1.0,0.25,0.25,0.25],
[0.25,1.0,0.5,0.25],
[0.25,0.5,1,0.25],
[0.25,0.25,0.25,1]
],["personal accident attritional","motor third party liability attritional","motor hull attritional","property attritional"],PerilMarker),])
		}
	}
	reserveGenerators {
		subMotorHull {
			parmReservesModel[0]=ReservesGeneratorStrategyType.getStrategy(ReservesGeneratorStrategyType.INITIAL_RESERVES, ["basedOnClaimsGenerators":new ComboBoxTableMultiDimensionalParameter(["motor hull attritional","motor hull single"],["Claims Generators"], PerilMarker),])
			parmDistribution[0]=RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [mean:100.0, stDev:10.0])
			parmInitialReserves[0]=200.0
			parmModification[0]=DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
			parmPeriodPaymentPortion[0]=0.8
		}
	}
	reinsurance {
        subContracts {
            subQuotaShareMotorHull {
                parmContractStrategy[0]=ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare":0.5,"coveredByReinsurer":1.0,])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
                parmInuringPriority[0]=0
                parmCover[0] = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.PERILS, ['perils': new ComboBoxTableMultiDimensionalParameter(['motor hull attritional','motor hull single'], ["Covered Perils"], PerilMarker),])
            }
        }
	}
	underwritingSegments {
		subMotorHull {
			parmUnderwritingInformation[0]=new TableMultiDimensionalParameter([[0.0],
[0.0],
[9.7014E7],
[0.0]
],["maximum sum insured","average sum insured","premium","number of policies"])
		}
	}
}
