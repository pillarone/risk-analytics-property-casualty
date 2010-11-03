package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class AbstractPatternStrategy implements IPatternStrategy {

    public int patternLength() {
        return getPatternValues().size();
    }
}
