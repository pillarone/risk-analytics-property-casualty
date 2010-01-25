package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

/**
 *  Converts a ClaimDevelopmentLeanPacket to a Claim packet setting the claim.ultimate equal to the incurred property.
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class ClaimsConverter extends Component {
    private PacketList<ClaimDevelopmentLeanPacket> inClaims = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);

    protected void doCalculation() {
        for (ClaimDevelopmentLeanPacket developmentClaim : inClaims) {
            outClaims.add(developmentClaim.getClaimPacket());
        }
    }

    public PacketList<ClaimDevelopmentLeanPacket> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<ClaimDevelopmentLeanPacket> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<Claim> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<Claim> outClaims) {
        this.outClaims = outClaims;
    }
}