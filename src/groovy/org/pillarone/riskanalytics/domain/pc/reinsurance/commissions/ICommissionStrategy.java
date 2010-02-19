package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

import java.util.List;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public interface ICommissionStrategy extends IParameterObject {

    void calculateCommission(List<Claim> claims, List<UnderwritingInfo> underwritingInfos, boolean firstPeriod);
}