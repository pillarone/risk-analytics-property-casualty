package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalSingleEQFloodStormClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceProgram3SerialContracts
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils

/**
 *  Compared with the ExampleLob the PropertyLob has different claims generators including cat claims.
 *
 * @author ali.majidi (at) munichre (dot) com
 */
class PropertyLob4RIPrograms extends ComposedComponent {

    UnderwritingSegment subUnderwriting
    AttritionalSingleEQFloodStormClaimsGenerator subClaimsGenerator
    ReinsuranceProgram3SerialContracts subRiProgramA
    ReinsuranceProgram3SerialContracts subRiProgramB
    ReinsuranceProgram3SerialContracts subRiProgramC
    ReinsuranceProgram3SerialContracts subRiProgramD

    PropertyLob4RIPrograms() {
        subUnderwriting = new UnderwritingSegment()
        subClaimsGenerator = new AttritionalSingleEQFloodStormClaimsGenerator()
        subRiProgramA = new ReinsuranceProgram3SerialContracts()
        subRiProgramB = new ReinsuranceProgram3SerialContracts()
        subRiProgramC = new ReinsuranceProgram3SerialContracts()
        subRiProgramD = new ReinsuranceProgram3SerialContracts()
    }

    public void doCalculation() {
        subUnderwriting.start()
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subRiProgramA.inClaims = subClaimsGenerator.outClaims
            subRiProgramB.inClaims = subClaimsGenerator.outClaims
            subRiProgramC.inClaims = subClaimsGenerator.outClaims
            subRiProgramD.inClaims = subClaimsGenerator.outClaims
            subRiProgramA.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subRiProgramB.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subRiProgramC.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subRiProgramD.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
        }
    }
}
