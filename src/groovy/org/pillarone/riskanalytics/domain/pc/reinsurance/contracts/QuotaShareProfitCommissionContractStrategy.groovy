package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareProfitCommissionContractStrategy extends QuotaShareContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHAREPROFITCOMMISSION

    /** reinsurers expances are expressed as a fraction of ceded premium */
    double expensesOfReinsurer

    /** profit commission is expressed as a fraction of reinsurers profit */
    double profitCommission

    double aggregateCededClaimValue

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["quotaShare": quotaShare,
         "commission": commission,
         "coveredByReinsurer": coveredByReinsurer,
         "expensesOfReinsurer": expensesOfReinsurer,
         "profitCommission": profitCommission]
    }

    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        aggregateCededClaimValue = 0d
        for (Claim claim: inClaims) {
            aggregateCededClaimValue += super.calculateCoveredLoss(claim)
        }
    }

    private double calculateCommission(double cededPremium) {
        private double profit = Math.max (cededPremium - aggregateCededClaimValue - (commission + expensesOfReinsurer) * cededPremium, 0d)
        return cededPremium * commission + profit * profitCommission
    }

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo) {
        UnderwritingInfo cededUnderwritingInfo = super.calculateCoverUnderwritingInfo(grossUnderwritingInfo)
        cededUnderwritingInfo.commission = calculateCommission(cededUnderwritingInfo.premiumWritten)
        cededUnderwritingInfo
    }
}