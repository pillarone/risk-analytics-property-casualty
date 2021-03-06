package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class HistoricLastReportedClaimsStrategy extends AbstractParameterObject implements IHistoricClaimsStrategy {

    TableMultiDimensionalParameter reportedByDevelopmentPeriod = new TableMultiDimensionalParameter([[0d], [1]], ['Reported','Development Periods'])

    public Map<Integer, Double> getDiagonalValues() {
        Map<Integer, Double> diagonalValues = new LinkedHashMap<Integer, Double>()
        for (int row = 1; row < reportedByDevelopmentPeriod.getRowCount(); row++) {
            diagonalValues.put((Integer) reportedByDevelopmentPeriod.getValueAt(row, 1), (Double) reportedByDevelopmentPeriod.getValueAt(row, 0))
        }
        return diagonalValues
    }

    public IParameterObjectClassifier getType() {
        return HistoricClaimsStrategyType.LAST_REPORTED
    }

    public Map getParameters() {
        return ['reportedByDevelopmentPeriod': reportedByDevelopmentPeriod];
    }
}
