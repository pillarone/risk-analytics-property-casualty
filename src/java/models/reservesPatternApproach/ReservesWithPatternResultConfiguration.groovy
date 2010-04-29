package models.reservesPatternApproach

model = models.reservesPatternApproach.ReservesWithPatternModel
displayName = "Developed Claims (aggregated)"
components {
	claimDevelopment {
		outClaimsDevelopment = "AGGREGATED"
		outClaimsDevelopmentWithIBNR = "AGGREGATED"
	}
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATED"
		}
	}
}
