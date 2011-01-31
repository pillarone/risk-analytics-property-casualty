package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 *  In a first step claims are merged per event. Merged claims are ceded and afterwards the ceded part is
 *  allocated proportionally to the individual claims.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CXLContractStrategy extends XLContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    private Map<Event, Double> claimsValueMergedByEvent = [:]
    private Map<Event, Double> cededShareByEvent = [:]

    ReinsuranceContractType getType() {
        ReinsuranceContractType.CXL
    }

    double allocateCededClaim(Claim inClaim) {
        // todo (sku): work on clear definitions of ClaimType.EVENT and ClaimType.AGGREGATE_EVENT
        if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
            return inClaim.ultimate * cededShareByEvent.get(inClaim.event)  * deductibleFactor
        }
        else {
            return 0d
        }
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        super.initBookkeepingFigures inClaims, coverUnderwritingInfo
        // merge claims by event id
        claimsValueMergedByEvent.clear()
        for (Claim claim: inClaims) {
            // todo (sku): work on clear definitions of ClaimType.EVENT and ClaimType.AGGREGATE_EVENT
            if (claim.claimType == ClaimType.EVENT || claim.claimType == ClaimType.AGGREGATED_EVENT) {
                double value = claimsValueMergedByEvent.get(claim.event)
                if (value != null) {
                    claimsValueMergedByEvent.put(claim.event, value + claim.ultimate)
                }
                else {
                    claimsValueMergedByEvent.put(claim.event, claim.ultimate)
                }
            }
        }
        // todo (jdi): CXL should also work with PremiumBase.GNPI
        for (MapEntry claim: claimsValueMergedByEvent.entrySet()) {
            double ceded = Math.min(Math.max(claim.value - attachmentPoint, 0), limit)
            ceded = availableAggregateLimit > ceded ? ceded : availableAggregateLimit
            availableAggregateLimit -= ceded
            cededShareByEvent.put(claim.key, claim.value == 0 ? 0d : ceded / claim.value)
        }
        calculateDeductibleFactor(inClaims)
    }

    public void resetMemberInstances() {
        claimsValueMergedByEvent.clear()
        cededShareByEvent.clear()
    }
}