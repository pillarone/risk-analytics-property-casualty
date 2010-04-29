package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import models.test.StructureTestModel
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.IReinsurerMarker
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLineReinsuranceContractWithDefaultTests extends GroovyTestCase {

    static MultiLineReinsuranceContractWithDefault getContract0() {
        return new MultiLineReinsuranceContractWithDefault(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.5,
                                "coveredByReinsurer": 1d]),
                parmCoveredLines: new ComboBoxTableMultiDimensionalParameter(['fire'], ['Covered Lines'], LobMarker),
                parmReinsurer: new ConstrainedString(IReinsurerMarker, 'earth re'))
    }

    // todo(sku): try to fix it without a stub
//    void testUsageDefaultOccured() {
//        Component component1 = new TestComponent()
//        MultiLineReinsuranceContractWithDefault contract = getContract0()
//
//        Claim claimFire100 = new Claim(origin: component1, claimType: ClaimType.ATTRITIONAL, ultimate: 100d, fractionOfPeriod: 0.2)
//        contract.inClaims << claimFire100
//        contract.inUnderwritingInfo << new UnderwritingInfo(origin: component1, premiumWritten: 40)
//        contract.inReinsurersDefault << new ReinsurerDefault('earth re', true)
//
//        def probeContractUWI = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims
//
//        StubFor simulationContextStub = new StubFor(SimulationScope)
//        // 1 = 1 contract * 1 Claims Object * 1 Underwriting Objects
//        simulationContextStub.demand.getStructureInformation(6..6) {
//            StructureInformation info = new StructureInformation(new ConfigObject(), new StructureTestModel())
//            info.componentsOfLine[component1] = "fire"
//            return info
//        }
//        simulationContextStub.use {
//            SimulationScope scope = new SimulationScope()
//            scope.model = new StructureTestModel()
//            contract.simulationScope = scope
//            contract.doCalculation()
//        }
//
//        assertEquals "covered claim value 0", 0, contract.outCoveredClaims[0].ultimate
//        assertEquals "ceded premium 0", 0, contract.outCoverUnderwritingInfo[0].premiumWritten
//    }

    void testUsageNoDefaultOccured() {
        Component component1 = new TestComponent()
        MultiLineReinsuranceContractWithDefault contract = getContract0()

        Claim claimFire100 = new Claim(origin: component1, claimType: ClaimType.ATTRITIONAL, ultimate: 100d, fractionOfPeriod: 0.2)
        contract.inClaims << claimFire100
        contract.inUnderwritingInfo << new UnderwritingInfo(origin: component1, premiumWritten: 40)
        contract.inReinsurersDefault << new ReinsurerDefault('earth re', false)

        def probeContractUWI = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims

        SimulationScope scope = new SimulationScope()
        scope.model = new StructureTestModel()
        contract.simulationScope = scope
        contract.doCalculation()
//todo(sku): fix
//        assertEquals "covered claim value 50", 30, contract.outCoveredClaims[0].ultimate
//        assertEquals "ceded premium 20", 20, contract.outCoverUnderwritingInfo[0].premiumWritten
    }
}