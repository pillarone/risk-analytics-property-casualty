package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event

/**
 *  In a first step claims are merged per event. Merged claims are ceded and afterwards the ceded part is
 *  allocated proportionally to the individual claims.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class WCXLContractStrategy extends XLContractStrategy implements IReinsuranceContractStrategy {

    private Map<Event, Double> claimsValueMergedByEvent = [:]
    private Map<Event, Double> cededShareByEvent = [:]

    ReinsuranceContractType getType() {
        ReinsuranceContractType.WCXL
    }

    double allocateCededClaim(Claim inClaim) {
        // todo (sku): work on clear definitions of ClaimType.EVENT and ClaimType.AGGREGATE_EVENT
        if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
            return inClaim.ultimate * cededShareByEvent.get(inClaim.event)
        }
        else {
            if (inClaim.claimType.equals(ClaimType.SINGLE)) {
                return calculateCededClaim(inClaim.ultimate)
            }
            else {
                return 0d
            }

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

        for (MapEntry claim: claimsValueMergedByEvent.entrySet()) {
            double ultimate = (Double) claim.value
            double ceded = calculateCededClaim(ultimate)
            cededShareByEvent.put((Event) claim.key, ultimate == 0 ? 0d : ceded / ultimate)
        }
    }

    public void resetMemberInstances() {
        claimsValueMergedByEvent.clear()
        cededShareByEvent.clear()
    }
}
