package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalSingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceProgram3SerialContracts
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 *  This example line of business contains an underwriting, claims generator and a
 *  reinsurance program. The later with a fixed number of three serial contracts.
 *  Furthermore several aggregators are included for the collection and aggregation of packets.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LobUnderwritingSegment extends ComposedComponent implements ISegmentMarker {

    UnderwritingSegment subUnderwriting
    AttritionalSingleClaimsGenerator subClaimsGenerator
    ReinsuranceProgram3SerialContracts subRiProgram

    LobUnderwritingSegment() {
        subUnderwriting = new UnderwritingSegment()
        subClaimsGenerator = new AttritionalSingleClaimsGenerator()
        subRiProgram = new ReinsuranceProgram3SerialContracts()
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subRiProgram.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subRiProgram.inClaims = subClaimsGenerator.outClaims
        }
    }
}
