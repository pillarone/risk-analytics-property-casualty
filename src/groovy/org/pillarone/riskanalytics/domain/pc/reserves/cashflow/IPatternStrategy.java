package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPatternStrategy extends IParameterObject {
    List<Double> getPatternValues();
    int patternLength();
    SimulationPeriod calibrationPeriod();
}