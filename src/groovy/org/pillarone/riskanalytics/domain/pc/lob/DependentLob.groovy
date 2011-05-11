package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.generators.claims.EventClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.severities.EventSeverityExtractor
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['SEGMENT'])
class DependentLob extends ComposedComponent implements LobMarker {

    EventSeverityExtractor subSeverityExtractor
    EventClaimsGenerator subClaimsGenerator

    PacketList<EventDependenceStream> inEventSeverities = new PacketList(EventDependenceStream)
    PacketList<Claim> outClaims = new PacketList(Claim)

    DependentLob() {
        subSeverityExtractor = new EventSeverityExtractor()
        subClaimsGenerator = new EventClaimsGenerator()
    }

    public void wire() {
        WiringUtils.use(PortReplicatorCategory) {
            subSeverityExtractor.inSeverities = this.inEventSeverities
            this.outClaims = subClaimsGenerator.outClaims
        }
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inSeverities = subSeverityExtractor.outSeverities
        }
    }
}
