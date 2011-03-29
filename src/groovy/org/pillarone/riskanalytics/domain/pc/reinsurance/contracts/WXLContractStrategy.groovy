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


    double allocateCededClaim(Claim inClaim) {
        if (inClaim.claimType.equals(ClaimType.SINGLE)) {
            return calculateCededClaim(inClaim.ultimate)
        }
        else {
            return 0d
        }
    }
}