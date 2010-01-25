package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicSingleClaimsGeneratorInternalModelTests extends GroovyTestCase {

    DynamicSingleClaimsGeneratorInternalModel generator
    TestFrequencyUnderwritingProvider provider
    SingleClaimsGeneratorWithFrequencyExtractor generator0
    SingleClaimsGeneratorWithFrequencyExtractor generator1
    SingleClaimsGeneratorWithFrequencyExtractor generator2

    void setUp() {
        generator = new DynamicSingleClaimsGeneratorInternalModel()

        generator0 = new SingleClaimsGeneratorWithFrequencyExtractor(0)
        generator0.subClaimsGenerator = new SingleClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        generator0.subClaimsGenerator.validateParameterization()
        generator0.name = "subGenerator0"
        generator.addSubComponent(generator0)

        generator1 = new SingleClaimsGeneratorWithFrequencyExtractor(1)
        generator1.subClaimsGenerator = new SingleClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        generator1.subClaimsGenerator.validateParameterization()
        generator1.name = "subGenerator1"
        generator.addSubComponent(generator1)

        generator2 = new SingleClaimsGeneratorWithFrequencyExtractor(2)
        generator2.subClaimsGenerator = new SingleClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        generator2.subClaimsGenerator.validateParameterization()
        generator2.name = "subGenerator2"
        generator.addSubComponent(generator2)

        provider = new TestFrequencyUnderwritingProvider()
    }

    void testDefaultContracts() {
        generator = new DynamicSingleClaimsGeneratorInternalModel()
        SingleClaimsGeneratorWithFrequencyExtractor subGenerator = generator.createDefaultSubComponent()
        assertNotNull subGenerator
    }

    void testUsage() {
        WiringUtils.use(WireCategory) {
            generator.inFrequency = provider.outFrequency
            generator.inUnderwritingInfo = provider.outUnderwritingInfo
        }
        generator.wire()

        def probeFrequency = new TestProbe(provider, "outFrequency")
        List frequencies = probeFrequency.result

        def probeClaimsGenerator0 = new TestProbe(generator.componentList[0], "outClaims")
        List outClaimsGenerator0 = probeClaimsGenerator0.result

        def probeClaimsGenerator1 = new TestProbe(generator.componentList[1], "outClaims")
        List outClaimsGenerator1 = probeClaimsGenerator1.result

        def probeClaimsGenerator2 = new TestProbe(generator.componentList[2], "outClaims")
        List outClaimsGenerator2 = probeClaimsGenerator2.result

        provider.execute()
        assertEquals "2 claims generated", frequencies[0].value, outClaimsGenerator0.size()
        assertEquals "5 claims generated", frequencies[1].value, outClaimsGenerator1.size()
        assertEquals "7 claims generated", frequencies[2].value, outClaimsGenerator2.size()


    }
}