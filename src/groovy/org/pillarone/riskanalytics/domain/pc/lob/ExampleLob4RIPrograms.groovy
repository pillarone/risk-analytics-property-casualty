package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalSingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceProgram3SerialContracts
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskBands
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocator
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 *  This example line of business contains an underwriting, claims generator and a
 *  reinsurance program. The later with a fixed number of three serial contracts.
 *  Furthermore several aggregators are included for the collection and aggregation of packets.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['SEGMENT'])
class ExampleLob4RIPrograms extends ComposedComponent {
    RiskBands subUnderwriting
    AttritionalSingleClaimsGenerator subClaimsGenerator
    ReinsuranceProgram3SerialContracts subRiProgramA
    ReinsuranceProgram3SerialContracts subRiProgramB
    ReinsuranceProgram3SerialContracts subRiProgramC
    ReinsuranceProgram3SerialContracts subRiProgramD
    RiskAllocator subAllocator

    ExampleLob4RIPrograms() {
        subUnderwriting = new RiskBands()
        subClaimsGenerator = new AttritionalSingleClaimsGenerator()
        subRiProgramA = new ReinsuranceProgram3SerialContracts()
        subRiProgramB = new ReinsuranceProgram3SerialContracts()
        subRiProgramC = new ReinsuranceProgram3SerialContracts()
        subRiProgramD = new ReinsuranceProgram3SerialContracts()
        subAllocator = new RiskAllocator()
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subAllocator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subAllocator.inTargetDistribution = subUnderwriting.outAttritionalTargetDistribution
            subAllocator.inClaims = subClaimsGenerator.outClaims
            subRiProgramA.inUnderwritingInfo = subAllocator.outUnderwritingInfo
            subRiProgramA.inClaims = subAllocator.outClaims
            subRiProgramB.inUnderwritingInfo = subAllocator.outUnderwritingInfo
            subRiProgramB.inClaims = subAllocator.outClaims
            subRiProgramC.inUnderwritingInfo = subAllocator.outUnderwritingInfo
            subRiProgramC.inClaims = subAllocator.outClaims
            subRiProgramD.inUnderwritingInfo = subAllocator.outUnderwritingInfo
            subRiProgramD.inClaims = subAllocator.outClaims
        }
    }
}
