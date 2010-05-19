package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IncrementalPatternStrategy extends AbstractPatternStrategy {

    TableMultiDimensionalParameter incrementalPattern = new TableMultiDimensionalParameter([1d], ['Increments'])
    SimulationPeriod calibrationPeriod

    public IParameterObjectClassifier getType() {
        PatternStrategyType.INCREMENTAL
    }

    public Map getParameters() {
        ['incrementalPattern': incrementalPattern,
         'calibrationPeriod': calibrationPeriod]
    }

    public List getPatternValues() {
        if (incrementalPattern.getValues().size() > 0 && incrementalPattern.getValues().get(0) instanceof List) {
            return (List) incrementalPattern.getValues().get(0)
        }
        incrementalPattern.getValues()
    }

    SimulationPeriod calibrationPeriod() {
        calibrationPeriod
    }
}