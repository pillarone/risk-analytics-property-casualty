package models.claims

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

model = models.claims.ClaimsModel
periodCount = 1
displayName = 'Example'
components {
    claimsGenerators {
        subAttritional {
            parmAssociateExposureInfo[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
            parmClaimsModel[0] = ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase": Exposure.ABSOLUTE, "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.LOGNORMAL, [stDev: 0.56, mean: 100.0]), "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
            parmUnderwritingInformation[0] = new ComboBoxTableMultiDimensionalParameter([""], ["Underwriting Information"], IUnderwritingInfoMarker)
        }
    }
}
