package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 *  This is a compound component composed of a <tt>FrequencyGenerator</tt> and
 *  a <tt>LargeClaimsGenerator</tt>.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FrequencyClaimsGenerator extends ComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)
    ClaimType claimType

    FrequencyGenerator subFrequencyGenerator
    SingleClaimsGenerator subClaimsGenerator

    PacketList<Claim> outClaims = new PacketList(Claim)

    FrequencyClaimsGenerator() {
        subFrequencyGenerator = new FrequencyGenerator()
        subClaimsGenerator = new SingleClaimsGenerator()
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inClaimCount = subFrequencyGenerator.outFrequency
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

    public void setClaimType(ClaimType sClaimType) {
        claimType = sClaimType
        subClaimsGenerator.claimType = claimType
    }
}
