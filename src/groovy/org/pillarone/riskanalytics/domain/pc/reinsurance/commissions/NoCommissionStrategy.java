package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class NoCommissionStrategy implements ICommissionStrategy {

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.NOCOMMISSION;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void calculateCommission(List<Claim> claims, List<CededUnderwritingInfo> underwritingInfos, boolean isFirstPeriod, boolean isAdditive) {
        // If !isAdditive, ceded commission is set to 0 in the contract strategies, and no steps are required here,
        // whereas if it is additive, the prior commission remains intact and no new commission need be added here.
    }
}