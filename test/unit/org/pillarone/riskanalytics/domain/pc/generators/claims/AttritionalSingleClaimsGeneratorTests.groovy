package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalSingleClaimsGeneratorTests extends GroovyTestCase {

    void testGenerateClaims() {
        AttritionalSingleClaimsGenerator generator = new AttritionalSingleClaimsGenerator()
        generator.internalWiring()
        generator.subSingleClaimsGenerator.subFrequencyGenerator.parmDistribution = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 10])
        generator.subSingleClaimsGenerator.subClaimsGenerator.parmDistribution = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 0, "stDev": 1])
        generator.subAttritionalClaimsGenerator.parmDistribution = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ["mean": 10, "stDev": 2])
        generator.validateParameterization()

        List frequency = new TestProbe(generator.subSingleClaimsGenerator.subFrequencyGenerator, "outFrequency").result
        List claims = new TestProbe(generator, "outClaims").result

        assertTrue generator.outClaims.isEmpty()
        generator.start()

        assertEquals("# claims generated", frequency[0].value, claims.size() - 1)
        assertEquals "# attritional claims", 1, claims*.claimType.count(ClaimType.ATTRITIONAL)
    }

    void testNoGeneratorSet() {
        shouldFail(java.lang.IllegalStateException, {
            AttritionalSingleClaimsGenerator generator = new AttritionalSingleClaimsGenerator()
            generator.subSingleClaimsGenerator.subFrequencyGenerator.parmDistribution = null
            generator.subSingleClaimsGenerator.subFrequencyGenerator.validateParameterization()
        })
    }
}