package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IncrementalPatternStrategy extends AbstractPatternStrategy {

    TableMultiDimensionalParameter incrementalPattern = new TableMultiDimensionalParameter([1d], ['Increments'])
    SimulationPeriod calibrationPeriod

    public Object getType() {
        PatternStrategyType.INCREMENTAL
    }

    public Map getParameters() {
        ['incrementalPattern': incrementalPattern,
         'calibrationPeriod': calibrationPeriod]
    }

    public List getPatternValues() {
        incrementalPattern.getValues()
    }

    SimulationPeriod calibrationPeriod() {
        calibrationPeriod
    }
}