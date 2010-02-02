package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
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
    private static final String COMMISSION = "commission";
    private double quotaShare;
    private double commission;

    public ReinsuranceContractType getType() {
        return type;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(2);
        parameters.put(QUOTASHARE, quotaShare);
        parameters.put(COMMISSION, commission);
        return parameters;
    }

    public Claim calculateCededClaim(Claim grossClaim, double coveredByReinsurer) {
        Claim cededClaim = grossClaim.copy();
        cededClaim.scale(quotaShare * coveredByReinsurer);
        return cededClaim;
    }

    public UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double coveredByReinsurer) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
        if (grossUnderwritingInfo != null) {
            cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo.originalUnderwritingInfo;
        }
        else {
            cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo;
        }
        cededUnderwritingInfo.premiumWritten *= quotaShare * coveredByReinsurer;
        cededUnderwritingInfo.premiumWrittenAsIf *= quotaShare * coveredByReinsurer;
        cededUnderwritingInfo.sumInsured *= quotaShare * coveredByReinsurer;
        cededUnderwritingInfo.maxSumInsured *= quotaShare * coveredByReinsurer;
        cededUnderwritingInfo.commission = cededUnderwritingInfo.premiumWritten * commission * coveredByReinsurer;
        return cededUnderwritingInfo;
    }

    public boolean exhausted() {
        return false;
    }
}
