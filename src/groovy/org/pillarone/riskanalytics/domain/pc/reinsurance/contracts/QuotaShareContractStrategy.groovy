package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHARE

    double quotaShare
    double commission

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["quotaShare": quotaShare,
            "commission": commission,
            "coveredByReinsurer": coveredByReinsurer]
    }

    double calculateCoveredLoss(Claim inClaim) {
        inClaim.ultimate * quotaShare * coveredByReinsurer
    }

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.premiumWritten *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.premiumWrittenAsIf *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.sumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.maxSumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.commission = cededUnderwritingInfo.premiumWritten * commission * coveredByReinsurer
        cededUnderwritingInfo
    }
}