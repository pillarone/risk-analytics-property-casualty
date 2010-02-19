package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class NoCommissionStrategy implements ICommissionStrategy {

    public Object getType() {
        return CommissionStrategyType.NOCOMMISSION;
    }

    public Map getParameters() {
        //Map<String, Double> map = new HashMap<String, Double>(1);
        //map.put("commission", commission);
        //return map;
        return null;
    }

    public void calculateCommission(List<Claim> claims, List<UnderwritingInfo> underwritingInfos, boolean firstPeriod) {
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            underwritingInfo.setCommission(underwritingInfo.getCommission());
        }
    }
}