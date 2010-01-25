package org.pillarone.riskanalytics.domain.assets;

import org.joda.time.DateTime;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public interface IBondCalculations {

    public double getMarketValue(DateTime date, ITermStructure termStructure);

    public double getDurationDiscrete(DateTime date, ITermStructure termStructure);

    public double getActuarialRate(DateTime date, ITermStructure termStructure); // equals to yield to maturity

    public double getSensitivity();

    public boolean isExpired(DateTime date);

    public double getCapitalGain(DateTime date, ITermStructure termStructure);

    public double getMarketValue(DateTime startDate, DateTime forwardDate, ITermStructure termStructure);

    /**
     * Try to invest the provided amount and return the effectively invested amount.
     *
     * @param amount to invest
     * @return effectively invested amount (should be smaller or equal than amount)
     */
    public double invest(double amount);

    /**
     * Try to sell the provided amount and return the effectively sold amount.
     *
     * @param amount to sell
     * @return effectively invested amount (should be higher or equal than amount)
     */
    public double sell(double amount);

}
