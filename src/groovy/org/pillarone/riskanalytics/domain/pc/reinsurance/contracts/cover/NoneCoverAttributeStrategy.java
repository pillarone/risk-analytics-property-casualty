package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoneCoverAttributeStrategy implements ICoverAttributeStrategy {

    public Object getType() {
        return CoverAttributeStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}