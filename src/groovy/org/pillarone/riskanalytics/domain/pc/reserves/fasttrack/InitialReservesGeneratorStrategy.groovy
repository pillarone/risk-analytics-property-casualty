package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author shartmann (at) munichre (dot) com
 */

public class InitialReservesGeneratorStrategy extends AbstractClaimsGeneratorBasedReservesGeneratorStrategy implements IParameterObject, IReservesGeneratorStrategy{

    public IParameterObjectClassifier getType() {
        return ReservesGeneratorStrategyType.INITIAL_RESERVES;
    }
}