package models.podraPreview

model = models.podraPreview.PodraPreviewModel
displayName = "Drill Down 2"
components {
	reinsurance {
		outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
		outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
		subContracts {
			outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
			outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
			subcontracts {
				outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
			}
		}
	}
}
