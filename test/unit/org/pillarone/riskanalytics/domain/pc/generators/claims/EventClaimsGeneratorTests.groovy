package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverity
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class EventClaimsGeneratorTests extends GroovyTestCase {
    void testGenerateLognormalClaims() {
        generateClaims(new EventClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(DistributionType.LOGNORMAL, ["mean": 5, "stDev": 10])))
    }

    void testGenerateNormalClaims() {
        generateClaims(new EventClaimsGenerator(parmDistribution: RandomDistributionFactory.getDistribution(DistributionType.NORMAL, ["mean": 10, "stDev": 2])))
    }

    private void generateClaims(EventClaimsGenerator generator) {
        generator.inSeverities = new PacketList([
            new EventSeverity(value: 0.0, event: new Event()),
            new EventSeverity(value: 0.24, event: new Event()),
            new EventSeverity(value: 1.0, event: new Event())])

        assertTrue generator.outClaims.isEmpty()
        generator.doCalculation()
        assertEquals(3, generator.outClaims.size())
    }

    void testNoGeneratorSet() {
        shouldFail(java.lang.IllegalStateException, {
            EventClaimsGenerator generator = new EventClaimsGenerator(parmDistribution: null)
            generator.doCalculation()
        })
    }
}