package models.mark

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = MarkModel

components {
    claimsGeneratorFire {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    claimsGeneratorMotor {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
}