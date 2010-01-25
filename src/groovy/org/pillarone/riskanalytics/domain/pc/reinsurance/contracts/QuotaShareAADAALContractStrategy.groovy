package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareAADAALContractStrategy extends QuotaShareContractStrategy implements IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHAREAADAAL

    double annualAggregateLimit
    double annualAggregateDeductible

    private double availableAnnualAggregateLimit
    private double remainingAnnualAggregateDeductible

    ReinsuranceContractType getType() {
        type
    }

    public Map getParameters() {
        ["quotaShare": quotaShare,
            "annualAggregateDeductible": annualAggregateDeductible,
            "annualAggregateLimit": annualAggregateLimit,
            "commission": commission,
            "coveredByReinsurer": coveredByReinsurer]
    }

    double calculateCoveredLoss(Claim inClaim) {
        if (remainingAnnualAggregateDeductible > 0) {
            double cededClaim = Math.min(Math.max(inClaim.ultimate - remainingAnnualAggregateDeductible, 0) * quotaShare, availableAnnualAggregateLimit) * coveredByReinsurer
            remainingAnnualAggregateDeductible -= inClaim.ultimate
            availableAnnualAggregateLimit -= cededClaim
            return cededClaim
        }
        else {
            double cededClaim = Math.min(inClaim.ultimate * quotaShare, availableAnnualAggregateLimit) * coveredByReinsurer
            availableAnnualAggregateLimit -= cededClaim
            return cededClaim
        }
    }

    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        remainingAnnualAggregateDeductible = annualAggregateDeductible
        availableAnnualAggregateLimit = annualAggregateLimit
    }
}