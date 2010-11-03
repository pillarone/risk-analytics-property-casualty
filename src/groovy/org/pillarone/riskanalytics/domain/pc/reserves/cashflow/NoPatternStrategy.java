package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoPatternStrategy extends AbstractPatternStrategy {

    public IParameterObjectClassifier getType() {
        return PatternStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public List getPatternValues() {
        return Collections.emptyList();
    }

    public List<Double> getCumulativePatternValues() {
        return Collections.emptyList();
    }

    public SimulationPeriod calibrationPeriod() {
        return SimulationPeriod.ANNUALLY;
    }

    public List<Period> getCumulativePeriods() {
        List<Period> periods = new ArrayList<Period>(1);
        periods.add(Period.months(0));
        return periods;
    }
}