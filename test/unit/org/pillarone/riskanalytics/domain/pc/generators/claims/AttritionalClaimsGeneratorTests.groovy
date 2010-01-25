package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity
import org.pillarone.riskanalytics.domain.pc.constants.Exposure

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsGeneratorTests extends GroovyTestCase {
    AttritionalClaimsGenerator generator
    TestProbabilityProvider probability
    UnderwritingSegment underwritingSegment0
    UnderwritingSegment underwritingSegment1

    void testGenerateLognormalClaims() {
        generateClaims(new AttritionalClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10])))
    }

    void testGenerateNormalClaims() {
        generateClaims(new AttritionalClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 1000, "stDev": 20])))
    }

    private void generateClaims(AttritionalClaimsGenerator generator) {
        assertTrue generator.outClaims.isEmpty()
        generator.validateParameterization()
        generator.doCalculation()
        assertEquals(1, generator.outClaims.size())
    }

    void testNoGeneratorSet() {
        shouldFail(java.lang.IllegalStateException, {
            generator = new AttritionalClaimsGenerator(parmDistribution: null)
            generator.validateParameterization()
        })
    }

    void testInvalidBase() {
        generator = new AttritionalClaimsGenerator(
            parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]),
            parmBase: Exposure.PREMIUM_WRITTEN
        )
        shouldFail(java.lang.IllegalStateException) {
            generator.validateParameterization()
        }
    }

    void testMoreThanOneUnderwritingInfoSourceWired() {
        generator = new AttritionalClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        underwritingSegment0 = new UnderwritingSegment()
        underwritingSegment1 = new UnderwritingSegment()

        WiringUtils.use(WireCategory) {
            generator.inUnderwritingInfo = underwritingSegment0.outUnderwritingInfo
            generator.inUnderwritingInfo = underwritingSegment1.outUnderwritingInfo
        }
        shouldFail(IllegalStateException) {
            generator.validateWiring()
        }
    }

    void testGenerationByInverseDistribution() {
        generator = new AttritionalClaimsGenerator(
            parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 0, "stDev": 1]))
        probability = new TestProbabilityProvider()

        WiringUtils.use(WireCategory) {
            generator.inProbability = probability.outProbabilities
        }

        def probeClaims = new TestProbe(generator, "outClaims")
        List claims = probeClaims.result

        probability.outProbabilities << new Severity(0.5)
        probability.start()

        assertEquals "probability 0.5", 0, claims[0].ultimate
    }

    void testGenerationByInverseDistributionNoProbabilityReceived() {
        generator = new AttritionalClaimsGenerator(
            parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 0, "stDev": 1]))
        probability = new TestProbabilityProvider()

        WiringUtils.use(WireCategory) {
            generator.inProbability = probability.outProbabilities
        }

        def probeClaims = new TestProbe(generator, "outClaims")
        List claims = probeClaims.result

        probability.start()
        assertTrue "one claim generated", claims.size() == 1
    }

    void testGenerationByInverseDistributionMoreThanOneProbability() {
        generator = new AttritionalClaimsGenerator(
            parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 0, "stDev": 1]))
        probability = new TestProbabilityProvider()

        WiringUtils.use(WireCategory) {
            generator.inProbability = probability.outProbabilities
        }

        probability.outProbabilities << new Severity(0.5) << new Severity(0.75)
        shouldFail(java.lang.IllegalStateException) {
            probability.start()
        }
    }
}

class TestProbabilityProvider extends Component {

    PacketList<Severity> outProbabilities = new PacketList(Severity)

    public void doCalculation() {
    }
}
