package models.sparrow

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

model=models.sparrow.SparrowModel
periodCount=2
displayName='SparrowConstantParameters'
components {
	frequencyGenerator {
		parmBase[1]=FrequencyBase.ABSOLUTE
		parmBase[0]=FrequencyBase.ABSOLUTE
		parmDistribution[1]=DistributionType.getStrategy(DistributionType.CONSTANT, ["constant":10.0])
		parmDistribution[0]=DistributionType.getStrategy(DistributionType.CONSTANT, ["constant":10.0])
	}
	claimsGenerator {
		parmDistribution[1]=DistributionType.getStrategy(DistributionType.CONSTANT, ["constant":10.0])
		parmDistribution[0]=DistributionType.getStrategy(DistributionType.CONSTANT, ["constant":10.0])
		parmModification[1]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
		parmModification[0]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
		parmBase[1]=Exposure.ABSOLUTE
		parmBase[0]=Exposure.ABSOLUTE
	}
}
