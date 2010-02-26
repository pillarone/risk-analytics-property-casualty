package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class FixedCommissionStrategy implements ICommissionStrategy {

    private double commission = 0;

    public Object getType() {
        return CommissionStrategyType.FIXEDCOMMISSION;
    }

    public Map getParameters() {
        Map<String, Double> map = new HashMap<String, Double>(1);
        map.put("commission", commission);
        return map;
    }

    public void calculateCommission(List<Claim> claims, List<UnderwritingInfo> underwritingInfos, boolean firstPeriod) {
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            underwritingInfo.setCommission(underwritingInfo.getCommission() + underwritingInfo.getPremiumWritten() * commission);
        }
    }
}