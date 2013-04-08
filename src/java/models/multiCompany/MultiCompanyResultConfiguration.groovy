package models.multiCompany

model = models.multiCompany.MultiCompanyModel
displayName = "Aggregated Overview"
components {
	almGenerators {
		subsubcomponents {
			outAlmResult = "AGGREGATED"
		}
	}
	claimsGenerators {
		outClaimsLeanDevelopment = "AGGREGATED"
		subsubcomponents {
			outClaimsLeanDevelopment = "AGGREGATED"
		}
	}
	companies {
		subsubcomponents {
			outClaimsLeanDevelopmentCeded = "AGGREGATED"
			outClaimsLeanDevelopmentGross = "AGGREGATED"
			outClaimsLeanDevelopmentGrossPrimaryInsurer = "AGGREGATED"
			outClaimsLeanDevelopmentGrossReinsurer = "AGGREGATED"
			outClaimsLeanDevelopmentNet = "AGGREGATED"
			outClaimsLeanDevelopmentNetPrimaryInsurer = "AGGREGATED"
			outFinancialResults = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoGrossPrimaryInsurer = "AGGREGATED"
			outUnderwritingInfoGrossReinsurer = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
			outUnderwritingInfoNetPrimaryInsurer = "AGGREGATED"
		}
	}
	linesOfBusiness {
		sublineOfBusiness {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
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
