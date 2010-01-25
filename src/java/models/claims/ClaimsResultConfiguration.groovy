package models.claims

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = models.claims.ClaimsModel
displayName = 'detailed claims'

components.claimsGenerators.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
components.claimsGenerators.subsubcomponents.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER