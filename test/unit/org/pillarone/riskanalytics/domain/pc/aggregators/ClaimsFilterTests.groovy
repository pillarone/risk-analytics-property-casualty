package org.pillarone.riskanalytics.domain.pc.aggregators

import models.test.StructureTestModel
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.parameterization.StructureInformation
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.wiring.Transmitter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsFilterTests extends GroovyTestCase {

    void testUsage() {
        Component component1 = new TestComponent()
        Component component2 = new TestComponent()
        Component component3 = new TestComponent()

        Claim grossClaim1 = new Claim(origin: component1, claimType: ClaimType.SINGLE, ultimate: 30)
        Claim grossClaim2 = new Claim(origin: component1, claimType: ClaimType.SINGLE, ultimate: 20)
        Claim grossClaim3 = new Claim(origin: component2, claimType: ClaimType.SINGLE, ultimate: 20)
        Claim cededClaim1 = new Claim(origin: component1, claimType: ClaimType.SINGLE, ultimate: 20, originalClaim: grossClaim1)
        Claim cededClaim3 = new Claim(origin: component2, claimType: ClaimType.SINGLE, ultimate: 5, originalClaim: grossClaim3)

        ClaimsFilter claimsFilter = new ClaimsFilter()
        TestClaimsContainer container = new TestClaimsContainer()
        claimsFilter.allOutputTransmitter << new Transmitter(claimsFilter, claimsFilter.outClaims, container, container.outClaimsGross)

        claimsFilter.inClaims << grossClaim1 << grossClaim2 << grossClaim3 << cededClaim1 << cededClaim3

        StructureInformation info = new StructureInformation(new ConfigObject(), new StructureTestModel())
        info.componentsOfLine[component1] = "fire"
        info.componentsOfLine[component2] = "hull"
        info.componentsOfLine[component3] = "legal"
        info.componentsOfLine[claimsFilter] = "fire"
        SimulationScope scope = new SimulationScope()
        scope.structureInformation = info
        claimsFilter.simulationScope = scope

        claimsFilter.doCalculation()
        assertTrue "#fire claims", 3 == claimsFilter.outClaims.size()

        assertEquals "fire gross claim incurred id1", 30, claimsFilter.outClaims[0].ultimate
        assertEquals "fire gross claim incurred id2", 20, claimsFilter.outClaims[1].ultimate
        assertEquals "fire ceded claim incurred id1", 20, claimsFilter.outClaims[2].ultimate

        claimsFilter.reset()

        claimsFilter.inClaims << grossClaim1 << grossClaim2 << grossClaim3 << cededClaim1 << cededClaim3

        info = new StructureInformation(new ConfigObject(), new StructureTestModel())
        info.componentsOfLine[component1] = "fire"
        info.componentsOfLine[component2] = "hull"
        info.componentsOfLine[component3] = "legal"
        info.componentsOfLine[claimsFilter] = "hull"
        scope = new SimulationScope()
        scope.structureInformation = info
        claimsFilter.simulationScope = scope

        claimsFilter.doCalculation()
        assertTrue "#hull claims", 2 == claimsFilter.outClaims.size()

        assertEquals "hull claim incurred id1", 20, claimsFilter.outClaims[0].ultimate
        assertEquals "hull claim incurred id2", 5, claimsFilter.outClaims[1].ultimate
    }
}