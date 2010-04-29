package models.densityestimation

import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.DistributionType

model=models.densityestimation.TestDensitiesModel
periodCount=1
all=0..0

components {
	claims {
		parmDistribution[0]=DistributionType.getStrategy(DistributionType.PARETO, ["beta":100.0, "alpha":2.0])
		parmBase[0]=Exposure.ABSOLUTE
	}
	sl {
		parmContractStrategy[0]= ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["limit":600.0,"attachmentPoint":400.0,"premium":0.0,"premiumBase":PremiumBase.ABSOLUTE, "coveredByReinsurer": 1d])
		parmInuringPriority[0]=0
	}
	triModalClaims {
		parmDistribution[0]=DistributionType.getStrategy(DistributionType.NORMAL, ["stDev":5.0, "mean":100.0])
		parmShifts[0]=new SimpleMultiDimensionalParameter([85.0,100.0])
		parmMixDistribution[0] = DistributionType.getStrategy(DistributionType.UNIFORM, ["a":0.0, "b":1.0])
	}
}
