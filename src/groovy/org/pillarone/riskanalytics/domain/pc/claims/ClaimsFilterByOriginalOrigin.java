package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CLAIM","FILTER"})
public class ClaimsFilterByOriginalOrigin extends Component {

    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);


    public void doCalculation() {
        outClaims.addAll(inClaimsCeded);
    }

    @Override
    public void filterInChannel(PacketList inChannel, PacketList source) {
        if (inChannel == inClaimsCeded) {
            if (source.size() > 0 ) {
                inClaimsCeded.addAll(ClaimFilterUtilities.filterClaimsByOriginalOrigin(inClaimsGross, source));
            }
        }
        else {
            super.filterInChannel(inChannel, source);
        }
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