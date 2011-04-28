package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author shartmann (at) munichre (dot) com
 */

public class PriorPeriodReservesGeneratorStrategy extends AbstractClaimsGeneratorBasedReservesGeneratorStrategy
    implements IReservesGeneratorStrategy{

    public IParameterObjectClassifier getType() {
        return ReservesGeneratorStrategyType.PRIOR_PERIOD;
    }
}
