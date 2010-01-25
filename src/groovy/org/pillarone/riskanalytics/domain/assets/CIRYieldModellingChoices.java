package org.pillarone.riskanalytics.domain.assets;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class CIRYieldModellingChoices extends YieldModellingChoices {

    private double meanReversionParameter;
    private double riskAversionParameter;
    private double longRunMean;
    private double volatility;
    private double initialInterestRate;

    public double getMeanReversionParameter() {
        return meanReversionParameter;
    }

    public void setMeanReversionParameter(double meanReversionParameter) {
        this.meanReversionParameter = meanReversionParameter;
    }

    public double getRiskAversionParameter() {
        return riskAversionParameter;
    }

    public void setRiskAversionParameter(double riskAversionParameter) {
        this.riskAversionParameter = riskAversionParameter;
    }

    public double getLongRunMean() {
        return longRunMean;
    }

    public void setLongRunMean(double longRunMean) {
        this.longRunMean = longRunMean;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getInitialInterestRate() {
        return initialInterestRate;
    }

    public void setInitialInterestRate(double initialInterestRate) {
        this.initialInterestRate = initialInterestRate;
    }
}