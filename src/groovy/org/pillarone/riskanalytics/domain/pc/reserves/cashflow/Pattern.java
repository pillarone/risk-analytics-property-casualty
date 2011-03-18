package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.jfree.data.time.Month;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Period;

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

    /**
     * @param elapsedMonths
     * @return outstanding share after elapsedMonths using a linear interpolation if elapsedMonths is not part of the cumulativePeriods
     */
    public double outstandingShare(double elapsedMonths) {
        int indexAboveElapsedMonths = 0;
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths < cumulativePeriods.get(i).getMonths()) {
                indexAboveElapsedMonths = i;
                int numberOfMonthsBelowElapsedMonths = cumulativePeriods.get(indexAboveElapsedMonths - 1).getMonths();
                int numberOfMonthsAboveElapsedMonths = cumulativePeriods.get(indexAboveElapsedMonths).getMonths();
                double valueBelowElapsedMonths = cumulativeValues.get(indexAboveElapsedMonths - 1);
                double valueAboveElapsedMonths = cumulativeValues.get(indexAboveElapsedMonths);
                double periodRatio = (elapsedMonths - numberOfMonthsBelowElapsedMonths)
                        / (double) (numberOfMonthsAboveElapsedMonths - numberOfMonthsBelowElapsedMonths);
                double paidPortion = (valueAboveElapsedMonths - valueBelowElapsedMonths) * periodRatio;
                return 1 - valueBelowElapsedMonths - paidPortion;
            }
            else if (elapsedMonths == cumulativePeriods.get(i).getMonths()) {
                return 1 - cumulativeValues.get(i);
            }
        }
        // elapseMonths is after latest period
        return 0d;
    }

    /**
     * @param elapsedMonths
     * @return nearest pattern index with month value lower elapsedMonths or null if elapsedMonths is after last period
     */
    public Integer nextPayoutIndex(double elapsedMonths) {
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths < cumulativePeriods.get(i).getMonths()) {
                return i;
            }
        }
        // elapseMonths is after latest period
        return null;
    }

    /**
     * @param elapsedMonths
     * @return nearest pattern index with month value lower or equal elapsedMonths or null if elapsedMonths is after last period
     */
    public Integer thisOrNextPayoutIndex(double elapsedMonths) {
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths <= cumulativePeriods.get(i).getMonths()) {
                return i;
            }
        }
        // elapseMonths is after latest period
        return null;
    }

    /**
     * @deprecated use adjustedIncrementalFactor(double elapsedMonths)
     * @param occurrenceDate
     * @param updateDate
     * @param elapsedMonths
     * @return
     */
    @Deprecated
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

    public double incrementFactor(int developmentPeriod, double outstandingShare) {
        return incrementFactor(developmentPeriod) / outstandingShare;
    }

//    public double incrementFactor(Period months) {
//        return cumulativeValues.get(cumulativePeriods.indexOf(months));
//    }

    /**
     * Corrects the incremental factor following after elapsedMonths
     * @param elapsedMonths
     * @return
     */
    public Double adjustedIncrementalFactor(double elapsedMonths) {
        Integer nextPayoutIndex = nextPayoutIndex(elapsedMonths);
        if (nextPayoutIndex == null) return null;
        double upperIncrementalFactor =  incrementFactor(nextPayoutIndex);
        int upperCumulativeMonths = cumulativePeriods.get(nextPayoutIndex).getMonths();
        double timeFraction = (upperCumulativeMonths - elapsedMonths) / (double) incrementMonths(nextPayoutIndex);
        return upperIncrementalFactor * timeFraction;
    }

    public Double incrementFactorElapsed(double elapsedMonths, double outstandingShare) {
        Double factor = adjustedIncrementalFactor(elapsedMonths);
        return factor == null ? null : factor / outstandingShare;
    }

    public Integer incrementMonths(int developmentPeriod) {
        if (developmentPeriod >= size()) return null;
        if (developmentPeriod == 0) {
            return cumulativePeriods.get(0).getMonths();
        }
        else {
            return cumulativePeriods.get(developmentPeriod).getMonths() - cumulativePeriods.get(developmentPeriod - 1).getMonths();
        }
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
