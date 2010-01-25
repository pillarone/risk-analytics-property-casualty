package models.densityestimation

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = models.densityestimation.TestDensitiesModel

components.claims.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
components.sl.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
components.sl.outUncoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
components.triModalClaims.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
