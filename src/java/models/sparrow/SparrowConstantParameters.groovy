package models.sparrow

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase

model=models.sparrow.SparrowModel
periodCount=2
displayName='SparrowConstantParameters'
components {
	frequencyGenerator {
		parmBase[1]=FrequencyBase.ABSOLUTE
		parmBase[0]=FrequencyBase.ABSOLUTE
		parmDistribution[1]=RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant":10.0])
		parmDistribution[0]=RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant":10.0])
	}
	claimsGenerator {
		parmDistribution[1]=RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant":10.0])
		parmDistribution[0]=RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant":10.0])
		parmModification[1]=DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
		parmModification[0]=DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
		parmBase[1]=Exposure.ABSOLUTE
		parmBase[0]=Exposure.ABSOLUTE
	}
}
