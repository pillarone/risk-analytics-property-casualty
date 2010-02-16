package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PeriodCoveredStrategy implements ICoverPeriod, IParameterObject {

    private DateTime start;
    private DateTime end;

    public Object getType() {
        return CoverPeriodType.PERIOD;
    }

    public Map getParameters() {
        Map<String, DateTime> params = new HashMap<String, DateTime>(2);
        params.put("start", start);
        params.put("end", end);
        return params;
    }

    public double getStartAsFractionOfPeriod(DateTime beginOfPeriod, DateTime endOfPeriod) {
        if (isCovered(beginOfPeriod, endOfPeriod)) {
            if (start.isBefore(beginOfPeriod)) {//{ ? beginOfPeriod : start;
                return 0;
            }
            else {
                return Days.daysBetween(beginOfPeriod, start).getDays() / coveredPeriodLength(beginOfPeriod, endOfPeriod);
            }
        }
        else {
            return -1;
        }
    }

    public double getEndAsFractionOfPeriod(DateTime beginOfPeriod, DateTime endOfPeriod) {
        if (isCovered(beginOfPeriod, endOfPeriod)) {
            if (end.isAfter(endOfPeriod)) {
                return 1;
            }
            else {
                return Days.daysBetween(beginOfPeriod, end).getDays() / coveredPeriodLength(beginOfPeriod, endOfPeriod);
            }
        }
        else {
            return -1;
        }
    }

    private Double coveredPeriodLength(DateTime begin, DateTime end) {
        Integer days = Days.daysBetween(begin, end).getDays();
        return days.doubleValue();
    }

    public boolean isCovered(DateTime beginOfPeriod, DateTime endOfPeriod) {
        return !(!endOfPeriod.isAfter(start) || !beginOfPeriod.isBefore(end));
    }

    public CoverDuration getCoverDuration(DateTime beginOfPeriod, DateTime endOfPeriod) {
        return new CoverDuration(getStartAsFractionOfPeriod(beginOfPeriod, endOfPeriod), getEndAsFractionOfPeriod(beginOfPeriod, endOfPeriod));
    }
}
