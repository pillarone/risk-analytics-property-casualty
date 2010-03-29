package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod;

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

    public SimulationPeriod calibrationPeriod() {
        return SimulationPeriod.ANNUALLY;
    }
}