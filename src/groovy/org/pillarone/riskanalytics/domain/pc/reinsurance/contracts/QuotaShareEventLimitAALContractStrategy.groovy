package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareEventLimitAALContractStrategy extends QuotaShareEventLimitContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHAREEVENTLIMITAAL

    double annualAggregateLimit

    private double availableAnnualAggregateLimit

    ReinsuranceContractType getType() {
        type
    }

    public Map getParameters() {
        ["quotaShare": quotaShare,
            "eventLimit": eventLimit,
            "annualAggregateLimit": annualAggregateLimit,
            "commission": commission,
            "coveredByReinsurer": coveredByReinsurer]
    }

    double calculateCoveredLoss(Claim inClaim) {
        if (availableAnnualAggregateLimit > 0) {
            double Value = super.calculateCoveredLoss(inClaim)
            double cededValue = Math.min(Value, availableAnnualAggregateLimit)
            availableAnnualAggregateLimit -= cededValue
            return cededValue
        }
        return 0d
    }

    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        availableAnnualAggregateLimit = annualAggregateLimit
    }
}
