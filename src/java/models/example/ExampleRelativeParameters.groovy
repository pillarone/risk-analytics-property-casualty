package models.example

import org.pillarone.riskanalytics.domain.examples.groovy.IndexType

model=models.example.ExampleModel
periodCount=3
displayName='Moderate Inflation (Relative)'
applicationVersion='0.5'
components {
	premiumCalculation {
		subMtpl {
			parmPricePerPolicy[1]=50.0
			parmPricePerPolicy[2]=50.0
			parmPricePerPolicy[0]=50.0
			parmNumberOfPolicy[2]=1000.0
			parmNumberOfPolicy[0]=1000.0
			parmNumberOfPolicy[1]=1000.0
		}
		subFire {
			parmNumberOfPolicy[0]=1000.0
			parmNumberOfPolicy[1]=1100.0
			parmNumberOfPolicy[2]=900.0
			parmPricePerPolicy[1]=55.0
			parmPricePerPolicy[0]=50.0
			parmPricePerPolicy[2]=600.0
		}
	}
	indexProvider {
		parmIndex[2]=IndexType.getStrategy(IndexType.RELATIVEPRIORPERIOD, ["changeIndex":1.04,])
		parmIndex[1]=IndexType.getStrategy(IndexType.RELATIVEPRIORPERIOD, ["changeIndex":0.98,])
		parmIndex[0]=IndexType.getStrategy(IndexType.RELATIVEPRIORPERIOD, ["changeIndex":1.01,])
	}
}
