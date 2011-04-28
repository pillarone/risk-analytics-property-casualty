package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;

/**
 *
 * @author martin.melchior (at) fhnw (dot) ch
 */
abstract class AbstractContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    double coveredByReinsurer = 1d
    Double parmCovered

    double covered() {
        coveredByReinsurer
    }

    void adjustCovered(double factor) {
        if (!parmCovered) {
            parmCovered = coveredByReinsurer
        }
        coveredByReinsurer *= factor
    }

    void resetCovered() {
        coveredByReinsurer = parmCovered
    }

    public void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
    }

    void initCededPremiumAllocation(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
    }

    public void resetMemberInstances() {
    }

}
