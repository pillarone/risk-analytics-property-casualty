package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBandsTests

/**
 * @author michael-noe (at) web (dot) de, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SingleClaimsGeneratorWithFrequencyExtractorTests extends GroovyTestCase {

    SingleClaimsGeneratorWithFrequencyExtractor generator0
    SingleClaimsGeneratorWithFrequencyExtractor generator1
    TestFrequencyUnderwritingProvider provider

    void setUp() {
        generator0 = new SingleClaimsGeneratorWithFrequencyExtractor(0)
        generator0.subClaimsGenerator = new SingleClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        generator0.subClaimsGenerator.validateParameterization()
        generator1 = new SingleClaimsGeneratorWithFrequencyExtractor(1)
        generator1.subClaimsGenerator = new SingleClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        generator1.subClaimsGenerator.validateParameterization()
        provider = new TestFrequencyUnderwritingProvider()
    }

    void testUsage() {
        generator0.wire()
        generator1.wire()
        WiringUtils.use(WireCategory) {
            generator0.inFrequency = provider.outFrequency
            generator1.inFrequency = provider.outFrequency
            generator0.inUnderwritingInfo = provider.outUnderwritingInfo
            generator1.inUnderwritingInfo = provider.outUnderwritingInfo
        }

        def probeFrequency = new TestProbe(provider, "outFrequency")
        List frequencies = probeFrequency.result

        def probeClaimsGenerator0 = new TestProbe(generator0, "outClaims")
        List outClaimsGenerator0 = probeClaimsGenerator0.result

        def probeClaimsGenerator1 = new TestProbe(generator1, "outClaims")
        List outClaimsGenerator1 = probeClaimsGenerator1.result

        provider.execute()
        assertEquals "2 claims generated", frequencies[0].value, outClaimsGenerator0.size()
        assertEquals "5 claims generated", frequencies[1].value, outClaimsGenerator1.size()
    }
}

class TestFrequencyUnderwritingProvider extends Component {

    PacketList<Frequency> outFrequency = new PacketList(Frequency)
    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)

    protected void doCalculation() {
        outUnderwritingInfo.addAll RiskBandsTests.underwritingInfos
        outFrequency << new Frequency(value: 2) << new Frequency(value: 5) << new Frequency(value: 7)
    }

}