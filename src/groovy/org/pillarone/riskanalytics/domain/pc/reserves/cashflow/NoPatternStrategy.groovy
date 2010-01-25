package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoPatternStrategy implements IPatternStrategy, IParameterObject {

    public Object getType() {
        return PatternStrategyType.NONE
    }

    public Map getParameters() {
        [:]
    }

    public List getPatternValues() {
        return null;
    }
}