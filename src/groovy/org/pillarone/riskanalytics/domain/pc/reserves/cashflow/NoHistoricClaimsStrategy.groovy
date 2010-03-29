package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoHistoricClaimsStrategy implements IHistoricClaimsStrategy {

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