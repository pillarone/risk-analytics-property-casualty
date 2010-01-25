package org.pillarone.riskanalytics.domain.assets

import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class YieldCurveCIRStrategy implements ITermStructure, IParameterObject, IModellingStrategy {

    static final TermStructureType type = TermStructureType.CIR

    double meanReversionParameter;
    double riskAversionParameter;
    double longRunMean;
    double volatility;
    double initialInterestRate;


    public Object getType() {
        type
    }

    public Map getParameters() {
        ["meanReversionParameter": meanReversionParameter,
            "riskAversionParameter": riskAversionParameter,
            "longRunMean": longRunMean,
            "volatility": volatility,
            "initialInterestRate": initialInterestRate]
    }


    public void reset() {

    }

    //todo(cne) !!!! this requires a great deal of calibration !!!!

    public YieldCurveCIRStrategy(YieldModellingChoices curve) {
        meanReversionParameter = curve.getMeanReversionParameter();
        riskAversionParameter = curve.getRiskAversionParameter();
        longRunMean = curve.getLongRunMean();
        volatility = curve.getVolatility();
        initialInterestRate = curve.getInitialInterestRate();
    }

    public YieldCurveCIRStrategy(double meanReversionParameter, double riskAversionParameter, double longRunMean,
                                 double volatility, double initialInterestRate) {
        this.meanReversionParameter = meanReversionParameter;
        this.riskAversionParameter = riskAversionParameter;
        this.longRunMean = longRunMean;
        this.volatility = volatility;
        this.initialInterestRate = initialInterestRate;
    }

    public double termStructureDiscountFactorCalculation(double time) {
        double vol_sqr = Math.pow(volatility, 2);
        double gamma = Math.sqrt(Math.pow((meanReversionParameter + riskAversionParameter), 2) + 2.0 * vol_sqr);
        double denum = (gamma + meanReversionParameter + riskAversionParameter) * (Math.exp(gamma * time) - 1) + 2 * gamma;
        double p = 2 * meanReversionParameter * longRunMean / vol_sqr;
        double enum1 = 2 * gamma * Math.exp(0.5 * (meanReversionParameter + riskAversionParameter + gamma) * time);
        double A = Math.pow((enum1 / denum), p);
        double B = (2 * (Math.exp(gamma * time) - 1)) / denum;
        return A * Math.exp(-B * initialInterestRate);
    }


    public double termStructureYieldCalculation(double time) {
        return Math.pow(this.termStructureDiscountFactorCalculation(time), -1 / time) - 1;
    }

    public double getRiskFreeRate(double time) {
        return termStructureYieldCalculation(time);
    }

    public double getZeroCouponRate(GregorianCalendar date) {
        return 0;
    }
}