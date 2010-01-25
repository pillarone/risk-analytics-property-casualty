package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareAADContractStrategy extends QuotaShareContractStrategy implements IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHAREAAD

    double annualAggregateDeductible

    private double remainingAnnualAggregateDeductible

    ReinsuranceContractType getType() {
        type
    }

    public Map getParameters() {
        ["quotaShare": quotaShare,
            "annualAggregateDeductible": annualAggregateDeductible,
            "commission": commission,
            "coveredByReinsurer": coveredByReinsurer]
    }

    double calculateCoveredLoss(Claim inClaim) {
        if (remainingAnnualAggregateDeductible > 0) {
            remainingAnnualAggregateDeductible -= inClaim.ultimate
            if (remainingAnnualAggregateDeductible < 0) {
                return -remainingAnnualAggregateDeductible * quotaShare * coveredByReinsurer
            }
            return 0.0
        }
        else {
            double cededClaim = inClaim.ultimate * quotaShare * coveredByReinsurer
            return cededClaim
        }
    }

    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        remainingAnnualAggregateDeductible = annualAggregateDeductible
    }
}