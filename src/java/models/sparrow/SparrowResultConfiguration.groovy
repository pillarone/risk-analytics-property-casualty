package models.sparrow

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = SparrowModel

components {
    claimsGenerator {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
}

