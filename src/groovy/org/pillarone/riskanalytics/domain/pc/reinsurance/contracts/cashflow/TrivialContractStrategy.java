package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;


import org.pillarone.riskanalytics.domain.pc.claims.Claim;
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

    public UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double coveredByReinsurer) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
        if (grossUnderwritingInfo != null) {
            cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo.originalUnderwritingInfo;
        }
        else {
            cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo;
        }
        cededUnderwritingInfo.premiumWritten = 0;
        cededUnderwritingInfo.premiumWrittenAsIf = 0;
        cededUnderwritingInfo.sumInsured = 0;
        cededUnderwritingInfo.maxSumInsured = 0;
        cededUnderwritingInfo.commission = 0;
        return cededUnderwritingInfo;
    }

//    @Override
//    public IReinsuranceContractStrategy clone() {
//        return ReinsuranceContractType.getContractStrategy(getType(), getParameters());
//    }

    public boolean exhausted() {
        return true;
    }
}