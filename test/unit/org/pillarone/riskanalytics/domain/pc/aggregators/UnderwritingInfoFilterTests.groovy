package org.pillarone.riskanalytics.domain.pc.aggregators

import models.test.StructureTestModel
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.parameterization.StructureInformation
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.wiring.Transmitter
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests
import org.pillarone.riskanalytics.core.example.component.TestComponent

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnderwritingInfoFilterTests extends GroovyTestCase {

    void testUsage() {
        Component component0 = new TestComponent()
        Component component1 = new TestComponent()
        Component component2 = new TestComponent()

        UnderwritingInfo grossUnderwritingInfo0 = UnderwritingInfoTests.getUnderwritingInfo()
        grossUnderwritingInfo0.origin = component0
        UnderwritingInfo grossUnderwritingInfo1 = UnderwritingInfoTests.getUnderwritingInfo2()
        grossUnderwritingInfo1.origin = component1
        UnderwritingInfo grossUnderwritingInfo2 = UnderwritingInfoTests.getUnderwritingInfo3()
        grossUnderwritingInfo2.origin = component2
        UnderwritingInfo cededUnderwritingInfo0 = UnderwritingInfoTests.getUnderwritingInfo()
        cededUnderwritingInfo0.originalUnderwritingInfo = grossUnderwritingInfo0
        cededUnderwritingInfo0.origin = component0
        UnderwritingInfo cededUnderwritingInfo2 = UnderwritingInfoTests.getUnderwritingInfo3()
        cededUnderwritingInfo2.originalUnderwritingInfo = grossUnderwritingInfo2
        cededUnderwritingInfo2.origin = component2

        UnderwritingInfoFilter underwritingInfoFilter = new UnderwritingInfoFilter()
        TestUnderwritingInfoContainer container = new TestUnderwritingInfoContainer()
        underwritingInfoFilter.allOutputTransmitter << new Transmitter(underwritingInfoFilter, underwritingInfoFilter.outUnderwritingInfo, container, container.outUnderwritingInfoGross)

        underwritingInfoFilter.inUnderwritingInfo << grossUnderwritingInfo0 << grossUnderwritingInfo1 << grossUnderwritingInfo2 << cededUnderwritingInfo0 << cededUnderwritingInfo2

        StructureInformation info = new StructureInformation(new ConfigObject(), new StructureTestModel())
        info.componentsOfLine[component0] = "fire"
        info.componentsOfLine[component1] = "hull"
        info.componentsOfLine[component2] = "legal"
        info.componentsOfLine[underwritingInfoFilter] = "fire"

        SimulationScope scope = new SimulationScope()
        scope.structureInformation = info
        underwritingInfoFilter.simulationScope = scope

        underwritingInfoFilter.doCalculation()
        assertEquals "#fire underwriting info", 2, underwritingInfoFilter.outUnderwritingInfo.size()

        assertEquals "fire gross premium 0", 2000, underwritingInfoFilter.outUnderwritingInfo[0].premiumWritten
        assertEquals "fire ceded premium 0", 2000, underwritingInfoFilter.outUnderwritingInfo[1].premiumWritten

        underwritingInfoFilter.reset()

        underwritingInfoFilter.inUnderwritingInfo << grossUnderwritingInfo0 << grossUnderwritingInfo1 << grossUnderwritingInfo2 << cededUnderwritingInfo0 << cededUnderwritingInfo2

        info = new StructureInformation(new ConfigObject(), new StructureTestModel())
        info.componentsOfLine[component0] = "fire"
        info.componentsOfLine[component1] = "hull"
        info.componentsOfLine[component2] = "legal"
        info.componentsOfLine[underwritingInfoFilter] = "hull"
        scope = new SimulationScope()
        scope.structureInformation = info
        underwritingInfoFilter.simulationScope = scope

        underwritingInfoFilter.doCalculation()
        assertEquals "#hull underwriting info", 1, underwritingInfoFilter.outUnderwritingInfo.size()

        assertEquals "hull gross premium 1", 500, underwritingInfoFilter.outUnderwritingInfo[0].premiumWritten
    }
}