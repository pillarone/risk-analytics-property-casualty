package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoHistoricClaimsStrategy implements IHistoricClaimsStrategy {

    public Map<Integer, Double> getDiagonalValues() {
        Collections.emptyMap()
    }

    public Object getType() {
        return HistoricClaimsStrategyType.NONE
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}