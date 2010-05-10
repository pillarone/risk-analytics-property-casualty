package models.podra

model = models.podra.PodraModel
displayName = "Drill Down"
components {
	linesOfBusiness {
		outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
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
		subContracts {
            subRiContracts {
                outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
                outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
                outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
                outCoverUnderwritingInfo  = "AGGREGATED_DRILL_DOWN"
            }
		}
	}
}
