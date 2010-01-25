package models.test

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = StructureTestModel

components {
    mtpl {
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
                }
            }
        }
    }
    claimsAggregator {
        outClaimsCeded = AggregatedCollectingModeStrategy.IDENTIFIER
    }
}



