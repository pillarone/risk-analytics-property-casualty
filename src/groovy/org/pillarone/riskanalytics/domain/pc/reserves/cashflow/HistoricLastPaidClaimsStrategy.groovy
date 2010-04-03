package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * Cave: minimum development period at least 1
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class HistoricLastPaidClaimsStrategy implements IHistoricClaimsStrategy {

    TableMultiDimensionalParameter paidByDevelopmentPeriod = new TableMultiDimensionalParameter([[0d], [1]], ['Paids','Development Periods'])

    public Map<Integer, Double> getDiagonalValues() {
        Map<Integer, Double> diagonalValues = new LinkedHashMap<Integer, Double>()
        for (int row = 1; row < paidByDevelopmentPeriod.getRowCount(); row++) {
            diagonalValues.put((Integer) paidByDevelopmentPeriod.getValueAt(row, 2), (Double) paidByDevelopmentPeriod.getValueAt(row, 1))
        }
        return diagonalValues
    }

    public IParameterObjectClassifier getType() {
        return HistoricClaimsStrategyType.LAST_PAID
    }

    public Map getParameters() {
        return ['paidByDevelopmentPeriod': paidByDevelopmentPeriod];
    }
}
