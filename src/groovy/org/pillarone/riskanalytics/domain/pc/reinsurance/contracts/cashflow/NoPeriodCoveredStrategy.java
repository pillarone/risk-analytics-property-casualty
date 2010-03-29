package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NoPeriodCoveredStrategy implements ICoverPeriod, IParameterObject {

    public IParameterObjectClassifier getType() {
        return CoverPeriodType.FULL;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public double getStartAsFractionOfPeriod(DateTime beginOfPeriod, DateTime endOfPeriod) {
        return -1;
    }

    public double getEndAsFractionOfPeriod(DateTime beginOfPeriod, DateTime endOfPeriod) {
        return -1;
    }

    public boolean isCovered(DateTime beginOfPeriod, DateTime endOfPeriod) {
        return false;
    }

    public CoverDuration getCoverDuration(DateTime beginOfPeriod, DateTime endOfPeriod) {
        return new CoverDuration(false);
    }
}