package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

import java.util.List;

/**
 *
 * Specific commission strategies are used in two types of components: Commission and ReinsuranceContract.
 * The isAdditive parameter of calculateCommission should be true for commissions, but false for reinsurance contracts.
 *
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public interface ICommissionStrategy extends IParameterObject {

    void calculateCommission(List<Claim> claims, List<UnderwritingInfo> underwritingInfos, boolean isFirstPeriod, boolean isAdditive);
}