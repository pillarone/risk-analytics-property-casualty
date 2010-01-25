package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareAALContractStrategy extends QuotaShareContractStrategy implements IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHAREAAL

    double annualAggregateLimit

    private double availableAnnualAggregateLimit

    ReinsuranceContractType getType() {
        type
    }

    public Map getParameters() {
        ["quotaShare": quotaShare,
            "availableAnnualAggregateLimit": annualAggregateLimit,
            "commission": commission,
            "coveredByReinsurer": coveredByReinsurer]
    }

    double calculateCoveredLoss(Claim inClaim) {
        if (availableAnnualAggregateLimit > 0) {
            double cededClaim = Math.min(inClaim.ultimate * quotaShare, availableAnnualAggregateLimit) * coveredByReinsurer
            availableAnnualAggregateLimit -= cededClaim
            return cededClaim
        }
        else {
            return 0d
        }
    }

    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        availableAnnualAggregateLimit = annualAggregateLimit
    }
}