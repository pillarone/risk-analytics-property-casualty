package models.capitalEagle

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy

model = models.capitalEagle.CapitalEagleModel
displayName = 'newCollectors'

components {
    mtpl {
        subClaimsGenerator {
            outClaims = SingleValueCollectingModeStrategy.IDENTIFIER
        }
    }
    motorHull {
        subClaimsGenerator {
            outClaims = SingleValueCollectingModeStrategy.IDENTIFIER
        }
    }
    personalAccident {
        subClaimsGenerator {
            outClaims = SingleValueCollectingModeStrategy.IDENTIFIER
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