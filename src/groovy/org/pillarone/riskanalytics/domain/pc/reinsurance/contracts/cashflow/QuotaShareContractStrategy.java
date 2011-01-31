package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContractStrategy extends AbstractContractStrategy {

    public static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHARE;

    private static final String QUOTASHARE = "quotaShare";
    private double quotaShare;

    public ReinsuranceContractType getType() {
        return type;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(2);
        parameters.put(QUOTASHARE, quotaShare);
        return parameters;
    }

    public Claim calculateCededClaim(Claim grossClaim, double coveredByReinsurer) {
        Claim cededClaim = grossClaim.copy();
        cededClaim.scale(quotaShare * coveredByReinsurer);
        return cededClaim;
    }

    public CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double coveredByReinsurer) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
        if (grossUnderwritingInfo != null) {
            cededUnderwritingInfo.setOriginalUnderwritingInfo(grossUnderwritingInfo.getOriginalUnderwritingInfo());
        }
        else {
            cededUnderwritingInfo.setOriginalUnderwritingInfo(grossUnderwritingInfo);
        }
        cededUnderwritingInfo.setPremium(cededUnderwritingInfo.getPremium() * quotaShare * coveredByReinsurer);
        cededUnderwritingInfo.setSumInsured(cededUnderwritingInfo.getSumInsured() * quotaShare * coveredByReinsurer);
        cededUnderwritingInfo.setMaxSumInsured(cededUnderwritingInfo.getMaxSumInsured() * quotaShare * coveredByReinsurer);
        return cededUnderwritingInfo;
    }

    public boolean exhausted() {
        return false;
    }
}
