package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.domain.utils.PacketUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimUtilities {

    public static ClaimWithExposure getClaimWithExposure(Claim claim) {
        ClaimWithExposure result = new ClaimWithExposure();
        result.setOriginalClaim(claim.getOriginalClaim());
        result.setEvent(claim.getEvent());
        result.setFractionOfPeriod(claim.getFractionOfPeriod());
        result.setClaimType(claim.getClaimType());
        // SingleValuePacket properties
        result.setUltimate(claim.getUltimate());
        // Packet properties
        result.setOrigin(claim.origin);
        result.setSender(claim.sender);
        result.setSenderChannelName(claim.senderChannelName);
        return result;
    }

    public static boolean isOriginal(Claim original, Claim hasOriginal) {
        return hasOriginal.getOriginalClaim() != null && hasOriginal.getOriginalClaim().equals(original);
    }

    public static boolean equalOriginalClaim(Claim claim1, Claim claim2) {
        if (claim1 == null || claim1.getOriginalClaim() == null) {
            return false;
        } else if (claim2 == null || claim2.getOriginalClaim() == null) {
            return false;
        } else {
            return claim1.getOriginalClaim().equals(claim2.getOriginalClaim());
        }
    }

    /**
     * @param claims this list will be sorted
     * @return a sorted list of the claims and merges claims if they have the same id property.
     */
    public static List<Claim> mergeClaimsWithEqualOriginalClaim(final List<Claim> claims, Component origin) {
        List<Claim> mergedClaims = new ArrayList<Claim>();
        Map<Claim, Claim> originalClaims = new HashMap<Claim, Claim>();
        for (Claim claim : claims) {
            Claim originalClaim = (Claim) claim.getOriginalClaim();
            if (originalClaim == null) {
                mergedClaims.add(claim);
            } else {
                Claim claimWithSameOriginalClaim = originalClaims.get(originalClaim);
                if (claimWithSameOriginalClaim == null) {
                    originalClaims.put(originalClaim, claim);
                    mergedClaims.add(claim);
                } else {
                    claimWithSameOriginalClaim.plus(claim);
                    claimWithSameOriginalClaim.origin = origin;
                }
            }
        }
        return mergedClaims;
    }

    // todo(sku): use dko if there is a more general solution possible
    public static boolean sameContent(Claim claim1, Claim claim2) {
        return (PacketUtilities.sameContent(claim1, claim2)
            && PacketUtilities.equals(claim1.getOriginalClaim(), claim2.getOriginalClaim())
            && PacketUtilities.equals(claim1.getEvent(), claim2.getEvent())
            && PacketUtilities.equals(claim1.getFractionOfPeriod(), claim2.getFractionOfPeriod())
            && PacketUtilities.equals(claim1.getClaimType(), claim2.getClaimType())
            && claim1.getUltimate() == claim2.getUltimate());
    }

    public static Claim aggregateClaims(List<Claim> claims, Component component) {
        if (claims.size() == 0) {
            return null;
        } else {
            Claim claim = ClaimPacketFactory.createPacket();
            claim.origin = component;
            claim.setClaimType(ClaimType.AGGREGATED);
            claim.setUltimate(PacketUtilities.sumClaims(claims));
            return claim;
        }
    }
}
