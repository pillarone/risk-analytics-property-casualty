package models.podra

model = models.podra.PodraModel
displayName = "Drill Down"
components {
	linesOfBusiness {
		outClaimsCeded = "AGGREGATED"
		outClaimsGross = "AGGREGATED"
		outClaimsNet = "AGGREGATED"
		outUnderwritingInfoCeded = "AGGREGATED"
		sublineOfBusiness {
			outClaimsCeded = "AGGREGATED_DRILL_DOWN"
			outClaimsGross = "AGGREGATED_DRILL_DOWN"
			outClaimsNet = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoCeded = "AGGREGATED_DRILL_DOWN"
		}
	}
	reinsurance {
		outClaimsDevelopmentLeanCeded = "AGGREGATED"
		outClaimsDevelopmentLeanGross = "AGGREGATED"
		outClaimsDevelopmentLeanNet = "AGGREGATED"
		outCoverUnderwritingInfo = "AGGREGATED"
		subContracts {
			subriContract {
				outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
				outCoverUnderwritingInfo = "AGGREGATED_DRILL_DOWN"
			}
		}
	}
}
