package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

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
