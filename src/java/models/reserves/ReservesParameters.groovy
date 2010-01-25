package models.reserves

import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker

model=models.reserves.ReservesModel
periodCount=3
allPeriods=0..<periodCount
displayName='Prior Period Example'
components {
	claimsGenerators {
		subAttritional {
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[allPeriods]=ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL,
                    ["claimsSizeBase":Exposure.ABSOLUTE,
                            "claimsSizeDistribution":RandomDistributionFactory.getDistribution(DistributionType.NORMAL,
                                    [mean:1000.0, stDev:100.0]),
                            "claimsSizeModification":DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[allPeriods]=0.8
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
	}
	reserveGenerators {
		subAttrirional {
			parmDistribution[allPeriods]=RandomDistributionFactory.getDistribution(DistributionType.NORMAL, [mean:100.0, stDev:25.0])
			parmInitialReserves[0]=500.0
			parmInitialReserves[1..2]=0.0
			parmModification[allPeriods]=DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])
			parmPeriodPaymentPortion[allPeriods]=0.6
			parmReservesModel[allPeriods]=ReservesGeneratorStrategyType.getStrategy(ReservesGeneratorStrategyType.PRIOR_PERIOD,
                    ["basedOnClaimsGenerators":new ComboBoxTableMultiDimensionalParameter([],["Claims Generators"], PerilMarker),])
		}
	}
}
