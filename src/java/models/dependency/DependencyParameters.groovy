package models.dependency

import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopulaType
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

model = DependencyModel
periodCount = 5
allPeriods = 0..<periodCount
displayName = 'Dependency Model Comparison'
periodLabels = ['Model 1', 'Model 2', 'Model 3', 'Model 4', 'Model 5']

components {
    frequencyGenerator {
        parmDistribution[allPeriods] = DistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ["constant": 2d])
    }
    copula {
        /*parmCopulaStrategy[0] = CopulaStrategyFactory.getCopulaStrategy(CopulaType.GUMBEL,
                ["targets": new LobTableMultiDimensionalParameter(['Fire', 'Hull'], ['Targets']),
                 "lambda": 10, "dimension": 2])*/
        parmCopulaStrategy[0..4] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.NORMAL,
                ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1.0, 0.5], [0.5, 1.0]], ['fire', 'hull'], LobMarker)])
    }
    fire {
        subSeverityExtractor {
            parmFilterCriteria[allPeriods] = new ConstrainedString(LobMarker, 'fire')
        }
        subClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 10, "stDev": 2])
        }
    }
    hull {
        subSeverityExtractor {
            parmFilterCriteria[allPeriods] = new ConstrainedString(LobMarker, 'hull')
        }
        subClaimsGenerator {
            parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 10, "stDev": 2])
        }
    }
    cxl {
        parmContractStrategy[allPeriods] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.CXL,
                ["reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ["Reinstatement Premium"]), "limit": 10, "aggregateLimit": 10, "attachmentPoint": 10.0, "premium": 0, "premiumBase": PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
        parmInuringPriority[allPeriods] = 0
    }

}
