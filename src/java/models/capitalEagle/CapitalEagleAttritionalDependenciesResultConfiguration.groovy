package models.capitalEagle

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = models.capitalEagle.CapitalEagleAttritionalDependenciesModel
displayName = 'Attritional Gross Claims'

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