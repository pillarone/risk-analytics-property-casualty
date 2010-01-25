package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 *  This is a compound component composed of four <tt>FrequencyClaimsGenerator</tt> and
 *  a <tt>AttritionalClaimsGenerator</tt>.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalSingleEQFloodStormClaimsGenerator extends ComposedComponent {

    PacketList<Severity> inProbabilities = new PacketList(Severity)
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    FrequencyClaimsGenerator subSingleClaimsGenerator
    FrequencyEventClaimsGenerator subEQGenerator
    FrequencyEventClaimsGenerator subFloodGenerator
    FrequencyEventClaimsGenerator subStormGenerator
    FrequencyGenerator subAttritionalFrequencyGenerator
    AttritionalClaimsGenerator subAttritionalSeverityClaimsGenerator
    ClaimType claimType = ClaimType.SINGLE
    PacketList<Claim> outClaims = new PacketList(Claim)

    AttritionalSingleEQFloodStormClaimsGenerator() {
        subSingleClaimsGenerator = new FrequencyClaimsGenerator()
        subEQGenerator = new FrequencyEventClaimsGenerator()
        subFloodGenerator = new FrequencyEventClaimsGenerator()
        subStormGenerator = new FrequencyEventClaimsGenerator()
        subAttritionalFrequencyGenerator = new FrequencyGenerator()
        subAttritionalSeverityClaimsGenerator = new AttritionalClaimsGenerator()
    }

    public void doCalculation() {
        if (isReceiverWired(inUnderwritingInfo)) {
            super.doCalculation()
        } else {
            subSingleClaimsGenerator.start()
            subEQGenerator.start()
            subFloodGenerator.start()
            subStormGenerator.start()
            subAttritionalFrequencyGenerator.start()
        }
    }

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
