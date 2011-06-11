package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalSingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 *  This example line of business contains an underwriting, claims generator and a
 *  reinsurance program. The later with a fixed number of three serial contracts.
 *  Furthermore several aggregators are included for the collection and aggregation of packets.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class Lob extends ComposedComponent implements ISegmentMarker {

    UnderwritingSegment subUnderwriting
    AttritionalSingleClaimsGenerator subClaimsGenerator

    PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList(UnderwritingInfo)

    PacketList<Claim> outClaims = new PacketList(Claim)
    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)

    Lob() {
        subUnderwriting = new UnderwritingSegment()
        subClaimsGenerator = new AttritionalSingleClaimsGenerator()
    }

    public void doCalculation() {
        subUnderwriting.start()
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
        }
        WiringUtils.use(PortReplicatorCategory) {
            this.outUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            this.outClaims = subClaimsGenerator.outClaims
        }
    }
}