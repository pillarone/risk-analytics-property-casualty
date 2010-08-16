package models.podra

model = models.podra.PodraModel
displayName = "Drill Down"
components {
	linesOfBusiness {
		outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
		outUnderwritingInfoCeded = "AGGREGATED_DRILL_DOWN"
		sublineOfBusiness {
			outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
			outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
			outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoCeded = "AGGREGATED_DRILL_DOWN"
		}
	}
	reinsurance {
		outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
		outCoverUnderwritingInfo = "AGGREGATED_DRILL_DOWN"
		subContracts {
			subriContract {
				outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
				outCoverUnderwritingInfo = "AGGREGATED_DRILL_DOWN"
			}
		}
	}
	structures {
		subsegmentFilter {
			outClaimsCeded = "AGGREGATED_DRILL_DOWN"
			outClaimsGross = "AGGREGATED_DRILL_DOWN"
			outClaimsNet = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoCeded = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoGross = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoNet = "AGGREGATED_DRILL_DOWN"
		}
	}
}
