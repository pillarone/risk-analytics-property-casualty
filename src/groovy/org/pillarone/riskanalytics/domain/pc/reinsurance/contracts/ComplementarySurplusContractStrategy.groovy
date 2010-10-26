package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author martin.melchior (at) fhnw (dot) ch
 * @author Michael-Noe (at) Web (dot) de
 */
class ComplementarySurplusContractStrategy extends SurplusContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.SURPLUSCOMPLEMENTARY

    ReinsuranceContractType getType() {
        type
    }

    public double allocateCededClaim(Claim inClaim) {
        if (inClaim.hasExposureInfo()) {
            return inClaim.ultimate * (1 - (coveredByReinsurer * getFractionCeded(inClaim.exposure.sumInsured)))
        }
        else {
            return inClaim.ultimate * (1 - (coveredByReinsurer * defaultCededLossShare))
        }
    }
}