package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import java.util.List;
import java.util.ArrayList;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class Pattern {
    private List<Double> cumulativeValues;

    public Pattern() {
    }

    public Pattern(IPatternStrategy strategy) {
        if (strategy instanceof CumulativePatternStrategy) {
            cumulativeValues = strategy.getPatternValues();
        }
        else if (strategy instanceof IncrementalPatternStrategy) {
            List<Double> incrementalValues = strategy.getPatternValues();
            cumulativeValues = new ArrayList<Double>(incrementalValues.size());
            double cumulative = 0d;
            for (Double increment : incrementalValues) {
                cumulative += increment;
                cumulativeValues.add(cumulative);
            }
        }
        else if (strategy instanceof NoPatternStrategy) {
            cumulativeValues = new ArrayList<Double>(1);
            cumulativeValues.add(1d);
        }
        else {
            throw new IllegalArgumentException("Unknown strategy: " + strategy.getClass());
        }
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
