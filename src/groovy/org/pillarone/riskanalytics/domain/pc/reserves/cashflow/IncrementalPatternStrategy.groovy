package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IncrementalPatternStrategy implements IPatternStrategy, IParameterObject {

    TableMultiDimensionalParameter incrementalPattern = new TableMultiDimensionalParameter([1d], ['Increments'])

    public Object getType() {
        return PatternStrategyType.INCREMENTAL
    }

    public Map getParameters() {
        ['incrementalPattern': incrementalPattern]
    }

    public List getPatternValues() {
        return incrementalPattern.getValues();
    }
}