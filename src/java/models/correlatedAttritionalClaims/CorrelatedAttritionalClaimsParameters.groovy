package models.correlatedAttritionalClaims

import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopulaType
import org.pillarone.riskanalytics.core.parameterization.MatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.DistributionType

model = CorrelatedAttritionalClaimsModel
periodCount = 3
allPeriods = 0..<periodCount

components {
    copula {
        parmCopulaStrategy[0] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.NORMAL,
                ["dependencyMatrix": new MatrixMultiDimensionalParameter([[1.0, 0.5], [0.5, 1.0]], ['Fire', 'Hull'], ['Fire', 'Hull'])])
        parmCopulaStrategy[1] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.INDEPENDENT,
                ["targets": new SimpleMultiDimensionalParameter(["Fire", "Hull"]),])
        parmCopulaStrategy[2] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.FRECHETUPPERBOUND,
                ["targets": new SimpleMultiDimensionalParameter(["Fire", "Hull"]),])

    }
    fireClaims {
        parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 100000, "stDev": 200])
    }
    hullClaims {
        parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 100000, "stDev": 200])
    }

}
