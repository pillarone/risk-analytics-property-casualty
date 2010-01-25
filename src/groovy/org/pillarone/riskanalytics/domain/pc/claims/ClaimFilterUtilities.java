package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.core.components.Component;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimFilterUtilities {
    /**
     * @param claims        to be filtered
     * @param coverCriteria components such as claims generators, lines of business
     * @return a claim is added to the list of filtered claims it the originalClaim references an element of the
     *         cover criteria or if the originalClaim property is null the origin property is evaluated.
     */
    public static List<Claim> filterClaims(List<Claim> claims, List coverCriteria) {
        List<Claim> filteredClaims = new ArrayList<Claim>(claims.size());
        if (coverCriteria != null) {
            for (Claim claim : claims) {
                if (claim.getOriginalClaim() == null) {
                    if (coverCriteria.contains(claim.origin)) {
                        filteredClaims.add(claim);
                    }
                }
                else {
                    if (coverCriteria.contains(claim.getOriginalClaim())) {
                        filteredClaims.add(claim);
                    }
                }
            }
        }
        Collections.sort(filteredClaims, SortClaimsByDate.getInstance());
        return filteredClaims;
    }

    /**
     * @param claims        to be filtered
     * @param coverCriteria components such as claims generators, lines of business
     * @return a claim is added to the list of filtered claims it the origin references an element of the
     *         cover criteria
     */
    public static List<Claim> filterClaimsByOrigin(List<Claim> claims, List coverCriteria) {
        List<Claim> filteredClaims = new ArrayList<Claim>(claims.size());
        if (coverCriteria != null) {
            for (Claim claim : claims) {
                if (coverCriteria.contains(claim.origin)) {
                    filteredClaims.add(claim);
                }
            }
        }
        Collections.sort(filteredClaims, SortClaimsByDate.getInstance());
        return filteredClaims;
    }

    /**
     * @param claims
     * @param coveredOrigin
     * @param coveredOriginal
     * @return
     */
    public static List<Claim> filterClaims(List<Claim> claims, List coveredOrigin, List coveredOriginal) {
        List<Claim> filteredClaims = new ArrayList<Claim>();
        if ((coveredOrigin == null || coveredOrigin.size() == 0) && (coveredOriginal == null || coveredOriginal.size() == 0)) {
            filteredClaims.addAll(claims);
        }
        else if (coveredOrigin.size() > 0 && coveredOriginal.size() > 0) {
            for (Claim claim : claims) {
                if (coveredOrigin.contains(claim.origin) && coveredOriginal.contains(claim.getOriginalClaim().origin)) {
                    filteredClaims.add(claim);
                }
            }
        }
        else if (coveredOrigin.size() > 0) {
            for (Claim claim : claims) {
                if (coveredOrigin.contains(claim.origin)) {
                    filteredClaims.add(claim);
                }
            }
        }
        else if (coveredOriginal.size() > 0) {
            for (Claim claim : claims) {
                if (coveredOriginal.contains(claim.getOriginalClaim().origin)) {
                    filteredClaims.add(claim);
                }
            }
        }
        return filteredClaims;
    }

    public static List<Claim> filterClaimsByPerilAndLob(List<Claim> claims, List<PerilMarker> coveredPerils, List<LobMarker> coveredLinesOfBusiness) {
        List<Claim> filteredClaims = new ArrayList<Claim>();
        if ((coveredPerils == null || coveredPerils.size() == 0) && (coveredLinesOfBusiness == null || coveredLinesOfBusiness.size() == 0)) {
            filteredClaims.addAll(claims);
        }
        else if (coveredPerils.size() > 0 && coveredLinesOfBusiness.size() > 0) {
            for (Claim claim : claims) {
                if (coveredPerils.contains(claim.getPeril()) && coveredLinesOfBusiness.contains(claim.getLineOfBusiness())) {
                    filteredClaims.add(claim);
                }
            }
        }
        else if (coveredPerils.size() > 0) {
            for (Claim claim : claims) {
                if (coveredPerils.contains(claim.getPeril())) {
                    filteredClaims.add(claim);
                }
            }
        }
        else if (coveredLinesOfBusiness.size() > 0) {
            for (Claim claim : claims) {
                if (coveredLinesOfBusiness.contains(claim.getLineOfBusiness())) {
                    filteredClaims.add(claim);
                }
            }
        }
        return filteredClaims;
    }

    public static List<Claim> filterClaimsByPeril(List<Claim> claims, List<PerilMarker> coveredPerils) {
        if (coveredPerils == null || coveredPerils.size() == 0) {
            return claims;
        }
        else {
            List<Claim> filteredClaims = new ArrayList<Claim>();
            for (Claim claim : claims) {
                if (coveredPerils.contains(claim.getPeril())) {
                    filteredClaims.add(claim);
                }
            }
            return filteredClaims;
        }
    }

    public static List<Claim> filterClaimsByOrigin(List<Claim> claims, Component origin) {
        List<Claim> filteredClaims = new ArrayList<Claim>(claims.size());
        for (Claim claim : claims) {
            if ((claim.origin).equals(origin)) {
                filteredClaims.add(claim);
            }
        }
        Collections.sort(filteredClaims, SortClaimsByDate.getInstance());
        return filteredClaims;
    }

    public static List<Claim> filterClaimsByOriginalOrigin(List<Claim> claimsGross, List<Claim> claimsCeded) {
        List<Component> originalOrigins = new ArrayList<Component>();
        for (Claim claimGross : claimsGross) {
            originalOrigins.add(claimGross.getOriginalClaim().origin);
        }
        List<Claim> filteredClaims = new ArrayList<Claim>(claimsGross.size());
        for (Claim claimCeded : claimsCeded) {
            if (originalOrigins.contains(claimCeded.getOriginalClaim().origin)) {
                filteredClaims.add(claimCeded);
            }
        }
        return filteredClaims;
    }

    public static List<Component> getOrigins(List<Claim> claims) {
        Set<Component> origins = new HashSet<Component>();
        for (Claim claim : claims) {
            origins.add(claim.origin);
        }
        return new ArrayList<Component>(origins);
    }

    public static List<LobMarker> getLineOfBusiness(List<Claim> claims) {
        Set<LobMarker> linesOfBusiness = new HashSet<LobMarker>();
        for (Claim claim : claims) {
            linesOfBusiness.add(claim.getLineOfBusiness());
        }
        return new ArrayList<LobMarker>(linesOfBusiness);
    }
}