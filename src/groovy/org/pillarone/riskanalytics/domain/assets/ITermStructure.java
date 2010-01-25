package org.pillarone.riskanalytics.domain.assets;


import java.util.GregorianCalendar;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public interface ITermStructure {

    double getRiskFreeRate(double time);
    double getZeroCouponRate(GregorianCalendar date);

    

}
