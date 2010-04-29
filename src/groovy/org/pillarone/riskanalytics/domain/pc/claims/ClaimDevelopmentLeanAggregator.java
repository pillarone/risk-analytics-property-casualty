package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacketFactory;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimDevelopmentLeanAggregator extends Component {

    private PacketList<ClaimDevelopmentLeanPacket> inClaimsCeded = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> inClaimsGross = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsCeded = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsGross = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsNet = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);

    public void doCalculation() {
        if (inClaimsGross.size() == 0 && inClaimsCeded.size() > 0) {
            throw new IllegalStateException("Only ceded claims found!");
        }

        ClaimDevelopmentLeanPacket grossAggregateClaim = ClaimDevelopmentLeanPacketFactory.createPacket();
        ClaimDevelopmentLeanPacket cededAggregateClaim = ClaimDevelopmentLeanPacketFactory.createPacket();

        if (isSenderWired(outClaimsGross) || isSenderWired(outClaimsNet)) {
            outClaimsGross.add(aggregateClaims(grossAggregateClaim, inClaimsGross));
        }
        if (isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet)) {
            if (inClaimsCeded.size() > 0) {
                outClaimsCeded.add(aggregateClaims(cededAggregateClaim, inClaimsCeded));
            }
        }
        if (isSenderWired(outClaimsNet)) {
            ClaimDevelopmentLeanPacket netAggregateClaim = ClaimDevelopmentLeanPacketFactory.copy(grossAggregateClaim);
            netAggregateClaim.minus(cededAggregateClaim);
            outClaimsNet.add(netAggregateClaim);
        }
    }

    private ClaimDevelopmentLeanPacket aggregateClaims(ClaimDevelopmentLeanPacket aggregateClaim, List<ClaimDevelopmentLeanPacket> claims) {
        aggregateClaim.origin = this;
        aggregateClaim.setClaimType(ClaimType.AGGREGATED);
        for (ClaimDevelopmentLeanPacket claim : claims) {
            aggregateClaim.plus(claim);
        }
        return aggregateClaim;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<ClaimDevelopmentLeanPacket> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getInClaimsGross() {
        return inClaimsGross;
    }

    public void setInClaimsGross(PacketList<ClaimDevelopmentLeanPacket> inClaimsGross) {
        this.inClaimsGross = inClaimsGross;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(PacketList<ClaimDevelopmentLeanPacket> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<ClaimDevelopmentLeanPacket> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(PacketList<ClaimDevelopmentLeanPacket> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }
}
