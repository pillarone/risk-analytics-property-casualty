package models.podra

model = models.podra.PodraModel
displayName = "Segments, Claims, R/I"
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
                outContractFinancials = "AGGREGATED"
                outCoverUnderwritingInfo = "AGGREGATED"
            }
		}
	}
}
