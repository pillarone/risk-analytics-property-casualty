package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.domain.pc.constants.IncludeType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AllCoverAttributeStrategy implements ICoverAttributeStrategy {
    
    private IncludeType reserves = IncludeType.NOTINCLUDED;

    public Object getType() {
        return CoverAttributeStrategyType.ALL;
    }

    public Map getParameters() {
        Map<String, IncludeType> parameters = new HashMap<String, IncludeType>(1);
        parameters.put("reserves", reserves);
        return parameters;
    }
}
