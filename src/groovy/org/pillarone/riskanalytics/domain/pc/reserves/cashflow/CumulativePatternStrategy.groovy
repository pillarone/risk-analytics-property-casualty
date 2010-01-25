package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class CumulativePatternStrategy implements IPatternStrategy, IParameterObject {

    TableMultiDimensionalParameter cumulativePattern = new TableMultiDimensionalParameter([1d], ['Cumulative'])

    public Object getType() {
        return PatternStrategyType.CUMULATIVE
    }

    public Map getParameters() {
        ['cumulativePattern': cumulativePattern]
    }

   public List getPatternValues() {
        return cumulativePattern.getValues();
    }
}