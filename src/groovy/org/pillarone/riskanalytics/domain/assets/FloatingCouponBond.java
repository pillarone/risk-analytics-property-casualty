package org.pillarone.riskanalytics.domain.assets;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class FloatingCouponBond extends FixedInterestRateBond {
//todo(cne) rename to FloatingRateBond? /define master classes "GovernmentBond"-"CorporateBond"?

    //private IModellingStrategy termStructure = ModellingStrategyFactory.getModellingStrategy(TermStructureType.CIR,
    //      ArrayUtils.toMap(new Object[][]{{"meanReversionParameter", 0.08},
    //            {"riskAversionParameter", 0},
    //          {"longRunMean", 0.08},
    //        {"volatility", 0.01},
    //      {"initialInterestRate", 0.05}}));

    private YieldCurveCIRStrategy termStructure = new YieldCurveCIRStrategy(0.01, 0, 0.08, 0.01, 0.05);
    //todo(cne) check if CIR is appropriate for modelling inflation (for instance)

    public FloatingCouponBond(BondParameters bond) {
        super(bond);
    }

    public double floatingCoupon(double time) {
        return coupon * (1 + termStructure.getRiskFreeRate(time));
    }


    public double getMarketValue(DateTime date, ITermStructure termStructure) {

        double marketValue = 0;
        int i = 0;
        DateTime tempDate = date.plus(installmentPeriod); //todo (cne) check if the coupon occurs immediately of not
        Duration tempDuration = new Duration(date, tempDate);

        while (!isExpired(tempDate)) {
            marketValue += this.clone().floatingCoupon(convertDurationToPercentageOfAYear(tempDuration)) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i);
            i++;
            tempDate = tempDate.plus(installmentPeriod);
            tempDuration = tempDuration.plus(installmentPeriod);
        }
        return quantity * (marketValue + (this.clone().floatingCoupon(convertDurationToPercentageOfAYear(tempDuration)) + getFaceValue()) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i));
    }


    public double getMarketValue(DateTime startDate, DateTime forwardDate, ITermStructure termStructure) {

        double marketValue = 0;
        int i = 0;
        DateTime tempDate = forwardDate.plus(installmentPeriod);
        Duration tempDuration = new Duration(startDate, tempDate);

        while (!isExpired(tempDate)) {
            marketValue += this.clone().floatingCoupon(convertDurationToPercentageOfAYear(tempDuration)) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i);
            i++;
            tempDate = tempDate.plus(installmentPeriod);
            tempDuration = tempDuration.plus(installmentPeriod);
        }
        return quantity * (marketValue + (this.clone().floatingCoupon(convertDurationToPercentageOfAYear(tempDuration)) + getFaceValue()) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i));
    }

    @Override
    public FloatingCouponBond clone() {
        return (FloatingCouponBond) super.clone();
    }


}
