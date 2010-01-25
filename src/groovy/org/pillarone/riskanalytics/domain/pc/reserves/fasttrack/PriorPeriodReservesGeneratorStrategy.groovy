package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author shartmann (at) munichre (dot) com
 */

public class PriorPeriodReservesGeneratorStrategy extends AbstractClaimsGeneratorBasedReservesGeneratorStrategy implements IParameterObject, IReservesGeneratorStrategy{

    public Object getType() {
        return ReservesGeneratorStrategyType.PRIOR_PERIOD;
    }
}