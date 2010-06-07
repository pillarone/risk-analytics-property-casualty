package models.capitalEagle

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy

model = models.capitalEagle.CapitalEagleModel
displayName = 'newCollectors'

components {
    mtpl {
        subClaimsGenerator {
            outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
    motorHull {
        subClaimsGenerator {
            outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
    personalAccident {
        subClaimsGenerator {
            outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
    property {
        subClaimsGenerator {
            outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
        subRiProgram {
            outClaimsCeded = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
}