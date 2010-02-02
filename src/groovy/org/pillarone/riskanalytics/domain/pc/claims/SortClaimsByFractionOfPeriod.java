package org.pillarone.riskanalytics.domain.pc.claims;

import java.util.Comparator;

/**
 *  Compares Claim object using their date attribute.
 *
 *  @return -1 if claim.date is before otherClaim.date, 0 if both dates are identical
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SortClaimsByFractionOfPeriod implements Comparator<Claim> {

    private static SortClaimsByFractionOfPeriod instance = null;

    private SortClaimsByFractionOfPeriod() {
    }

    public static SortClaimsByFractionOfPeriod getInstance() {
        if (instance == null) {
            instance = new SortClaimsByFractionOfPeriod();
        }
        return instance;
    }

    public int compare(Claim claim, Claim otherClaim) {
        return claim.getFractionOfPeriod().compareTo(otherClaim.getFractionOfPeriod());
    }
}