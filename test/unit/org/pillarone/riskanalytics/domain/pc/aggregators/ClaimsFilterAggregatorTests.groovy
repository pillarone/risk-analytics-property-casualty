package org.pillarone.riskanalytics.domain.pc.aggregators

import groovy.mock.interceptor.StubFor
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.parameterization.StructureInformation
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.wiring.Transmitter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.core.example.component.TestComponent
import models.test.StructureTestModel

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ClaimsFilterAggregatorTests extends GroovyTestCase {

    void testUsage() {
        Component component1 = new TestComponent()
        Component component2 = new TestComponent()
        Component component3 = new TestComponent()

        Claim grossClaim1 = new Claim(origin: component1, claimType: ClaimType.SINGLE, ultimate: 30)
        Claim grossClaim2 = new Claim(origin: component1, claimType: ClaimType.SINGLE, ultimate: 20)
        Claim grossClaim3 = new Claim(origin: component2, claimType: ClaimType.SINGLE, ultimate: 20)
        Claim cededClaim1 = new Claim(origin: component1, claimType: ClaimType.SINGLE, ultimate: 20, originalClaim: grossClaim1)
        Claim cededClaim3 = new Claim(origin: component2, claimType: ClaimType.SINGLE, ultimate: 5, originalClaim: grossClaim3)

        ClaimsFilterAggregator claimsFilterAggregator = new ClaimsFilterAggregator()
        claimsFilterAggregator.internalWiring()

        TestClaimsContainer container = new TestClaimsContainer()
        claimsFilterAggregator.allOutputTransmitter << new Transmitter(claimsFilterAggregator, claimsFilterAggregator.outClaimsGross,
            container, container.outClaimsGross)
        claimsFilterAggregator.allOutputTransmitter << new Transmitter(claimsFilterAggregator, claimsFilterAggregator.outClaimsCeded,
            container, container.outClaimsCeded)
        claimsFilterAggregator.allOutputTransmitter << new Transmitter(claimsFilterAggregator, claimsFilterAggregator.outClaimsNet,
            container, container.outClaimsNet)

        claimsFilterAggregator.inClaimsGross << grossClaim1 << grossClaim2 << grossClaim3
        claimsFilterAggregator.inClaimsCeded << cededClaim1 << cededClaim3

        StubFor simulationScopeStube = new StubFor(SimulationScope)
        // 10 = number of claims * number doCalculation() calls
        simulationScopeStube.demand.getStructureInformation(10..10) {
            StructureInformation info = new StructureInformation(new ConfigObject(), new StructureTestModel())
            info.componentsOfLine[component1] = "fire"
            info.componentsOfLine[component2] = "hull"
            info.componentsOfLine[component3] = "legal"
            info.componentsOfLine[claimsFilterAggregator.subClaimsGrossFilter] = "fire"
            info.componentsOfLine[claimsFilterAggregator.subClaimsCededFilter] = "fire"
            return info
        }
        simulationScopeStube.use {
            SimulationScope scope = new SimulationScope()
            claimsFilterAggregator.subClaimsGrossFilter.simulationScope = scope
            claimsFilterAggregator.subClaimsCededFilter.simulationScope = scope

            claimsFilterAggregator.doCalculation()
            assertTrue "#fire gross claims", 1 == claimsFilterAggregator.outClaimsGross.size()
            assertTrue "#fire ceded claims", 1 == claimsFilterAggregator.outClaimsCeded.size()
            assertTrue "#fire net claims", 1 == claimsFilterAggregator.outClaimsNet.size()

            assertEquals "fire gross claim incurred id1", 50, claimsFilterAggregator.outClaimsGross[0].ultimate
            assertEquals "fire ceded claim incurred id1", 20, claimsFilterAggregator.outClaimsCeded[0].ultimate
            assertEquals "fire net claim incurred id1", 30, claimsFilterAggregator.outClaimsNet[0].ultimate
        }

        claimsFilterAggregator.reset()

        claimsFilterAggregator.inClaimsGross << grossClaim1 << grossClaim2 << grossClaim3
        claimsFilterAggregator.inClaimsCeded << cededClaim1 << cededClaim3

        simulationScopeStube = new StubFor(SimulationScope)
        // 10 = number of claims * number doCalculation() calls
        simulationScopeStube.demand.getStructureInformation(10..10) {
            StructureInformation info = new StructureInformation(new ConfigObject(), new StructureTestModel())
            info.componentsOfLine[component1] = "fire"
            info.componentsOfLine[component2] = "hull"
            info.componentsOfLine[component3] = "legal"
            info.componentsOfLine[claimsFilterAggregator.subClaimsGrossFilter] = "hull"
            info.componentsOfLine[claimsFilterAggregator.subClaimsCededFilter] = "hull"
            return info
        }
        simulationScopeStube.use {
            SimulationScope scope = new SimulationScope()
            claimsFilterAggregator.subClaimsGrossFilter.simulationScope = scope
            claimsFilterAggregator.subClaimsCededFilter.simulationScope = scope

            claimsFilterAggregator.doCalculation()
            assertTrue "#hull claims", 1 == claimsFilterAggregator.outClaimsGross.size()

            assertEquals "hull gross claim incurred", 20, claimsFilterAggregator.outClaimsGross[0].ultimate
            assertEquals "hull ceded claim incurred", 5, claimsFilterAggregator.outClaimsCeded[0].ultimate
        }
    }
}