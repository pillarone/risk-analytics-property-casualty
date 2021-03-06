package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.components.ComponentCategory
import org.pillarone.riskanalytics.core.wiring.WiringValidation

/**
 *  This is a compound component composed of four <tt>FrequencyClaimsGenerator</tt> and
 *  a <tt>AttritionalClaimsGenerator</tt>.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['CLAIM','GENERATOR','ATTRITIONAL','SINGLE','EVENT'])
class AttritionalSingleEQFloodStormClaimsGenerator extends ComposedComponent {

    @WiringValidation(connections= [0, 1], packets= [1, 1])
    PacketList<Severity> inProbabilities = new PacketList(Severity)
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    FrequencyClaimsGenerator subSingleClaimsGenerator = new FrequencyClaimsGenerator()
    FrequencyEventClaimsGenerator subEQGenerator = new FrequencyEventClaimsGenerator()
    FrequencyEventClaimsGenerator subFloodGenerator = new FrequencyEventClaimsGenerator()
    FrequencyEventClaimsGenerator subStormGenerator = new FrequencyEventClaimsGenerator()
    FrequencyGenerator subAttritionalFrequencyGenerator = new FrequencyGenerator()
    AttritionalClaimsGenerator subAttritionalSeverityClaimsGenerator = new AttritionalClaimsGenerator()
    ClaimType claimType = ClaimType.SINGLE
    PacketList<Claim> outClaims = new PacketList(Claim)

    public void wire() {
        if (isReceiverWired(inUnderwritingInfo)) {
            WiringUtils.use(PortReplicatorCategory) {
                subSingleClaimsGenerator.inUnderwritingInfo = this.inUnderwritingInfo
                subEQGenerator.inUnderwritingInfo = this.inUnderwritingInfo
                subFloodGenerator.inUnderwritingInfo = this.inUnderwritingInfo
                subStormGenerator.inUnderwritingInfo = this.inUnderwritingInfo
                subAttritionalFrequencyGenerator.inUnderwritingInfo = this.inUnderwritingInfo
                subAttritionalSeverityClaimsGenerator.inUnderwritingInfo = this.inUnderwritingInfo
            }
        }
        if (isReceiverWired(inProbabilities)) {
            WiringUtils.use(PortReplicatorCategory) {
                subAttritionalSeverityClaimsGenerator.inProbability = this.inProbabilities
            }
        }

        WiringUtils.use(WireCategory) {
            subAttritionalSeverityClaimsGenerator.inMultiplier = subAttritionalFrequencyGenerator.outFrequency
        }

        WiringUtils.use(PortReplicatorCategory) {
            this.outClaims = subSingleClaimsGenerator.outClaims
            this.outClaims = subEQGenerator.outClaims
            this.outClaims = subFloodGenerator.outClaims
            this.outClaims = subStormGenerator.outClaims
            this.outClaims = subAttritionalSeverityClaimsGenerator.outClaims
        }
    }
}
