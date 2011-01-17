package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
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

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.FIXEDCOMMISSION;
    }

    public Map getParameters() {
        Map<String, Double> map = new HashMap<String, Double>(1);
        map.put("commission", commission);
        return map;
    }

    public void calculateCommission(List<Claim> claims, List<UnderwritingInfo> underwritingInfos, boolean isFirstPeriod, boolean isAdditive) {
        if (isAdditive) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                double premiumWritten = underwritingInfo.getPremiumWritten();
                underwritingInfo.setCommission(underwritingInfo.getCommission() - premiumWritten * commission);
                underwritingInfo.setFixedCommission(underwritingInfo.getFixedCommission() - premiumWritten * commission);
                underwritingInfo.setVariableCommission(underwritingInfo.getVariableCommission());
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                underwritingInfo.setCommission(-underwritingInfo.getPremiumWritten() * commission);
                underwritingInfo.setFixedCommission(underwritingInfo.getCommission());
                underwritingInfo.setVariableCommission(0d);
            }
        }
    }
}