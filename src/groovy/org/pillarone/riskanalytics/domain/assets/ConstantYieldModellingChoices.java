package org.pillarone.riskanalytics.domain.assets;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class ConstantYieldModellingChoices extends YieldModellingChoices {

    private double rate = 0;

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}