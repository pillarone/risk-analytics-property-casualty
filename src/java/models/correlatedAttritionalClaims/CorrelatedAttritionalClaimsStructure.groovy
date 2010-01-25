package models.correlatedAttritionalClaims

model = CorrelatedAttritionalClaimsModel
periodCount = 3

company {
    General {
        components {
            copula
        }
    }
    Fire {
        components {
            extractorFire
            fireClaims
        }
    }
    Hull {
        components {
            extractorHull
            hullClaims
        }
    }
}