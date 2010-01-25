package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareEventLimitContractStrategy extends QuotaShareContractStrategy implements IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHAREEVENTLIMIT

    double eventLimit

    ReinsuranceContractType getType() {
        type
    }

    public Map getParameters() {
        ["quotaShare": quotaShare,
            "eventLimit": eventLimit,
            "commission": commission,
            "coveredByReinsurer": coveredByReinsurer]
    }

    double calculateCoveredLoss(Claim inClaim) {
        double cededValue = super.calculateCoveredLoss(inClaim)
        if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
            cededValue = Math.min(cededValue, eventLimit)
        }
        return cededValue
    }
}