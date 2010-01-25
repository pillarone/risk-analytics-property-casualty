package models.exampleCompany

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = models.exampleCompany.ExampleCompanyModel

components {
    claimsAggregator {
        outClaimsGross = AggregatedCollectingModeStrategy.IDENTIFIER
        outClaimsCeded = AggregatedCollectingModeStrategy.IDENTIFIER
        outClaimsNet = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    mtpl {
//        subUnderwriting {
//            outUnderwritingInfo = AggregatedCollectingModeStrategy.IDENTIFIER
//        }
        subRiProgram {
//            outUnderwritingInfoCeded = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract2.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract3.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract1.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subSingleClaimsGenerator.subClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
    motorHull {
//        subUnderwriting {
//            outUnderwritingInfo = AggregatedCollectingModeStrategy.IDENTIFIER
//        }
        subRiProgram {
//            outUnderwritingInfoCeded = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract2.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract3.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract1.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subSingleClaimsGenerator.subClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
    personalAccident {
//        subUnderwriting {
//            outUnderwritingInfo = AggregatedCollectingModeStrategy.IDENTIFIER
//        }
        subRiProgram {
//            outUnderwritingInfoCeded = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract2.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract3.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract1.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subSingleClaimsGenerator.subClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
    property {
//        subUnderwriting {
//            outUnderwritingInfo = AggregatedCollectingModeStrategy.IDENTIFIER
//        }
        subRiProgram {
//            outUnderwritingInfoCeded = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract2.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract3.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subContract1.outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
            subSingleClaimsGenerator.subClaimsGenerator.outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
}
