package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ICoverPeriod extends IParameterObject {

    double getStartAsFractionOfPeriod(DateTime beginOfPeriod, DateTime endOfPeriod);
    double getEndAsFractionOfPeriod(DateTime beginOfPeriod, DateTime endOfPeriod);
    boolean isCovered(DateTime beginOfPeriod, DateTime endOfPeriod);
    CoverDuration getCoverDuration(DateTime beginOfPeriod, DateTime endOfPeriod);
}
