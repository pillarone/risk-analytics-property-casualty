package models.podra

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = models.podra.PodraModel
displayName = 'Lines of Business Net Claims'

components.linesOfBusiness.outClaimsNet = AggregatedCollectingModeStrategy.IDENTIFIER
components.linesOfBusiness.sublineOfBusiness.outClaimsNet = AggregatedCollectingModeStrategy.IDENTIFIER
