package models.dependency

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

model = DependencyModel
displayName = 'Frequency, Severity, Claim'

components {
    frequencyGenerator {
        outFrequency = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    cxl {
        outCoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        outUncoveredClaims = AggregatedCollectingModeStrategy.IDENTIFIER
    }
    hull {
        outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        subClaimsGenerator {
            outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
        subSeverityExtractor {
            outSeverities = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
    fire {
        outClaims = collector
        subSeverityExtractor {
            outSeverities = AggregatedCollectingModeStrategy.IDENTIFIER
        }
        subClaimsGenerator {
            outClaims = AggregatedCollectingModeStrategy.IDENTIFIER
        }
    }
}

