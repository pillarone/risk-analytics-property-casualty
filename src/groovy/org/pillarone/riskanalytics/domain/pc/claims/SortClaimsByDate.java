package org.pillarone.riskanalytics.domain.pc.claims;

import java.util.Comparator;

/**
 *  Compares Claim object using their date attribute.
 *
 *  @return -1 if claim.date is before otherClaim.date, 0 if both dates are identical
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SortClaimsByDate implements Comparator<Claim> {

    private static SortClaimsByDate instance = null;

    private SortClaimsByDate() {
    }

    public static SortClaimsByDate getInstance() {
        if (instance == null) {
            instance = new SortClaimsByDate();
        }
        return instance;
    }

    public int compare(Claim claim, Claim otherClaim) {
        return claim.getFractionOfPeriod().compareTo(otherClaim.getFractionOfPeriod());
    }
}