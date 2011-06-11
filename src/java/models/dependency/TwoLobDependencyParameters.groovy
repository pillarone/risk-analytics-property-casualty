package models.dependency

import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopulaType
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

model = TwoLobDependencyModel
periodCount = 1
allPeriods = 0..<periodCount
displayName = 'Conference Example'
periodLabels = ['Model 1', 'Model 2', 'Model 3']

components {
    frequencyGeneratorEvent {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 1d])
    }
    frequencyGeneratorLarge {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 1d])
    }
    independentFrequencyGeneratorHull {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10d])
    }
    independentFrequencyGeneratorFire {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10d])
    }
    copulaEvent {
        parmCopulaStrategy[allPeriods] =
            parmCopulaStrategy[allPeriods] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.T,
                    ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1.0, 0.7], [0.7, 1.0]], ['fire', 'hull'], ISegmentMarker),
                            "degreesOfFreedom": 3])
    }
    copulaLarge {
        parmCopulaStrategy[allPeriods] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.NORMAL,
                ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1.0, 0.5], [0.5, 1.0]], ['fire', 'hull'], ISegmentMarker)])

    }
    copulaAttritional {
        parmCopulaStrategy[allPeriods] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.NORMAL,
                ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1.0, 0.5], [0.5, 1.0]], ['fire', 'hull'], ISegmentMarker)])
    }
    fire {
        subEventSeverityExtractor {
            parmFilterCriteria[allPeriods] = new ConstrainedString(ISegmentMarker, 'fire')
        }
        /*subSingleSeverityExtractor {
            parmFilterCriteria[allPeriods] = 'fire'
        }
        subAttritionalSeverityExtractor {
            parmFilterCriteria[allPeriods] = 'fire'
        }*/
        subEventClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 1.2, "alpha": 1000d])

        }
        subSingleClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 1.5, "alpha": 500d])
            parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min": 500d, "max": 20000d])
        }
        subAttritionalClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 100000d, "stDev": 6000d])
        }
    }
    hull {
        subEventSeverityExtractor {
            parmFilterCriteria[allPeriods] = new ConstrainedString(ISegmentMarker, 'hull')
        }
        /*subSingleSeverityExtractor {
            parmFilterCriteria[allPeriods] = 'hull'
        }
        subAttritionalSeverityExtractor {
            parmFilterCriteria[allPeriods] = 'hull'
        }*/
        subEventClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 1.2, "alpha": 1000d])
        }
        subSingleClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 1.5, "alpha": 500d])
            parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min": 500d, "max": 20000d])
        }
        subAttritionalClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 100000d, "stDev": 6000d])
        }
    }
    cxl {
        parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])

    }

}
