package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;
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

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanNet = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanGross = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanCeded = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentNet = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentGross = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentCeded = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);

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
            if (isSenderWired(outClaimsCeded) || isSenderWired(outClaimsNet)
                    || isSenderWired(outClaimsDevelopmentLeanCeded) || isSenderWired(outClaimsDevelopmentLeanNet)) {
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
                            aggregateCededClaim.setReinsuranceContract(correctReinsuranceContract(aggregateCededClaim, cededClaim));
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
        if (isSenderWired(outClaimsCeded) || isSenderWired(outClaimsDevelopmentLeanCeded)) {
            outClaimsCeded.addAll(inClaimsCeded);
        }
        if (inClaimsGross.size() > 0 && inClaimsGross.get(0) instanceof ClaimDevelopmentLeanPacket) {
            if (isSenderWired(outClaimsDevelopmentLeanGross)) {
                for (Claim claim : outClaimsGross) {
                    outClaimsDevelopmentLeanGross.add((ClaimDevelopmentLeanPacket) claim);
                }
            }
            if (isSenderWired(outClaimsDevelopmentLeanCeded)) {
                for (Claim claim : outClaimsCeded) {
                    outClaimsDevelopmentLeanCeded.add((ClaimDevelopmentLeanPacket) claim);
                }
            }
            if (isSenderWired(outClaimsDevelopmentLeanNet)) {
                for (Claim claim : outClaimsNet) {
                    outClaimsDevelopmentLeanNet.add((ClaimDevelopmentLeanPacket) claim);
                }
            }
        }
        if (inClaimsGross.size() > 0 && inClaimsGross.get(0) instanceof ClaimDevelopmentPacket) {
            if (isSenderWired(outClaimsDevelopmentGross)) {
                for (Claim claim : outClaimsGross) {
                    outClaimsDevelopmentGross.add((ClaimDevelopmentPacket) claim);
                }
            }
            if (isSenderWired(outClaimsDevelopmentCeded)) {
                for (Claim claim : outClaimsCeded) {
                    outClaimsDevelopmentCeded.add((ClaimDevelopmentPacket) claim);
                }
            }
            if (isSenderWired(outClaimsDevelopmentNet)) {
                for (Claim claim : outClaimsNet) {
                    outClaimsDevelopmentNet.add((ClaimDevelopmentPacket) claim);
                }
            }
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

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopmentNet() {
        return outClaimsDevelopmentNet;
    }

    public void setOutClaimsDevelopmentNet(PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentNet) {
        this.outClaimsDevelopmentNet = outClaimsDevelopmentNet;
    }

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopmentGross() {
        return outClaimsDevelopmentGross;
    }

    public void setOutClaimsDevelopmentGross(PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentGross) {
        this.outClaimsDevelopmentGross = outClaimsDevelopmentGross;
    }

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopmentCeded() {
        return outClaimsDevelopmentCeded;
    }

    public void setOutClaimsDevelopmentCeded(PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentCeded) {
        this.outClaimsDevelopmentCeded = outClaimsDevelopmentCeded;
    }
}