package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBands
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import models.claims.ClaimsModel

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class TypableClaimsGeneratorTests extends GroovyTestCase {

    TypableClaimsGenerator claimsGenerator
    RiskBands riskBands = new RiskBands()

    void setUp() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorStrategyFactory.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": RandomDistributionFactory.getDistribution(DistributionType.CONSTANT,
                                [constant: 123]),
                        "claimsSizeModification": DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),]))
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
    }

    void testUsage() {
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", 123, claimsGenerator.outClaims[0].ultimate
    }

    void testRelativeClaims() {
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", 123, claimsGenerator.outClaims[0].ultimate
    }
}