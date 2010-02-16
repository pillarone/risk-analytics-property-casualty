package models.example

model = models.example.ExampleModel
displayName = "All Aggregated"
components {
	indexProvider {
		outIndex = "AGGREGATED"
	}
	premiumCalculation {
		outPremium = "AGGREGATED"
		subsubcomponents {
			outPremium = "AGGREGATED"
		}
	}
}
