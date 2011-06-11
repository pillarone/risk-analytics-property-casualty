package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalSingleEQFloodStormClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceProgram3SerialContracts
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskBands
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocator
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 *  Compared with the ExampleLob the PropertyLob has a different claims generator including cat claims.
 *
 * @author ali.majidi (at) munichre (dot) com
 */
class PropertyLob extends ComposedComponent implements ISegmentMarker {
    RiskBands subUnderwriting
    AttritionalSingleEQFloodStormClaimsGenerator subClaimsGenerator
    ReinsuranceProgram3SerialContracts subRiProgram
    RiskAllocator subAllocator

    PropertyLob() {
        subUnderwriting = new RiskBands()
        subClaimsGenerator = new AttritionalSingleEQFloodStormClaimsGenerator()
        subRiProgram = new ReinsuranceProgram3SerialContracts()
        subAllocator = new RiskAllocator()
    }

    public void doCalculation() {
        subUnderwriting.start()
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subAllocator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subAllocator.inTargetDistribution = subUnderwriting.outAttritionalTargetDistribution

            subRiProgram.inUnderwritingInfo = subAllocator.outUnderwritingInfo
            subAllocator.inClaims = subClaimsGenerator.outClaims
            subRiProgram.inClaims = subAllocator.outClaims
        }
    }
}
