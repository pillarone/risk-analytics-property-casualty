package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class ProfitCommissionStrategy extends AbstractParameterObject implements ICommissionStrategy {

    private double profitCommissionRatio = 0d;
    private double commissionRatio = 0d; // for "prior" fixed commission
    private double costRatio = 0d;
    private boolean lossCarriedForwardEnabled = true;
    private double initialLossCarriedForward = 0d;
    /**
     * not a parameter but updated during calculateCommission() to avoid side effect for the parameter variable
     */
    private double lossCarriedForward = 0d;

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.PROFITCOMMISSION;
    }

    public Map getParameters() {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("profitCommissionRatio", profitCommissionRatio);
        map.put("commissionRatio", commissionRatio);
        map.put("costRatio", costRatio);
        map.put("lossCarriedForwardEnabled", lossCarriedForwardEnabled);
        map.put("initialLossCarriedForward", initialLossCarriedForward);
        return map;
    }

    public void calculateCommission(List<Claim> claims, List<CededUnderwritingInfo> underwritingInfos, boolean isFirstPeriod, boolean isAdditive) {
        if (lossCarriedForwardEnabled && isFirstPeriod) {
            lossCarriedForward = initialLossCarriedForward;
        }
        double incurredClaims = 0d;
        for (Claim claim : claims) {
            incurredClaims += claim.getUltimate();
        }
        double totalPremiumWritten = 0d;
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            totalPremiumWritten += underwritingInfo.getPremium();
        }
        double fixedCommission = commissionRatio * totalPremiumWritten; // calculate 'prior' fixed commission
        double currentProfit = totalPremiumWritten * (1d - costRatio) - fixedCommission - incurredClaims;
        double commissionableProfit = Math.max(0d, currentProfit - lossCarriedForward);
        double variableCommission = profitCommissionRatio * commissionableProfit;
        double totalCommission = fixedCommission + variableCommission;
        lossCarriedForward = lossCarriedForwardEnabled ? Math.max(0d, lossCarriedForward - currentProfit) : 0d;

        if (isAdditive) {
            for (CededUnderwritingInfo underwritingInfo : underwritingInfos) {
                double premiumWritten = underwritingInfo.getPremium();
                underwritingInfo.setCommission(-premiumWritten * totalCommission / totalPremiumWritten + underwritingInfo.getCommission());
                underwritingInfo.setFixedCommission(-premiumWritten * fixedCommission / totalPremiumWritten + underwritingInfo.getFixedCommission());
                underwritingInfo.setVariableCommission(-premiumWritten * variableCommission / totalPremiumWritten + underwritingInfo.getVariableCommission());
            }
        }
        else {
            for (CededUnderwritingInfo underwritingInfo : underwritingInfos) {
                double premiumWritten = underwritingInfo.getPremium();
                underwritingInfo.setCommission(-premiumWritten * totalCommission / totalPremiumWritten);
                underwritingInfo.setFixedCommission(-premiumWritten * fixedCommission / totalPremiumWritten);
                underwritingInfo.setVariableCommission(-premiumWritten * variableCommission / totalPremiumWritten);
            }
        }
    }
}
