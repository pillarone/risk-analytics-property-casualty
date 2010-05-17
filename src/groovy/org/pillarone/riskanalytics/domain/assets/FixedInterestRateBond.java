package org.pillarone.riskanalytics.domain.assets;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.domain.assets.constants.Seniority;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class FixedInterestRateBond implements IBondCalculations, Cloneable {

    protected double quantity;
    private double purchasePrice;
    private double bookValue;
    private double faceValue;
    private DateTime maturityDate;
    protected double coupon;                      //todo(cne) here the coupon is fixed. (It could also be named cashflow)
    protected Duration installmentPeriod;
    private Rating rating;
    private Seniority seniority;

    /*public FixedInterestRateBond(double quantity, double purchasePrice, double bookValue, double faceValue,
                                 DateTime maturityDate, Duration installmentPeriod,
                                 Rating rating, Seniority seniority) {
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.bookValue = bookValue;
        this.faceValue = faceValue;
        this.maturityDate = maturityDate;
        this.installmentPeriod = installmentPeriod;
        this.rating = rating;
        this.seniority = seniority;
    }*/

    /*private BondParameters bond;

    public FixedInterestRateBond(BondParameters bond) {
        this.bond = bond;
    }*/

    public FixedInterestRateBond(BondParameters bond) {
        quantity = bond.quantity;
        purchasePrice = bond.purchasePrice;
        bookValue = bond.bookValue;
        faceValue = bond.faceValue;
        maturityDate = bond.maturityDate;
        coupon = bond.coupon;
        installmentPeriod = bond.installmentPeriod;
        rating = bond.rating;
        seniority = bond.seniority;
    }


    //todo (cne) the following converter is very basic, something more robust has to be implemented

    public double convertDurationToPercentageOfAYear(Duration duration) {
        return duration.getMillis() / (float) Duration.standardDays(365).getMillis();
    }


    public double getMarketValue(DateTime date, ITermStructure termStructure) {//todo (cne) add the spread (due to the rating)

        double marketValue = 0;
        int i = 0;
        DateTime tempDate = date.plus(installmentPeriod); //todo (cne) check if the coupon occurs immediately of not
        Duration tempDuration = new Duration(date, tempDate);

        while (!isExpired(tempDate)) {
            marketValue += coupon / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i);
            i++;
            tempDate = tempDate.plus(installmentPeriod);
            tempDuration = tempDuration.plus(installmentPeriod);
            //int iterationCounter = i;
        }
        //i = iterationCounter;
        return quantity * (marketValue + (coupon + getFaceValue()) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i));
    }

    //the following function is used when one wants to use the term structure as of now to calculate market values for future periods (i.e. forward values)
    public double getMarketValue(DateTime startDate, DateTime forwardDate, ITermStructure termStructure) {

        int i = 0;
        double marketValue = 0;
        DateTime tempDate = forwardDate.plus(installmentPeriod);
        Duration tempDuration = new Duration(startDate, tempDate);

        while (!isExpired(tempDate)) {
            marketValue += coupon / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i);
            i++;
            tempDate = tempDate.plus(installmentPeriod);
            tempDuration = tempDuration.plus(installmentPeriod);
        }
        return quantity * (marketValue + (coupon + getFaceValue()) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i));
    }


    public double getDurationDiscrete(DateTime date, ITermStructure termStructure) {

        double numerator = 0;
        double denominator = 0;
        //int numberOfCashFlows = (int) installmentPeriod * numberOfPeriods(installmentPeriod, date, maturityDate);
        int i = 0;
        DateTime tempDate = date.plus(installmentPeriod);
        Duration tempDuration = new Duration(date, tempDate);
        while (!isExpired(tempDate)) {

            numerator += i * coupon / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i);
            denominator += coupon / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i);
            i++;
            tempDate = tempDate.plus(installmentPeriod);
            tempDuration = tempDuration.plus(installmentPeriod);
        }
        return (numerator + i * (coupon + getFaceValue()) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i)) /
                (denominator + (coupon + getFaceValue()) / Math.pow(1 + termStructure.getRiskFreeRate(convertDurationToPercentageOfAYear(tempDuration)), i));
    }


    public double BondPriceDiscrete(DateTime date, double r) {
        double bondPrice = 0;
        int i = 0;
        DateTime tempDate = date.plus(installmentPeriod);
        while (!isExpired(tempDate)) {

            bondPrice += coupon / Math.pow(1 + r, i);
            i++;
            tempDate = tempDate.plus(installmentPeriod);


        }
        return quantity * (bondPrice + (coupon + getFaceValue()) / Math.pow(1 + r, i));
    }

    public double getActuarialRate(DateTime date, ITermStructure termStructure) {
        final double ACCURACY = 1 * Math.pow(10, -5);
        final int MAX_ITERATIONS = 200;
        double bottom = 0;
        double top = 1.0;

        FixedInterestRateBond marketReferenceBond = this.clone();

        while (BondPriceDiscrete(date, top) > marketReferenceBond.getMarketValue(date, termStructure)) {
            top = top * 2;
        }
        double r = 0.5 * (top + bottom);
        for (int i = 1; i <= MAX_ITERATIONS; i++) {
            double diff = BondPriceDiscrete(date, r) - marketReferenceBond.getMarketValue(date, termStructure);
            if (Math.abs(diff) < ACCURACY)
                return r;
            else if (diff > 0.0) {
                bottom = r;
            }
            else {
                top = r;
            }
            r = 0.5 * (top + bottom);
        }
        return r;
    }

    @Override
    public FixedInterestRateBond clone() {
        try {
            return (FixedInterestRateBond) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getSensitivity() {
        return 0;
    }

    public boolean isExpired(DateTime date) {

        return date.isAfter(maturityDate);
    }

    public double getCapitalGain(DateTime date, ITermStructure termStructure) { // or "net market value", or "realized gains"
        return this.getMarketValue(date, termStructure) - quantity * getBookValue();
    }

    /**
     * Try to invest the provided amount and return the effectively invested amount.
     *
     * @param amount to invest
     * @return effectively invested amount (should be smaller or equal than amount)
     */
    public double invest(double amount) {
        double investedAmount = 0;
        if (purchasePrice > 0) {
            while (amount > purchasePrice) {
                investedAmount += purchasePrice;
                amount -= purchasePrice;
            }
        }
        return investedAmount;
    }

    /**
     * Try to sell the provided amount and return the effectively sold amount.
     *
     * @param amount to sell
     * @return effectively invested amount (should be higher or equal than amount)
     */
    public double sell(double amount) { //todo(cne) should it be something else than purchasePrice here?
        double amountSold = 0;
        while (amount > purchasePrice) {
            amountSold += purchasePrice;
            amount -= purchasePrice;
        }
        return amountSold;
    }

    public double getBookValue() {
        return bookValue;
    }

    public void setBookValue(double bookValue) {
        this.bookValue = bookValue;
    }

    public double getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(double faceValue) {
        this.faceValue = faceValue;
    }
}