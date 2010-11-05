package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class PremiumSharesPremiumAllocationStrategy extends AbstractPremiumAllocation {

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
        initUnderwritingInfoShares(grossUnderwritingInfos, Collections.<LobMarker, Double>emptyMap());
    }
}
