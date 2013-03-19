package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.List;

/**
 * The claims aggregator sums up the gross and ceded ultimates and calculates the net value.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CLAIM","AGGREGATOR"})
public class ClaimsAggregator extends Component {

    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsNet = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsCeded = new PacketList<Claim>(Claim.class);

    public void doCalculation() {
        if (inClaimsGross.size() == 0 && inClaimsCeded.size() > 0) {
            throw new IllegalStateException("ClaimsAggregator.onlyCededClaims");
        }

        Claim grossAggregateClaim = new Claim();
        Claim cededAggregateClaim = new Claim();

        if (inClaimsGross.size() > 0 && (isSenderWired(outClaimsGross) || isSenderWired(outClaimsNet))) {
            grossAggregateClaim = aggregateClaims(inClaimsGross);
            outClaimsGross.add(grossAggregateClaim);
        }
        if (inClaimsCeded.size() > 0 && (isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet))) {
            cededAggregateClaim = aggregateClaims(inClaimsCeded);
            outClaimsCeded.add(cededAggregateClaim);
        }
        if (isSenderWired(outClaimsNet)) {
            Claim netAggregateClaim = grossAggregateClaim.getNetClaim(cededAggregateClaim);
            outClaimsNet.add(netAggregateClaim);
        }
    }

    private Claim aggregateClaims(List<Claim> claims) {
        Claim aggregateClaim = claims.get(0).copy();
        aggregateClaim.scale(0);
        aggregateClaim.origin = this;
        aggregateClaim.setClaimType(ClaimType.AGGREGATED);
        for (Claim claim : claims) {
            aggregateClaim.plus(claim);
        }
        return aggregateClaim;
    }

    public PacketList<Claim> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<Claim> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }

    public PacketList<Claim> getInClaimsGross() {
        return inClaimsGross;
    }

    public void setInClaimsGross(PacketList<Claim> inClaimsGross) {
        this.inClaimsGross = inClaimsGross;
    }

    public PacketList<Claim> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(PacketList<Claim> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<Claim> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<Claim> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<Claim> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(PacketList<Claim> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }
}