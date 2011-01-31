package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;


import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TrivialContractStrategy extends AbstractContractStrategy {

    public static final ReinsuranceContractType type = ReinsuranceContractType.TRIVIAL;

    public ReinsuranceContractType getType() {
        return type;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public Claim calculateCededClaim(Claim grossClaim, double coveredByReinsurer) {
        Claim cededClaim = grossClaim.copy();
        cededClaim.scale(0);
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
        cededUnderwritingInfo.setPremium(0);
        cededUnderwritingInfo.setSumInsured(0);
        cededUnderwritingInfo.setMaxSumInsured(0);
        cededUnderwritingInfo.setCommission(0);
        return cededUnderwritingInfo;
    }

    public boolean exhausted() {
        return true;
    }
}