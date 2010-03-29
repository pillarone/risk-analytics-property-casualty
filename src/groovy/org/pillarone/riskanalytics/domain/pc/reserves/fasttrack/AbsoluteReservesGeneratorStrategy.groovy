package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author shartmann (at) munichre (dot) com
 */

public class AbsoluteReservesGeneratorStrategy extends AbstractClaimsGeneratorBasedReservesGeneratorStrategy implements IParameterObject, IReservesGeneratorStrategy{

    public IParameterObjectClassifier getType() {
        return ReservesGeneratorStrategyType.ABSOLUTE;
    }

}