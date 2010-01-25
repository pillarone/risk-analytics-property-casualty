package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack;

import java.util.Comparator;

/**
 *  Compares ClaimDevelopmentLeanPacket object using their date attribute.
 *
 *  @return -1 if claim.getFractionOfPeriod() is before otherClaim.getFractionOfPeriod(), 0 if both dates are identical
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SortClaimDevelopmentLeanPacketByFractionOfPeriod implements Comparator<ClaimDevelopmentLeanPacket> {

    private static SortClaimDevelopmentLeanPacketByFractionOfPeriod instance = null;

    private SortClaimDevelopmentLeanPacketByFractionOfPeriod() {
    }

    public static SortClaimDevelopmentLeanPacketByFractionOfPeriod getInstance() {
        if (instance == null) {
            instance = new SortClaimDevelopmentLeanPacketByFractionOfPeriod();
        }
        return instance;
    }

    public int compare(ClaimDevelopmentLeanPacket claim, ClaimDevelopmentLeanPacket otherClaim) {
        return claim.getFractionOfPeriod().compareTo(otherClaim.getFractionOfPeriod());
    }
}