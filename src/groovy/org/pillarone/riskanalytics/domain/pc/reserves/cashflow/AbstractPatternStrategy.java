package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class AbstractPatternStrategy extends AbstractParameterObject implements IPatternStrategy {

    public int patternLength() {
        return getPatternValues().size();
    }
}
