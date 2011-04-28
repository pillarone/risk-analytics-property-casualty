package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TrivialContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.TRIVIAL

    ReinsuranceContractType getType() {
        type
    }

    public double allocateCededClaim(Claim inClaim) {
        return 0d
    }

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.premium = 0
        cededUnderwritingInfo.sumInsured = 0
        cededUnderwritingInfo.maxSumInsured = 0
        cededUnderwritingInfo.commission = 0
        cededUnderwritingInfo.fixedCommission = 0
        cededUnderwritingInfo.variableCommission = 0
        cededUnderwritingInfo.fixedPremium = 0
        cededUnderwritingInfo.variablePremium = 0
        return cededUnderwritingInfo
    }

    public Map getParameters() {
        return [:];
    }
}
