package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalMultiSingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceProgram3SerialContracts
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskBands
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocator
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 *  This example line of business contains an underwriting, claims generator and a
 *  reinsurance program. The later with a fixed number of three serial contracts.
 *  Furthermore several aggregators are included for the collection and aggregation of packets.
 *
 * @author j.dittrich (at) intuitive-collaboration (dot) com
 */
class MultiSingleLob extends ComposedComponent implements ISegmentMarker {

    RiskBands subUnderwriting
    AttritionalMultiSingleClaimsGenerator subClaimsGenerator
    ReinsuranceProgram3SerialContracts subRiProgram
    RiskAllocator subAllocator

    MultiSingleLob() {
        subUnderwriting = new RiskBands()
        subClaimsGenerator = new AttritionalMultiSingleClaimsGenerator()
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
