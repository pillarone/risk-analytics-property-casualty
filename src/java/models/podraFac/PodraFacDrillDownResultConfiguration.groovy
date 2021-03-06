package models.podraFac

model = models.podraFac.PodraFacModel
displayName = "CapitalEagle Analysis Drill Down"
components {
	aggregateFinancials {
		outAlm = "AGGREGATED"
		outTotal = "AGGREGATED"
		outUnderwriting = "AGGREGATED"
	}
	almGenerators {
		outAlmResult = "AGGREGATED"
		outInitialVolume = "AGGREGATED"
		subsubcomponents {
			outAlmResult = "AGGREGATED"
			outInitialVolume = "AGGREGATED"
		}
	}
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
			outClaimsCeded = "AGGREGATED_DRILL_DOWN"
			outClaimsGross = "AGGREGATED_DRILL_DOWN"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoGross = "AGGREGATED_DRILL_DOWN"
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
			subriContract {
				outClaimsDevelopmentLeanCeded = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanGross = "AGGREGATED_DRILL_DOWN"
				outClaimsDevelopmentLeanNet = "AGGREGATED_DRILL_DOWN"
				outContractFinancials = "AGGREGATED"
				outCoverUnderwritingInfo = "AGGREGATED_DRILL_DOWN"
				outFilteredUnderwritingInfo = "AGGREGATED_DRILL_DOWN"
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
	structures {
		subsegmentFilter {
			outClaimsCeded = "AGGREGATED_DRILL_DOWN"
			outClaimsGross = "AGGREGATED_DRILL_DOWN"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoGross = "AGGREGATED_DRILL_DOWN"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
}
