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

    FrequencyClaimsGenerator subSingleClaimsGenerator1
    FrequencyClaimsGenerator subSingleClaimsGenerator2
    FrequencyClaimsGenerator subSingleClaimsGenerator3
    FrequencyClaimsGenerator subSingleClaimsGenerator4
    FrequencyClaimsGenerator subSingleClaimsGenerator5
    FrequencyClaimsGenerator subSingleClaimsGenerator6
    FrequencyClaimsGenerator subSingleClaimsGenerator7

    AttritionalClaimsGenerator subAttritionalClaimsGenerator

    PacketList<Claim> outClaims = new PacketList(Claim)

    AttritionalMultiSingleClaimsGenerator() {
        subSingleClaimsGenerator1 = new FrequencyClaimsGenerator()
        subSingleClaimsGenerator2 = new FrequencyClaimsGenerator()
        subSingleClaimsGenerator3 = new FrequencyClaimsGenerator()
        subSingleClaimsGenerator4 = new FrequencyClaimsGenerator()
        subSingleClaimsGenerator5 = new FrequencyClaimsGenerator()
        subSingleClaimsGenerator6 = new FrequencyClaimsGenerator()
        subSingleClaimsGenerator7 = new FrequencyClaimsGenerator()
        subAttritionalClaimsGenerator = new AttritionalClaimsGenerator()
    }

    public void doCalculation() {
        if (isReceiverWired(inUnderwritingInfo)) {
            super.doCalculation()
        } else {
            subSingleClaimsGenerator1.start()
            subSingleClaimsGenerator2.start()
            subSingleClaimsGenerator3.start()
            subSingleClaimsGenerator4.start()
            subSingleClaimsGenerator5.start()
            subSingleClaimsGenerator6.start()
            subSingleClaimsGenerator7.start()

            subAttritionalClaimsGenerator.start()
        }
    }

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
