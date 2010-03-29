package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class CumulativePatternStrategy extends AbstractPatternStrategy {

    TableMultiDimensionalParameter cumulativePattern = new TableMultiDimensionalParameter([1d], ['Cumulative'])
    SimulationPeriod calibrationPeriod

    public IParameterObjectClassifier getType() {
        PatternStrategyType.CUMULATIVE
    }

    public Map getParameters() {
        ['cumulativePattern': cumulativePattern,
         'calibrationPeriod': calibrationPeriod]
    }

   public List getPatternValues() {
        cumulativePattern.getValues()
    }

    SimulationPeriod calibrationPeriod() {
        calibrationPeriod
    }
}