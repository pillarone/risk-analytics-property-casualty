package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

/**
 *
 * @author martin.melchior (at) fhnw (dot) ch
 */
abstract class AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    double coveredByReinsurer = 1d

    public void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
    }

    public void resetMemberInstances() {
    }
}