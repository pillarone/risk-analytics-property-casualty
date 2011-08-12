package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.joda.time.Period;

import java.util.List;

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public interface IPattern {

    int size();

    List<Double> getCumulativeValues();

    boolean isTrivial();

    double incrementFactor(int developmentFactor);

    double cumulativeFactor(int developmentPeriod);

    double outstandingShare(double elapsedMonths);

    Integer nextPayoutIndex(double elapsedMonths);

    Integer thisOrNextPayoutIndex(double elapsedMonths);

    Double incrementFactorElapsed(double elapsedMonths, double outstandingShare );

    double incrementFactor( int payoutIndex, double outstandingShare);

    Period cumulativeLapseTime(int payoutIndex);

}