package org.pillarone.riskanalytics.domain.pc.constants;

import java.util.Map;

/**
 * @author martin.melchior (at) fhnw (dot) ch, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum RiskBandAllocationBaseLimited {
    NUMBER_OF_POLICIES, PREMIUM; //CUSTOM, LOSS_PROBABILITY;  // todo(sku): re-enable PMO-497

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}