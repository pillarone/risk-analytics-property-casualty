package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class AbstractContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    public void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
    }
    public void initBookKeepingFiguresForIteration(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfo) {
    }

    public void initBookKeepingFiguresOfPeriod(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos, double coveredByReinsurer) {
    }

    public void applyAnnualLimits() {
    }

    public void resetMemberInstances() {
    }

    public IReinsuranceContractStrategy copy() {
        return ReinsuranceContractType.getContractStrategy((ReinsuranceContractType) getType(), getParameters());
    }
}
