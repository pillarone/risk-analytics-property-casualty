package models.podra

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType

model = models.podra.PodraModel
periodCount = 1
displayName = 'Drill Down Test Parameter'
applicationVersion = '0.5'
components {
    reinsurance {
        subContracts {
            subPropertyCxl {
                parmCover[0] = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ['lines': new ComboBoxTableMultiDimensionalParameter(['property'], ["Covered Lines"], LobMarker),])
//            parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ALL, ["reserves":org.pillarone.riskanalytics.domain.pc.constants.IncludeType.NOTINCLUDED,])
//            parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new ComboBoxTableMultiDimensionalParameter([[""]],["Covered Lines"], LobMarker),])
                parmInuringPriority[0] = 1
                parmContractStrategy[0] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.CXL,
                        ["premiumBase": PremiumBase.GNPI, "premium": 0.0688,
                                "reinstatementPremiums": new TableMultiDimensionalParameter(GroovyUtils.toList([[1.0, 1.0, 1.0]]), ["Reinstatement Premium"]),
                                "attachmentPoint": 300.0, "limit": 50.0, "aggregateLimit": 50.0, "coveredByReinsurer": 1.0,])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0.0,])
            }
            subMotorHullWxl {
                parmContractStrategy[0] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.WXL,
                        ["premiumBase": PremiumBase.GNPI, "premium": 0.043,
                                "reinstatementPremiums": new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]),
                                        ["Reinstatement Premium"]), "attachmentPoint": 500.0, "limit": 200.0, "aggregateLimit": 200.0, "coveredByReinsurer": 1.0,])
                parmCover[0] = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ['lines': new ComboBoxTableMultiDimensionalParameter(['motor hull'], ["Covered Lines"], LobMarker),])
                parmInuringPriority[0] = 1
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0.0,])
            }
            subPropertyQuotaShare {
                parmInuringPriority[0] = 0
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ["commission": 0.0,])
                parmCover[0] = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ['lines': new ComboBoxTableMultiDimensionalParameter(['property'], ["Covered Lines"], LobMarker),])
                parmContractStrategy[0] = ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.2, "coveredByReinsurer": 1.0,])
            }
        }
    }
    claimsGenerators {
        subPropertySingle {
            parmClaimsModel[0] = ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase": FrequencyBase.ABSOLUTE,
                    "frequencyDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, [constant: 1.0]),
                    "frequencyModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]), "claimsSizeBase": Exposure.ABSOLUTE,
                    "claimsSizeDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, [constant: 900.0]),
                    "claimsSizeModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]), "produceClaim": FrequencySeverityClaimType.SINGLE,])
            parmUnderwritingInformation[0] = new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["property"]]), ["Underwriting Information"], IUnderwritingInfoMarker)
            parmAssociateExposureInfo[0] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
            parmPeriodPaymentPortion[0] = 1.0
        }
        subPropertyAttritional {
            parmPeriodPaymentPortion[0] = 1.0
            parmAssociateExposureInfo[0] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
            parmUnderwritingInformation[0] = new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["property"]]), ["Underwriting Information"], IUnderwritingInfoMarker)
            parmClaimsModel[0] = ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase": Exposure.ABSOLUTE,
                    "claimsSizeDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, [constant: 200.0]),
                    "claimsSizeModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
        }
        subPropertyEarthquake {
            parmClaimsModel[0] = ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase": FrequencyBase.ABSOLUTE,
                    "frequencyDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, [constant: 1.0]),
                    "frequencyModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
                    "claimsSizeBase": Exposure.ABSOLUTE, "claimsSizeDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, [constant: 500.0]),
                    "claimsSizeModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]), "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])
            parmAssociateExposureInfo[0] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
            parmPeriodPaymentPortion[0] = 1.0
            parmUnderwritingInformation[0] = new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["property"]]), ["Underwriting Information"], IUnderwritingInfoMarker)
        }
        subMotorHullSingle {
            parmPeriodPaymentPortion[0] = 1.0
            parmUnderwritingInformation[0] = new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["motor hull"]]), ["Underwriting Information"], IUnderwritingInfoMarker)
            parmClaimsModel[0] = ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase": FrequencyBase.ABSOLUTE,
                    "frequencyDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, [constant: 1.0]),
                    "frequencyModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
                    "claimsSizeBase": Exposure.ABSOLUTE, "claimsSizeDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT,
                    [constant: 1000.0]), "claimsSizeModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]), "produceClaim": FrequencySeverityClaimType.SINGLE,])
            parmAssociateExposureInfo[0] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
        }
        subMotorHullAttritional {
            parmClaimsModel[0] = ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase": Exposure.ABSOLUTE,
                    "claimsSizeDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, [constant: 100.0]),
                    "claimsSizeModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
            parmUnderwritingInformation[0] = new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["motor hull"]]), ["Underwriting Information"], IUnderwritingInfoMarker)
            parmPeriodPaymentPortion[0] = 1.0
            parmAssociateExposureInfo[0] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
        }
    }
    linesOfBusiness {
        subMotorHull {
            subClaimsFilter {
                parmPortions[0] = new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["motor hull attritional", "motor hull single"],
                        [1.0, 1.0]]), ["Claims Generator", "Portion of Claims"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
            }
            subUnderwritingFilter {
                parmPortions[0] = new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["motor hull"], [1.0]]), ["Underwriting", "Portion"],
                        ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
            }
            subReservesFilter {
                parmPortions[0] = new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["motor hull reserves"], [1.0]]),
                        ["Reserves", "Portion of Claims"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
            }
        }
        subProperty {
            subClaimsFilter {
                parmPortions[0] = new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["property single", "property attritional", "property storm", "property flood", "property earthquake"], [1.0, 1.0, 1.0, 1.0, 1.0]]), ["Claims Generator", "Portion of Claims"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
            }
            subReservesFilter {
                parmPortions[0] = new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["property reserves"], [1.0]]), ["Reserves", "Portion of Claims"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
            }
            subUnderwritingFilter {
                parmPortions[0] = new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["property"], [1.0]]), ["Underwriting", "Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
            }
        }
    }
    underwritingSegments {
        subProperty {
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter(
                    GroovyUtils.toList([[100000, 200000, 300000, 400000, 500000, 750000, 1000000, 1250000, 1500000],
                            [78479, 167895, 265488, 378455, 445783, 645789, 835481, 1202547, 1363521],
                            [8706931, 25985915, 22805313, 9315291, 5547769, 564420, 8021, 2886, 2454],
                            [92455, 128979, 71583, 24614, 12445, 874, 12, 4, 3]]), ["maximum sum insured", "average sum insured", "premium", "number of policies"])
        }
        subMotorHull {
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0], [0.0], [9.7014E7], [0.0]]), ["maximum sum insured", "average sum insured", "premium", "number of policies"])
        }
    }
}
