package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ClaimsSharesPremiumAllocationStrategy extends AbstractPremiumAllocation {

    public PremiumAllocationType getType() {
        return PremiumAllocationType.CLAIMS_SHARES;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    /**
     * Calculates the share per segment based on the ultimate of each claim and the line of business property
     * @param cededClaims
     * @param grossUnderwritingInfos not used within this strategy
     */
    public void initSegmentShares(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        Map<LobMarker, Double> segmentShares = new HashMap<LobMarker, Double>();
        double totalCededClaim = 0d;
        for (Claim cededClaim : cededClaims) {
            LobMarker segment = cededClaim.getLineOfBusiness();
            if (segment == null) continue;
            Double totalCededSegmentClaim = segmentShares.get(segment);
            totalCededClaim += cededClaim.getUltimate();
            if (totalCededSegmentClaim == null) {
                segmentShares.put(segment, cededClaim.getUltimate());
            }
            else {
                segmentShares.put(segment, totalCededSegmentClaim + cededClaim.getUltimate());
            }
        }
        if (totalCededClaim == 0) {
            segmentShares.clear();
        }
        else {
            for (Map.Entry<LobMarker, Double> totalCededSegmentClaim : segmentShares.entrySet()) {
                segmentShares.put(totalCededSegmentClaim.getKey(), totalCededSegmentClaim.getValue() / totalCededClaim);
            }
        }
        initUnderwritingInfoShares(grossUnderwritingInfos, segmentShares);
    }

}