package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The claims merger calculates merged ceded claims using the property original
 * claim. All ceded claims having the same reference for originalClaim will
 * be merged in one claim object by aggregating the values and setting the
 * origin to the ClaimsMerger (<code>this</code>).<br/>
 * If the outClaimsNet channel is connected  net claim object are constructed
 * too. For every gross claim a net claim is constructed subtracting
 * the ultimate of the merged ceded claim with the same originalClaim.<br/>
 * <p/>
 * <b>Usage:</b> If a gross claim has different ways through the network of a
 * model and is ceded in different places (parallel reinsurance contracts).
 * <p/>
 * <b>Cave:</b> The component will throw an IlleagalArgumentException if it
 * receives a gross claim twice.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MarketClaimsMerger extends Component {

    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsNet = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsCeded = new PacketList<Claim>(Claim.class);

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanNet = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanGross = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanCeded = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);

    public void doCalculation() {
        if (inClaimsGross.isEmpty() && !inClaimsCeded.isEmpty()) {
            throw new IllegalStateException("Only ceded claims found!");
        }

        /* The map contains the gross claims as keys and the ceded as values */
        if (anyOutChannelWired()) {
            // By using a LinkedHashMap, we can be sure, that all outClaims* lists will be sorted according
            // to the inClaimsGross list.
            Map<Claim, GrossCededClaimsPair> grossMergedCededPairs = new LinkedHashMap<Claim, GrossCededClaimsPair>(inClaimsGross.size());
            for (Claim grossClaim : inClaimsGross) {
                GrossCededClaimsPair claimsPair = new GrossCededClaimsPair(grossClaim);
                if (grossMergedCededPairs.containsKey(grossClaim.getOriginalClaim())) {
                    throw new IllegalArgumentException("MarketClaimsMerger.inClaimsGross contains twice a claim with the same originalClaim!");
                }
                grossMergedCededPairs.put(grossClaim.getOriginalClaim(), claimsPair);
                outClaimsGross.add(grossClaim);
            }
            if (isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet)
                    || isSenderWired(outClaimsDevelopmentLeanCeded) || isSenderWired(outClaimsDevelopmentLeanNet)) {
                for (Claim cededClaim : inClaimsCeded) {
                    if (grossMergedCededPairs.containsKey(cededClaim.getOriginalClaim())) {
                        GrossCededClaimsPair aggregateCededClaim = grossMergedCededPairs.get(cededClaim.getOriginalClaim());
                        if (aggregateCededClaim.getClaimCeded() == null) {
                            aggregateCededClaim.setClaimCeded(cededClaim.copy());
                        }
                        else {
                            aggregateCededClaim.getClaimCeded().plus(cededClaim);
                            aggregateCededClaim.getClaimCeded().origin = this;
                        }
                    }
                }

                for (Map.Entry<Claim, GrossCededClaimsPair> entry : grossMergedCededPairs.entrySet()) {
                    Claim grossClaim = entry.getValue().getClaimGross();
                    Claim netClaim;
                    Claim cededClaim = entry.getValue().getClaimCeded();
                    if (cededClaim == null) {
                        netClaim = grossClaim.copy();
                        cededClaim = grossClaim.copy();
                        cededClaim.scale(0d);
                    }
                    else {
                        netClaim = grossClaim.getNetClaim(cededClaim);
                    }
                    outClaimsCeded.add(cededClaim);
                    netClaim.setOriginalClaim(entry.getKey());
                    netClaim.origin = this;
                    outClaimsNet.add(netClaim);
                }
            }
        }
        if (inClaimsGross.size() > 0 && inClaimsGross.get(0) instanceof ClaimDevelopmentLeanPacket) {
            for (Claim claim : outClaimsGross) {
                outClaimsDevelopmentLeanGross.add((ClaimDevelopmentLeanPacket) claim);
            }
            for (Claim claim : outClaimsCeded) {
                outClaimsDevelopmentLeanCeded.add((ClaimDevelopmentLeanPacket) claim);
            }
            for (Claim claim : outClaimsNet) {
                outClaimsDevelopmentLeanNet.add((ClaimDevelopmentLeanPacket) claim);
            }
        }
    }

    private boolean anyOutChannelWired() {
        return isSenderWired(outClaimsGross) || isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet)
                || isSenderWired(outClaimsDevelopmentLeanGross) || isSenderWired(outClaimsDevelopmentLeanNet)
                || isSenderWired(outClaimsDevelopmentLeanCeded);
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

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsDevelopmentLeanNet() {
        return outClaimsDevelopmentLeanNet;
    }

    public void setOutClaimsDevelopmentLeanNet(PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanNet) {
        this.outClaimsDevelopmentLeanNet = outClaimsDevelopmentLeanNet;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsDevelopmentLeanGross() {
        return outClaimsDevelopmentLeanGross;
    }

    public void setOutClaimsDevelopmentLeanGross(PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanGross) {
        this.outClaimsDevelopmentLeanGross = outClaimsDevelopmentLeanGross;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsDevelopmentLeanCeded() {
        return outClaimsDevelopmentLeanCeded;
    }

    public void setOutClaimsDevelopmentLeanCeded(PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanCeded) {
        this.outClaimsDevelopmentLeanCeded = outClaimsDevelopmentLeanCeded;
    }
}