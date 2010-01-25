package models.asset

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = models.asset.AssetModel
displayName = 'Asset'

components.engine.outBondAccounting=AggregatedCollectingModeStrategy.IDENTIFIER
