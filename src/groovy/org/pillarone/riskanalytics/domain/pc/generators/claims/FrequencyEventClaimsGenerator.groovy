package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverityGenerator
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 *  This is a compound component composed of a <tt>FrequencyGenerator</tt> and
 *  a <tt>LargeClaimsGenerator</tt>.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['CLAIM','GENERATOR','EVENT'])
class FrequencyEventClaimsGenerator extends ComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    FrequencyGenerator subFrequencyGenerator
    EventSeverityGenerator subEventGenerator
    EventClaimsGenerator subClaimsGenerator

    PacketList<Claim> outClaims = new PacketList(Claim)

    FrequencyEventClaimsGenerator() {
        subFrequencyGenerator = new FrequencyGenerator()
        subEventGenerator = new EventSeverityGenerator()
        subClaimsGenerator = new EventClaimsGenerator()
    }

    public void doCalculation() {
        if (isReceiverWired(inUnderwritingInfo)) {
            super.doCalculation()
        } else {
            subFrequencyGenerator.start()
        }
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subEventGenerator.inSeverityCount = subFrequencyGenerator.outFrequency
            subClaimsGenerator.inSeverities = subEventGenerator.outSeverities
        }
        WiringUtils.use(PortReplicatorCategory) {
            this.outClaims = subClaimsGenerator.outClaims
        }
        if (isReceiverWired(inUnderwritingInfo)) {
            WiringUtils.use(PortReplicatorCategory) {
                subFrequencyGenerator.inUnderwritingInfo = this.inUnderwritingInfo
                subClaimsGenerator.inUnderwritingInfo = this.inUnderwritingInfo
            }
        }
    }
}
