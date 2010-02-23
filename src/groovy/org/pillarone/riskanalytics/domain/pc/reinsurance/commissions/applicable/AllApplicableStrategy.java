package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable;

import java.util.Collections;
import java.util.Map;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class AllApplicableStrategy implements IApplicableStrategy {

    public Object getType() {
        return ApplicableStrategyType.ALL;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
