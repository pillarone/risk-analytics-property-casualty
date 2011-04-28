package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoHistoricClaimsStrategy extends AbstractParameterObject implements IHistoricClaimsStrategy {

    public Map<Integer, Double> getDiagonalValues() {
        Collections.emptyMap()
    }

    public IParameterObjectClassifier getType() {
        return HistoricClaimsStrategyType.NONE
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
