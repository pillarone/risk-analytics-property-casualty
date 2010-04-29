package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

class FrequencyClaimsGeneratorTests extends GroovyTestCase {

    UnderwritingSegment underwritingSegment
    FrequencyClaimsGenerator generator

    void testGenerateClaims() {
        underwritingSegment = new UnderwritingSegment()
        generator = new FrequencyClaimsGenerator()
        generator.subFrequencyGenerator.parmDistribution = DistributionType.getStrategy(FrequencyDistributionType.POISSON, ["lambda": 10])
        generator.subClaimsGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.NORMAL, ["mean": 0, "stDev": 1])

        WiringUtils.use(WireCategory) {
            generator.inUnderwritingInfo = underwritingSegment.outUnderwritingInfo
        }
        generator.internalWiring()
        generator.validateParameterization()

        List frequency = new TestProbe(generator.subFrequencyGenerator, "outFrequency").result
        List claims = new TestProbe(generator.subClaimsGenerator, "outClaims").result


        assertTrue generator.outClaims.isEmpty()
        underwritingSegment.start()
        assertEquals("# claims generated", frequency[0].value, claims.size())
    }

}
