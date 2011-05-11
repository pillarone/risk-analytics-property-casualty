package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 *  This is a compound component composed of a  {@link FrequencyClaimsGenerator}  and
 *  a  {@link AttritionalClaimsGenerator} .
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['CLAIM','GENERATOR','ATTRITIONAL','SINGLE'])
class AttritionalSingleClaimsGenerator extends ComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<Severity> inProbabilities = new PacketList(Severity)

    FrequencyClaimsGenerator subSingleClaimsGenerator = new FrequencyClaimsGenerator()
    AttritionalClaimsGenerator subAttritionalClaimsGenerator = new AttritionalClaimsGenerator()

    PacketList<Claim> outClaims = new PacketList(Claim)

    AttritionalSingleClaimsGenerator() {
    }

    public void doCalculation() {
        if (isReceiverWired(inUnderwritingInfo)) {
            super.doCalculation()
        } else {
            subSingleClaimsGenerator.start()
            subAttritionalClaimsGenerator.start()
        }
    }

    public void wire() {
        if (isReceiverWired(inUnderwritingInfo)) {
            WiringUtils.use(PortReplicatorCategory) {
                subSingleClaimsGenerator.inUnderwritingInfo = this.inUnderwritingInfo
                subAttritionalClaimsGenerator.inUnderwritingInfo = this.inUnderwritingInfo
            }
        }
        if (isReceiverWired(inProbabilities)) {
            WiringUtils.use(PortReplicatorCategory) {
                subAttritionalClaimsGenerator.inProbability = this.inProbabilities
            }
        }
        WiringUtils.use(PortReplicatorCategory) {
            this.outClaims = subSingleClaimsGenerator.outClaims
            this.outClaims = subAttritionalClaimsGenerator.outClaims
        }
    }
}
