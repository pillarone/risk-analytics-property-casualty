package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CoverDuration {
    private double start;
    private double end;
    private boolean covered;

    public CoverDuration(double start, double end) {
        this.start = start;
        this.end = end;
        covered = start != -1 && end != -1;
    }

    public CoverDuration(boolean covered) {
        start = -1;
        end = -1;
        this.covered = covered;
    }

    public boolean isCovered() {
        return covered;
    }

    public boolean isCovered(double fractionOfPeriod) {
        if (fractionOfPeriod < 0 || fractionOfPeriod > 1) {
            throw new IllegalArgumentException("['CoverDuration.outOfInterval','"+fractionOfPeriod+"']");
        }
        return start <= fractionOfPeriod && fractionOfPeriod <= end;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("start: ").append(start).append(", end: ").append(end).append(", covered: ").append(covered).toString();
    }
}