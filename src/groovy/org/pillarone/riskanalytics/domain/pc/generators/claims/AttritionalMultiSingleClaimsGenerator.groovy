package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 *  This is a compound component composed of a  {@link FrequencyClaimsGenerator}  and
 *  a  {@link AttritionalClaimsGenerator} .
 *
 * @author j.dittrich (at) intuitive-collaboration (dot) com
 */
class AttritionalMultiSingleClaimsGenerator extends ComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    FrequencyClaimsGenerator subSingleClaimsGenerator1 = new FrequencyClaimsGenerator()
    FrequencyClaimsGenerator subSingleClaimsGenerator2 = new FrequencyClaimsGenerator()
    FrequencyClaimsGenerator subSingleClaimsGenerator3 = new FrequencyClaimsGenerator()
    FrequencyClaimsGenerator subSingleClaimsGenerator4 = new FrequencyClaimsGenerator()
    FrequencyClaimsGenerator subSingleClaimsGenerator5 = new FrequencyClaimsGenerator()
    FrequencyClaimsGenerator subSingleClaimsGenerator6 = new FrequencyClaimsGenerator()
    FrequencyClaimsGenerator subSingleClaimsGenerator7 = new FrequencyClaimsGenerator()

    AttritionalClaimsGenerator subAttritionalClaimsGenerator = new AttritionalClaimsGenerator()

    PacketList<Claim> outClaims = new PacketList(Claim)

    public void wire() {
        if (isReceiverWired(inUnderwritingInfo)) {
            WiringUtils.use(PortReplicatorCategory) {
                subSingleClaimsGenerator1.inUnderwritingInfo = this.inUnderwritingInfo
                subSingleClaimsGenerator2.inUnderwritingInfo = this.inUnderwritingInfo
                subSingleClaimsGenerator3.inUnderwritingInfo = this.inUnderwritingInfo
                subSingleClaimsGenerator4.inUnderwritingInfo = this.inUnderwritingInfo
                subSingleClaimsGenerator5.inUnderwritingInfo = this.inUnderwritingInfo
                subSingleClaimsGenerator6.inUnderwritingInfo = this.inUnderwritingInfo
                subSingleClaimsGenerator7.inUnderwritingInfo = this.inUnderwritingInfo

                subAttritionalClaimsGenerator.inUnderwritingInfo = this.inUnderwritingInfo
            }
        }
        WiringUtils.use(PortReplicatorCategory) {
            this.outClaims = subSingleClaimsGenerator1.outClaims
            this.outClaims = subSingleClaimsGenerator2.outClaims
            this.outClaims = subSingleClaimsGenerator3.outClaims
            this.outClaims = subSingleClaimsGenerator4.outClaims
            this.outClaims = subSingleClaimsGenerator5.outClaims
            this.outClaims = subSingleClaimsGenerator6.outClaims
            this.outClaims = subSingleClaimsGenerator7.outClaims

            this.outClaims = subAttritionalClaimsGenerator.outClaims
        }
    }
}
