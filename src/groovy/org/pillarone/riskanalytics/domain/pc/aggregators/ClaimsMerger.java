package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

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
public class ClaimsMerger extends Component {

    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsNet = new PacketList<Claim>(Claim.class);

    public void doCalculation() {
        if (inClaimsGross.isEmpty() && !inClaimsCeded.isEmpty()) {
            throw new IllegalStateException("Only ceded claims found!");
        }

        /* The map contains the gross claims as keys and the ceded as values */
        if (anyOutChannelWired()) {
            // By using a LinkedHashMap, we can be sure, that all outClaims* lists will be sorted according
            // to the inClaimsGross list.
            Map<Claim, Claim> grossMergedCededPairs = new LinkedHashMap<Claim, Claim>(inClaimsGross.size());
            for (Claim grossClaim : inClaimsGross) {
                if (grossMergedCededPairs.containsKey(grossClaim)) {
                    throw new IllegalArgumentException("ClaimsMerger.inClaimsGross contains twice the same claim!");
                }
                grossMergedCededPairs.put(grossClaim, null);
                outClaimsGross.add(grossClaim);
            }
            if (isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet)) {
                for (Claim cededClaim : inClaimsCeded) {
                    if (grossMergedCededPairs.containsKey(cededClaim.getOriginalClaim())) {
                        Claim aggregateCededClaim = grossMergedCededPairs.get(cededClaim.getOriginalClaim());
                        if (aggregateCededClaim == null) {
                            grossMergedCededPairs.put(cededClaim.getOriginalClaim(), cededClaim.copy());
                        } else {
                            aggregateCededClaim.plus(cededClaim);
                            aggregateCededClaim.origin = this;
                        }
                    }
                }

                for (Map.Entry<Claim, Claim> entry : grossMergedCededPairs.entrySet()) {
                    Claim grossClaim = entry.getKey();
                    Claim netClaim = grossClaim.copy();
                    netClaim.origin = this;
                    netClaim.setOriginalClaim(grossClaim);

                    Claim cededClaim = entry.getValue();
                    if (cededClaim == null) {
                        cededClaim = netClaim.copy();
                        cededClaim.setUltimate(0d);
                    } else {
                        netClaim.minus(cededClaim);
                    }
                    outClaimsCeded.add(cededClaim);
                    outClaimsNet.add(netClaim);
                }
            }
        }
    }

    private boolean anyOutChannelWired() {
        return isSenderWired(outClaimsGross) || isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet);
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