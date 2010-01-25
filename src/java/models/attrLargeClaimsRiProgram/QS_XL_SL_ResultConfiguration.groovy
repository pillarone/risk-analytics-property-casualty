package models.attrLargeClaimsRiProgram

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = QS_XL_SL_Model


components {
    claimsGenerator {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    attritionalClaimsGenerator {
        outClaims = collector
    }
    quotaShare {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    wxl {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    aggregator {
        outClaimsCeded = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    stopLoss {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
}

