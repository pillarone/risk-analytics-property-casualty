package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class PremiumSharesPremiumAllocationStrategy implements IPremiumAllocationStrategy {

    private Map<LobMarker, Double> segmentShares = new HashMap<LobMarker, Double>();

    public PremiumAllocationType getType() {
        return PremiumAllocationType.PREMIUM_SHARES;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    /**
     * Calculates the share per segment based on the gross premium written and the line of business property
     * @param cededClaims not used within this strategy
     * @param grossUnderwritingInfos
     */
    public void initSegmentShares(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        if (grossUnderwritingInfos == null || grossUnderwritingInfos.size() == 0) return;
        segmentShares = new HashMap<LobMarker, Double>();
        double totalGrossPremium = 0d;
        for (UnderwritingInfo underwritingInfo : grossUnderwritingInfos) {
            LobMarker segment = underwritingInfo.getLineOfBusiness();
            if (segment == null) continue;
            Double totalGrossSegmentPremium = segmentShares.get(segment);
            totalGrossPremium += underwritingInfo.getPremiumWritten();
            if (totalGrossSegmentPremium == null) {
                segmentShares.put(segment, underwritingInfo.getPremiumWritten());
            }
            else {
                segmentShares.put(segment, totalGrossSegmentPremium + underwritingInfo.getPremiumWritten());
            }
        }
        for (Map.Entry<LobMarker, Double> totalCededSegmentClaim : segmentShares.entrySet()) {
            segmentShares.put(totalCededSegmentClaim.getKey(), (totalCededSegmentClaim.getValue() / totalGrossPremium));
        }
    }

    public double getShare(LobMarker segment) {
        Double share = segmentShares.get(segment);
        return share == null ? 1d : share;
    }
}
