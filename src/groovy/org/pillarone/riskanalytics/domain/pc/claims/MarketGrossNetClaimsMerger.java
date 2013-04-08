package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The claims merger calculates merged ceded claims using the originalClaim
 * property. All ceded claims having the same reference for originalClaim will
 * be merged in one claim object by aggregating the values and setting the
 * origin to the ClaimsMerger (<code>this</code>). If the reinsuranceContract
 * or lineOfBusiness property of the ceded claims having the same originalClaim
 * differs, these properties are set to null in the merged claim.<br/>
 * If the outClaimsNet channel is connected, net claim objects are constructed
 * too. For every gross claim, a net claim is constructed subtracting
 * the ultimate of the merged ceded claim with the same originalClaim.<br/>
 * Ceded out channel contain the same packets as inClaimsCeded. This is required
 * to enable aggregate drill down.
 * <p/>
 * <b>Usage:</b> If a gross claim has different ways through the network of a
 * model and is ceded in different places (parallel reinsurance contracts).
 * <p/>
 * <b>Cave:</b> The component will throw an IlleagalArgumentException if it
 * receives a gross claim twice.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CLAIM","MARKET","MERGER"})
public class MarketGrossNetClaimsMerger extends Component {

    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsNet = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsCeded = new PacketList<Claim>(Claim.class);

    public void doCalculation() {
        if (inClaimsGross.isEmpty() && !inClaimsCeded.isEmpty()) {
            throw new IllegalStateException("MarketGrossNetClaimsMerger.onlyCededClaims");
        }

        /* The map contains the gross claims as keys and the ceded as values */
        if (anyOutChannelWired()) {
            // By using a LinkedHashMap, we can be sure, that all outClaims* lists will be sorted according
            // to the inClaimsGross list.
            Map<Claim, GrossCededClaimsPair> grossMergedCededPairs = new LinkedHashMap<Claim, GrossCededClaimsPair>(inClaimsGross.size());
            for (Claim grossClaim : inClaimsGross) {
                GrossCededClaimsPair claimsPair = new GrossCededClaimsPair(grossClaim);
                if (grossMergedCededPairs.containsKey(grossClaim.getOriginalClaim())) {
                    throw new IllegalArgumentException("MarketGrossNetClaimsMerger.doubleClaimInformation");
                }
                grossMergedCededPairs.put(grossClaim.getOriginalClaim(), claimsPair);
                outClaimsGross.add(grossClaim);
            }
            if (isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet)) {
                for (Claim cededClaim : inClaimsCeded) {
                    if (grossMergedCededPairs.containsKey(cededClaim.getOriginalClaim())) {
                        GrossCededClaimsPair aggregateGrossCededClaim = grossMergedCededPairs.get(cededClaim.getOriginalClaim());
                        if (aggregateGrossCededClaim.getClaimCeded() == null) {
                            aggregateGrossCededClaim.setClaimCeded(cededClaim.copy());
                        }
                        else {
                            Claim aggregateCededClaim = aggregateGrossCededClaim.getClaimCeded();
                            aggregateCededClaim.plus(cededClaim);
                            aggregateCededClaim.origin = this;
                            aggregateCededClaim.addMarker(IReinsuranceContractMarker.class,
                                    correctReinsuranceContract(aggregateCededClaim, cededClaim));
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
                    netClaim.setOriginalClaim(entry.getKey());
                    netClaim.origin = this;
                    outClaimsNet.add(netClaim);
                }
            }
        }
        if (isSenderWired(outClaimsCeded)) {
            outClaimsCeded.addAll(inClaimsCeded);
        }
    }

    private IReinsuranceContractMarker correctReinsuranceContract(Claim aggregateClaim, Claim claim) {
        if (aggregateClaim.getReinsuranceContract() != null
            && aggregateClaim.getReinsuranceContract().equals(claim.getReinsuranceContract())) {
            return aggregateClaim.getReinsuranceContract();
        }
        else {
            return null;
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