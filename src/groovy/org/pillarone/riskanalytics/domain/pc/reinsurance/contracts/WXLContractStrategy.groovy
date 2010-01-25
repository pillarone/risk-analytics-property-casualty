package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class WXLContractStrategy extends XLContractStrategy implements IReinsuranceContractStrategy, IParameterObject {


    ReinsuranceContractType getType() {
        ReinsuranceContractType.WXL
    }

    double calculateCoveredLoss(Claim inClaim) {
        if (inClaim.claimType.equals(ClaimType.SINGLE) && availableAggregateLimit > 0) {
            double ceded = Math.min(Math.max(inClaim.ultimate - attachmentPoint, 0), limit) * coveredByReinsurer
            ceded = availableAggregateLimit > ceded ? ceded : availableAggregateLimit
            availableAggregateLimit -= ceded
            return ceded
        }
        else {
            return 0d
        }
    }
}