package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.joda.time.Period

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class CumulativePatternStrategy extends AbstractPatternStrategy {

    public static final String CUMULATIVE = 'Cumulative'

    TableMultiDimensionalParameter cumulativePattern = new TableMultiDimensionalParameter([1d], [CUMULATIVE])
    SimulationPeriod calibrationPeriod

    public IParameterObjectClassifier getType() {
        PatternStrategyType.CUMULATIVE
    }

    public Map getParameters() {
        ['cumulativePattern': cumulativePattern,
         'calibrationPeriod': calibrationPeriod]
    }

   public List getPatternValues() {
        if (cumulativePattern.getValues().size() > 0 && cumulativePattern.getValues().get(0) instanceof List) {
            return (List) cumulativePattern.getValues().get(0)
        }
        cumulativePattern.getValues()
    }

    SimulationPeriod calibrationPeriod() {
        calibrationPeriod
    }

    List<Period> getCumulativePeriods() {
        List<Period> periods = new ArrayList<Period>();
        Period cumulatedPeriod = new Period();
        Period period = SimulationPeriod.asPeriod(calibrationPeriod, null);
        for (int row = cumulativePattern.getTitleRowCount(); row < cumulativePattern.getRowCount(); row++) {
            cumulatedPeriod = cumulatedPeriod.plus(period);
            periods.add(cumulatedPeriod)
        }
        return periods;
    }

    List<Double> getCumulativePatternValues() {
        return getPatternValues();
    }
}