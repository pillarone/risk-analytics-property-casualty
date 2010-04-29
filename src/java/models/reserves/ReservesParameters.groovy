package models.reserves

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ReservesGeneratorStrategyType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

model=models.reserves.ReservesModel
periodCount=3
allPeriods=0..<periodCount
displayName='Prior Period Example'
components {
	claimsGenerators {
		subAttritional {
			parmAssociateExposureInfo[allPeriods]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[allPeriods]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL,
                    ["claimsSizeBase":Exposure.ABSOLUTE,
                            "claimsSizeDistribution":DistributionType.getStrategy(DistributionType.NORMAL,
                                    [mean:1000.0, stDev:100.0]),
                            "claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
			parmPeriodPaymentPortion[allPeriods]=0.8
			parmUnderwritingInformation[allPeriods]=new ComboBoxTableMultiDimensionalParameter([""],["Underwriting Information"], IUnderwritingInfoMarker)
		}
	}
	reserveGenerators {
		subAttrirional {
			parmDistribution[allPeriods]=DistributionType.getStrategy(DistributionType.NORMAL, [mean:100.0, stDev:25.0])
			parmInitialReserves[0]=500.0
			parmInitialReserves[1..2]=0.0
			parmModification[allPeriods]=DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
			parmPeriodPaymentPortion[allPeriods]=0.6
			parmReservesModel[allPeriods]=ReservesGeneratorStrategyType.getStrategy(ReservesGeneratorStrategyType.PRIOR_PERIOD,
                    ["basedOnClaimsGenerators":new ComboBoxTableMultiDimensionalParameter([],["Claims Generators"], PerilMarker),])
		}
	}
}
