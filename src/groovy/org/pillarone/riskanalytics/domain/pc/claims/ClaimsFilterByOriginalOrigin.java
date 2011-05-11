package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CLAIM","FILTER"})
public class ClaimsFilterByOriginalOrigin extends Component {

    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);


    public void doCalculation() {
        outClaims.addAll(ClaimFilterUtilities.filterClaimsByOriginalOrigin(inClaimsGross, inClaimsCeded));
    }

    public PacketList<Claim> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<Claim> outClaims) {
        this.outClaims = outClaims;
    }

    public PacketList<Claim> getInClaimsGross() {
        return inClaimsGross;
    }

    public void setInClaimsGross(PacketList<Claim> inClaimsGross) {
        this.inClaimsGross = inClaimsGross;
    }

    public PacketList<Claim> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<Claim> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }
}