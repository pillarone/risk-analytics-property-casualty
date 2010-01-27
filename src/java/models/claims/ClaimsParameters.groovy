package models.claims

import models.claims.ClaimsModel
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory

model = models.claims.ClaimsModel
periodCount = 1
displayName = 'Example'
components {
    claimsGenerators {
        subAttritional {
            parmAssociateExposureInfo[0] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])
            parmClaimsModel[0] = ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase": Exposure.ABSOLUTE, "claimsSizeDistribution": RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, [stDev: 0.56, mean: 100.0]), "claimsSizeModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),])
            parmUnderwritingInformation[0] = new ComboBoxTableMultiDimensionalParameter([""], ["Underwriting Information"], IUnderwritingInfoMarker)
        }
    }
}
