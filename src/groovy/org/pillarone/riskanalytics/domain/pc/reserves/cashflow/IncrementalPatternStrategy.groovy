package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.joda.time.Period

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

    List<Period> getCumulativePeriods() {
        List<Period> periods = new ArrayList<Period>();
        Period cumulatedPeriod = new Period();
        periods.add(cumulatedPeriod)
        Period period = SimulationPeriod.asPeriod(calibrationPeriod, null);
        for (int row = incrementalPattern.getTitleRowCount(); row <= incrementalPattern.getRowCount(); row++) {
            cumulatedPeriod = cumulatedPeriod.plus(period);
            periods.add(cumulatedPeriod)
        }
        return periods;
    }

    List<Double> getCumulativePatternValues() {
        List<Double> cumulativeValues = new ArrayList<Double>(patternLength());
        double cumulative = 0d;
        for (Double increment : getPatternValues()) {
            cumulative += increment;
            cumulativeValues.add(cumulative);
        }
        return cumulativeValues;
    }
}