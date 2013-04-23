package models.podraPC

model = models.podraPC.PodraPCModel
displayName = "Aggregated Overview"
components {
	claimsGenerators {
		outClaimsLeanDevelopment = "AGGREGATED"
		subsubcomponents {
			outClaimsLeanDevelopment = "AGGREGATED"
		}
	}
	linesOfBusiness {
		outClaimsCeded = "AGGREGATED"
		outClaimsGross = "AGGREGATED"
		outClaimsNet = "AGGREGATED"
		outUnderwritingInfoCeded = "AGGREGATED"
		outUnderwritingInfoGross = "AGGREGATED"
		outUnderwritingInfoNet = "AGGREGATED"
		sublineOfBusiness {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	reinsurance {
		outClaimsDevelopmentLeanCeded = "AGGREGATED"
		outClaimsDevelopmentLeanGross = "AGGREGATED"
		outClaimsDevelopmentLeanNet = "AGGREGATED"
		outCoverUnderwritingInfo = "AGGREGATED"
		outNetAfterCoverUnderwritingInfo = "AGGREGATED"
		outUnderwritingInfo = "AGGREGATED"
		subContracts {
            subRiContracts {
                outClaimsDevelopmentLeanCeded = "AGGREGATED"
                outClaimsDevelopmentLeanGross = "AGGREGATED"
                outClaimsDevelopmentLeanNet = "AGGREGATED"
                outCoverUnderwritingInfo = "AGGREGATED"
//                outFilteredUnderwritingInfo = "AGGREGATED"
                outNetAfterCoverUnderwritingInfo = "AGGREGATED"
            }
		}
	}
	reserveGenerators {
		outClaimsLeanDevelopment = "AGGREGATED"
		outInitialReserves = "AGGREGATED"
		subsubcomponents {
			outClaimsLeanDevelopment = "AGGREGATED"
			outInitialReserves = "AGGREGATED"
		}
	}
}
