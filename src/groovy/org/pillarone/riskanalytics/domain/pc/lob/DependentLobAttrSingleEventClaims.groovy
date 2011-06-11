package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.EventClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.severities.EventSeverityExtractor
import org.pillarone.riskanalytics.domain.pc.severities.ProbabilityExtractor
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DependentLobAttrSingleEventClaims extends ComposedComponent implements ISegmentMarker {

    EventSeverityExtractor subEventSeverityExtractor
    ProbabilityExtractor subSingleSeverityExtractor
    ProbabilityExtractor subAttritionalSeverityExtractor
    EventClaimsGenerator subEventClaimsGenerator
    SingleClaimsGenerator subSingleClaimsGenerator
    AttritionalClaimsGenerator subAttritionalClaimsGenerator

    PacketList<EventDependenceStream> inEventSeverities = new PacketList(EventDependenceStream)
    PacketList<DependenceStream> inSingleSeverities = new PacketList(DependenceStream)
    PacketList<Frequency> inSingleClaimsCount = new PacketList(Frequency)
    PacketList<DependenceStream> inAttritionalSeverities = new PacketList(DependenceStream)

    PacketList<Claim> outClaims = new PacketList(Claim)

    DependentLobAttrSingleEventClaims() {
        subEventSeverityExtractor = new EventSeverityExtractor()
        subSingleSeverityExtractor = new ProbabilityExtractor()
        subAttritionalSeverityExtractor = new ProbabilityExtractor()
        subEventClaimsGenerator = new EventClaimsGenerator()
        subSingleClaimsGenerator = new SingleClaimsGenerator()
        subAttritionalClaimsGenerator = new AttritionalClaimsGenerator()
    }

    public void wire() {
        WiringUtils.use(PortReplicatorCategory) {
            subEventSeverityExtractor.inSeverities = this.inEventSeverities
            subSingleSeverityExtractor.inProbabilities = this.inSingleSeverities
            subAttritionalSeverityExtractor.inProbabilities = this.inAttritionalSeverities
            subSingleClaimsGenerator.inClaimCount = this.inSingleClaimsCount
            this.outClaims = subEventClaimsGenerator.outClaims
            this.outClaims = subSingleClaimsGenerator.outClaims
            this.outClaims = subAttritionalClaimsGenerator.outClaims
        }
        WiringUtils.use(WireCategory) {
            subEventClaimsGenerator.inSeverities = subEventSeverityExtractor.outSeverities
            subSingleClaimsGenerator.inProbability = subSingleSeverityExtractor.outProbabilities
            subAttritionalClaimsGenerator.inProbability = subAttritionalSeverityExtractor.outProbabilities
        }
    }
}
