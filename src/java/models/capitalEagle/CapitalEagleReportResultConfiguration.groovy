package models.capitalEagle

model = models.capitalEagle.CapitalEagleModel
displayName = "Reports"
components {
	Summary {
		claimsAggregator {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
		}
	}
	motorHull {
		subClaimsGenerator {
			subAttritionalClaimsGenerator {
				outClaims = "AGGREGATED"
			}
			subSingleClaimsGenerator {
				outClaims = "AGGREGATED"
			}
		}
		subRiProgram {
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	mtpl {
		subClaimsGenerator {
			subAttritionalClaimsGenerator {
				outClaims = "AGGREGATED"
			}
			subSingleClaimsGenerator {
				outClaims = "AGGREGATED"
			}
		}
		subRiProgram {
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	personalAccident {
		subClaimsGenerator {
			subAttritionalClaimsGenerator {
				outClaims = "AGGREGATED"
			}
			subSingleClaimsGenerator {
				outClaims = "AGGREGATED"
			}
		}
		subRiProgram {
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	property {
		subClaimsGenerator {
			subAttritionalSeverityClaimsGenerator {
				outClaims = "AGGREGATED"
			}
			subEQGenerator {
				outClaims = "AGGREGATED"
			}
			subFloodGenerator {
				outClaims = "AGGREGATED"
			}
			subSingleClaimsGenerator {
				outClaims = "AGGREGATED"
			}
			subStormGenerator {
				outClaims = "AGGREGATED"
			}
		}
		subRiProgram {
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
}
