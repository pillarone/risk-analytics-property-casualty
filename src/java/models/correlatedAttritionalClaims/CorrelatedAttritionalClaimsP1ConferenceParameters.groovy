package models.correlatedAttritionalClaims

import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopulaType
import org.pillarone.riskanalytics.core.parameterization.MatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.DistributionType


model = CorrelatedAttritionalClaimsModel
periodCount = 3
allPeriods = 0..<periodCount

components {
    copula {
        parmCopulaStrategy[allPeriods] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.NORMAL,
                ["meanVector": new MatrixMultiDimensionalParameter([[0.0, 0.0]], ['Fire', 'Hull'], ['means']),
                        "dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.5], [0.5, 1.0]], ['Fire', 'Hull'], ['Fire', 'Hull'])])
    }
    fireClaims {
        parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 100000, "stDev": 10000])
        parmDistribution[1] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 100000, "stDev": 10000])
        parmDistribution[2] = DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 100000, "stDev": 11125])
    }
    hullClaims {
        parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 100000, "stDev": 10000])
        parmDistribution[1] = DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 100000, "stDev": 11125])
        parmDistribution[2] = DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 100000, "stDev": 11125])
    }
}
