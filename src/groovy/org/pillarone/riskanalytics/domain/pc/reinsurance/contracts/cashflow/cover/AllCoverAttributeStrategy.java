package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.cover;

import org.pillarone.riskanalytics.domain.pc.constants.IncludeType;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.ICoverAttributeStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Covers all contracts, without regard to reserves.
 * 
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class AllCoverAttributeStrategy implements ICoverAttributeStrategy {

    public Object getType() {
        return CoverAttributeStrategyType.ALL;
    }

    public Map getParameters() {
        Map<String, IncludeType> parameters = new HashMap<String, IncludeType>(1);
        return parameters;
    }
}