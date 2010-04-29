package org.pillarone.riskanalytics.domain.pc.generators.frequency

import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

class FrequencyGeneratorTests extends GroovyTestCase {

    UnderwritingSegment underwritingInfo
    FrequencyGenerator generator

    void testGeneratePoissonFrequency() {
        generateFrequency(new FrequencyGenerator(parmDistribution: DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 5])))
    }

    void testGenerateNegativeBinomialFrequency() {
        generateFrequency(new FrequencyGenerator(parmDistribution: DistributionType.getStrategy(FrequencyDistributionType.NEGATIVEBINOMIAL, ["gamma": 5, "p": 0.2])))
    }

    private void generateFrequency(FrequencyGenerator generator) {
        assertTrue(generator.outFrequency.isEmpty())
        generator.validateParameterization()
        generator.doCalculation()
        assertEquals("# generated frequency packets", 1, generator.outFrequency.size())
    }

    void testGenerateNoneExistingDistributionFrequency() {
        shouldFail(java.lang.IllegalStateException, {
            FrequencyGenerator generator = new FrequencyGenerator(parmDistribution: null)
            generator.validateParameterization()
            generator.doCalculation()
        })
    }

    void testIllegalBaseSelection() {
        shouldFail(java.lang.IllegalStateException, {
            FrequencyGenerator generator = new FrequencyGenerator(
                parmDistribution: DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 1]),
                parmBase: FrequencyBase.NUMBER_OF_POLICIES)
            generator.validateParameterization()
            generator.doCalculation()
        })
    }

    void testNumberOfPolicyUsed() {
        underwritingInfo = new UnderwritingSegment(
            parmPricePerExposureUnit: 10,
            parmWrittenExposure: 100,
            parmExposureDefinition: Exposure.ABSOLUTE
        )
        generator = new FrequencyGenerator(
            parmDistribution: DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 1]),
            parmBase: FrequencyBase.NUMBER_OF_POLICIES)
        WiringUtils.use(WireCategory) {
            generator.inUnderwritingInfo = underwritingInfo.outUnderwritingInfo
        }
        generator.validateParameterization()
        underwritingInfo.start()
    }
}