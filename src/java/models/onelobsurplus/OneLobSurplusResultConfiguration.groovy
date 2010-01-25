package models.onelobsurplus

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = OneLobSurplusModel

components {
    underwriting {

    }
    attritionalClaimsGenerator {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    singleClaimsGenerator {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    surplus {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    wxl {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
}


