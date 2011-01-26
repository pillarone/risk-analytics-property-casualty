package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class WXLContractStrategy extends XLContractStrategy implements IReinsuranceContractStrategy, IParameterObject {


    ReinsuranceContractType getType() {
        ReinsuranceContractType.WXL
    }


    double allocateCededClaim(Claim inClaim) {
        if (inClaim.claimType.equals(ClaimType.SINGLE) && availableAggregateLimit > 0) {
            double ceded = Math.min(Math.max(inClaim.ultimate - attachmentPoint, 0), limit) 
            ceded = availableAggregateLimit > ceded ? ceded : availableAggregateLimit
            availableAggregateLimit -= ceded
            return ceded * deductibleFactor
        }
        else {
            return 0d
        }
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        super.initBookkeepingFigures(inClaims, coverUnderwritingInfo)
        calculateDeductibleFactor(inClaims)
        availableAggregateLimit = aggregateLimit
    }
}