package models.multiCompany

model = models.multiCompany.MultiCompanyModel
displayName = "Aggregated Overview 3"
components {
	claimsGenerators {
		outClaimsLeanDevelopment = "AGGREGATED"
		subsubcomponents {
			outClaimsLeanDevelopment = "AGGREGATED"
		}
	}
	companies {
		subsubcomponents {
			outClaimsLeanDevelopmentGross = "AGGREGATED"
			outClaimsLeanDevelopmentCeded = "AGGREGATED"
			outClaimsLeanDevelopmentNet = "AGGREGATED"
            outUnderwritingInfoGross = "AGGREGATED"
            outUnderwritingInfoCeded = "AGGREGATED"
            outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	linesOfBusiness {
		sublineOfBusiness {
			outClaimsDevelopmentLeanCeded = "AGGREGATED"
			outClaimsDevelopmentLeanGross = "AGGREGATED"
			outClaimsDevelopmentLeanNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	reinsuranceMarket {
		outClaimsDevelopmentLeanCeded = "AGGREGATED"
		outClaimsDevelopmentLeanGross = "AGGREGATED"
		outClaimsDevelopmentLeanNet = "AGGREGATED"
		outCoverUnderwritingInfo = "AGGREGATED"
		outNetAfterCoverUnderwritingInfo = "AGGREGATED"
		outUnderwritingInfo = "AGGREGATED"
	}
	underwritingSegments {
		outUnderwritingInfo = "AGGREGATED"
		subunderwritingSegments {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
