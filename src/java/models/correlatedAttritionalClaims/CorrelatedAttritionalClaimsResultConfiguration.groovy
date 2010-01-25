package models.correlatedAttritionalClaims

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = CorrelatedAttritionalClaimsModel


components {
    claimsAggregator {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    fireClaims {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    hullClaims {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
}