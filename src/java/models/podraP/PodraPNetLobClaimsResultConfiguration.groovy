package models.podraP

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = models.podraP.PodraPModel
displayName = 'Lines of Business Net Claims'

components.linesOfBusiness.outClaimsNet = AggregatedCollectingModeStrategy.IDENTIFIER
components.linesOfBusiness.sublineOfBusiness.outClaimsNet = AggregatedCollectingModeStrategy.IDENTIFIER
