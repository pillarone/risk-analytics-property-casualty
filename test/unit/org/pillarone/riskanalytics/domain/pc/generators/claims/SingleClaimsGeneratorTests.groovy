package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SingleClaimsGeneratorTests extends GroovyTestCase {

    TestFrequencyProvider frequency = new TestFrequencyProvider()
    TestFrequencyUnderwritingProvider provider = new TestFrequencyUnderwritingProvider()
    SingleClaimsGenerator generator = new SingleClaimsGenerator(parmDistribution: DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": 1]))

    void testGenerateLognormalClaims() {
        generator = new SingleClaimsGenerator(parmDistribution: DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        generateClaims()
    }

    void testGenerateNormalClaims() {
        generator = new SingleClaimsGenerator(parmDistribution: DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 1000, "stDev": 20]))
        generateClaims()
    }

    private void generateClaims() {
        WiringUtils.use(WireCategory) {
            generator.inClaimCount = frequency.outFrequency
        }
        frequency.outFrequency << new Frequency(value: 5)

        assertTrue generator.outClaims.isEmpty()
        generator.validateParameterization()

        def probeClaims = new TestProbe(generator, "outClaims")
        List claims = probeClaims.result

        frequency.start()

        assertEquals(5, claims.size())
    }

    void testNoGeneratorSet() {
        shouldFail(java.lang.IllegalStateException, {
            SingleClaimsGenerator generator = new SingleClaimsGenerator(parmDistribution: null)
            generator.validateParameterization()
        })
    }

    void testIllegalExposure() {
        shouldFail(java.lang.IllegalStateException, {
            SingleClaimsGenerator generator = new SingleClaimsGenerator(
                parmDistribution: DistributionType.getStrategy(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]),
                parmBase: Exposure.PREMIUM_WRITTEN)
            generator.validateParameterization()
        })
    }

    void testClaimSize0IfFrequency0() {
        generator = new SingleClaimsGenerator(
            parmDistribution: DistributionType.getStrategy(ClaimSizeDistributionType.PARETO, ["alpha": 0.523, "beta": 500000]),
            parmModification: DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min": 500000d, 'max': 20000000d])
        )
        WiringUtils.use(WireCategory) {
            generator.inClaimCount = frequency.outFrequency
        }
        frequency.outFrequency << new Frequency(value: 0)
        generator.validateParameterization()

        def probeClaims = new TestProbe(generator, "outClaims")
        List claims = probeClaims.result

        frequency.start()

        assertEquals "one claim", 1, claims.size()
        assertEquals "claim of size 0", 0, claims[0].ultimate
    }

    void testSeveralUnderwritingPackets() {
        generator.parmBase = Exposure.PREMIUM_WRITTEN
        WiringUtils.use(WireCategory) {
            generator.inClaimCount = provider.outFrequency
            generator.inUnderwritingInfo = provider.outUnderwritingInfo
        }
        generator.validateParameterization()

        def probeFrequency = new TestProbe(provider, "outFrequency")
        List frequencies = probeFrequency.result

        def probeClaimsGenerator = new TestProbe(generator, "outClaims")
        List outClaimsGenerator = probeClaimsGenerator.result

        provider.execute()
        assertEquals "claims generated according to frequency", frequencies.value.sum(), outClaimsGenerator.size()
        assertEquals "claim size", 11000, outClaimsGenerator[0].ultimate
    }
}

class TestFrequencyProvider extends Component {

    PacketList<Frequency> outFrequency = new PacketList(Frequency)

    public void doCalculation() {
    }
}
