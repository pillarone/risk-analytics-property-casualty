package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class AbstractPatternStrategy implements IPatternStrategy {

    public int patternLength() {
        return getPatternValues().size();
    }
}
