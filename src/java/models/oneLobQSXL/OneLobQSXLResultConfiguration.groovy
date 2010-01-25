package models.oneLobQSXL

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = OneLobQSXLModel

components {
    claimsGenerator {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    quotaShare {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    wxl {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
}

