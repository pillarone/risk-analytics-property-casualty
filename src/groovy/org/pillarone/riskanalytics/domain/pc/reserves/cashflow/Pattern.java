package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Pattern {
    private List<Double> cumulativeValues;
    private List<Period> cumulativePeriods;

    public Pattern() {
    }

    public Pattern(IPatternStrategy strategy) {
        cumulativePeriods = strategy.getCumulativePeriods();
        cumulativeValues = strategy.getCumulativePatternValues();
    }

    public double remainingPaid(int elapsedMonths) {
        int indexAboveElapsedMonths = 0;
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths < cumulativePeriods.get(i).getMonths()) {
                indexAboveElapsedMonths = i;
                double periodRatio = (elapsedMonths - cumulativePeriods.get(indexAboveElapsedMonths - 1).getMonths())
                    / (double) (cumulativePeriods.get(indexAboveElapsedMonths).getMonths() - cumulativePeriods.get(indexAboveElapsedMonths - 1).getMonths());
                double paidPortion = (cumulativeValues.get(indexAboveElapsedMonths) - cumulativeValues.get(indexAboveElapsedMonths - 1)) * periodRatio;
                return 1 - cumulativeValues.get(indexAboveElapsedMonths - 1) - paidPortion;
            }
            else if (elapsedMonths == cumulativePeriods.get(i).getMonths()) {
                return 1 - cumulativeValues.get(i);
            }
        }
        // elapseMonths is after latest period
        return 0d;
    }

    public Integer nextPayoutIndex(int elapsedMonths) {
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths < cumulativePeriods.get(i).getMonths()) {
                return i;
            }
        }
        // elapseMonths is after latest period
        return null;
    }

    public Integer thisOrNextPayoutIndex(int elapsedMonths) {
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths <= cumulativePeriods.get(i).getMonths()) {
                return i;
            }
        }
        // elapseMonths is after latest period
        return null;
    }

//    public double adjustedIncrementalFactor(DateTime occurrenceDate, DateTime updateDate, int elapsedMonths) {
//        Integer payoutIndex = thisOrNextPayoutIndex(elapsedMonths);
//        double originalIncrementalFactor =  incrementFactor(payoutIndex);
//        DateTime patternDateNext = occurrenceDate.plus(cumulativeLapseTime(payoutIndex));
//        if (updateDate.equals(patternDateNext)) return originalIncrementalFactor;   // no adjustment required
//        DateTime patternDateBefore = occurrenceDate.plus(cumulativeLapseTime(payoutIndex - 1));
//        double timeFraction = Days.daysBetween(updateDate, patternDateNext).getDays()
//                / (double) Days.daysBetween(patternDateBefore, patternDateNext).getDays();
//        return originalIncrementalFactor * timeFraction;
//    }

    public double adjustedIncrementalFactor(DateTime occurrenceDate, DateTime updateDate, int elapsedMonths) {
        double originalIncrementalFactor =  incrementFactor(nextPayoutIndex(elapsedMonths));
        Integer nextPayoutIndex = nextPayoutIndex(elapsedMonths);
        DateTime patternDateNext = occurrenceDate.plus(cumulativeLapseTime(nextPayoutIndex));
        DateTime patternDateBefore = occurrenceDate.plus(cumulativeLapseTime(nextPayoutIndex - 1));
        double timeFraction = Months.monthsBetween(updateDate, patternDateNext).getMonths()
                / (double) Months.monthsBetween(patternDateBefore, patternDateNext).getMonths();
        return originalIncrementalFactor * timeFraction;
    }

    public int size() {
        return cumulativeValues.size();
    }

    public boolean isTrivial() {
        return size() == 0 || (size() == 1 && cumulativeValues.get(0) == 1d);
    }

    public double incrementFactor(int developmentPeriod) {
        if (developmentPeriod == 0) {
            return cumulativeValues.get(0);
        }
        return cumulativeValues.get(developmentPeriod) - cumulativeValues.get(developmentPeriod - 1);
    }

    public Period cumulativeLapseTime(int developmentPeriod) {
        return cumulativePeriods.get(developmentPeriod);
    }

    public double cumulativeFactor(int developmentPeriod) {
        return cumulativeValues.get(developmentPeriod);
    }

    public double cumulativeFactorHistoric(int developmentPeriod) {
        return cumulativeValues.get(developmentPeriod - 1);
    }

    public List<Double> getCumulativeValues() {
        return cumulativeValues;
    }
}
