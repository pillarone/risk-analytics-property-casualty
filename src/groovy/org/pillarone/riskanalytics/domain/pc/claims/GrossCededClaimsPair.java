package org.pillarone.riskanalytics.domain.pc.claims;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GrossCededClaimsPair {
    private Claim claimGross;
    private Claim claimCeded;

    public GrossCededClaimsPair(Claim claimGross) {
        this.claimGross = claimGross;
    }

    public GrossCededClaimsPair(Claim claimGross, Claim claimCeded) {
        this.claimGross = claimGross;
        this.claimCeded = claimCeded;
    }

    public Claim getClaimGross() {
        return claimGross;
    }

    public void setClaimGross(Claim claimGross) {
        this.claimGross = claimGross;
    }

    public Claim getClaimCeded() {
        return claimCeded;
    }

    public void setClaimCeded(Claim claimCeded) {
        this.claimCeded = claimCeded;
    }
}