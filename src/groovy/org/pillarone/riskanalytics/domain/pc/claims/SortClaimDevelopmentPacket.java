package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket;

import java.util.Comparator;

/**
 *  Compares Claim object using their fraction of period and if available original period attribute.
 *
 *  @return -1 if claim is before otherClaim, 0 if both dates are identical
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SortClaimDevelopmentPacket implements Comparator<Claim> {

    private static SortClaimDevelopmentPacket instance = null;

    private SortClaimDevelopmentPacket() {
    }

    public static SortClaimDevelopmentPacket getInstance() {
        if (instance == null) {
            instance = new SortClaimDevelopmentPacket();
        }
        return instance;
    }

    public int compare(Claim claim, Claim otherClaim) {
        double diff = compareOriginalPeriod(claim, otherClaim);
        if (diff == 0) {
            return claim.getFractionOfPeriod().compareTo(otherClaim.getFractionOfPeriod());
        }
        else {
            return diff > 0 ? 1 : -1;
        }
    }

    private double compareOriginalPeriod(Claim claim, Claim otherClaim) {
        if (claim instanceof ClaimDevelopmentPacket && otherClaim instanceof ClaimDevelopmentPacket) {
            return Math.signum(((ClaimDevelopmentPacket) claim).getOriginalPeriod() - ((ClaimDevelopmentPacket) otherClaim).getOriginalPeriod());
        }
        else {
            return 0;
        }
    }
}
