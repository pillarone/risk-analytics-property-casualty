package models.multiCompany

model = models.multiCompany.MultiCompanyModel
displayName = "Detailed Results"
components {
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
		subContracts {
			subcontracts {
				outClaimsDevelopmentLeanCeded = "AGGREGATED"
				outContractFinancials = "AGGREGATED"
				outCoverUnderwritingInfo = "AGGREGATED"
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
	underwritingSegments {
		outUnderwritingInfo = "AGGREGATED"
		subunderwritingSegments {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
