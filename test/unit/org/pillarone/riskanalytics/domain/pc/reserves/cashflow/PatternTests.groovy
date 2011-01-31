package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.joda.time.Period
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PatternTests extends GroovyTestCase {

    static double EPSILON = 1E-10

    void testRemainingPaid() {
        Pattern pattern = new Pattern()
        pattern.cumulativePeriods = [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)]
        pattern.cumulativeValues = [0d, 0.7d, 0.8d, 0.95d, 1.0d]

        assertEquals 'remaining after 0 months', 1.0, pattern.remainingPaid(0)
        assertEquals 'remaining after 6 months', 0.65, pattern.remainingPaid(6)
        assertEquals 'remaining after 11 months', (0.3 + 0.7 * 1.0/12.0), pattern.remainingPaid(11), EPSILON
        assertEquals 'remaining after 12 months', 0.3, pattern.remainingPaid(12), EPSILON
        assertEquals 'remaining after 18 months', 0.25, pattern.remainingPaid(18)
        assertEquals 'remaining after 36 months', 0.05, pattern.remainingPaid(36), 1E-10
        assertEquals 'remaining after 48 months', 0.0, pattern.remainingPaid(48)
        assertEquals 'remaining after 60 months', 0.0, pattern.remainingPaid(60)
    }

    void testNextPayoutIndex() {
        Pattern pattern = new Pattern()
        pattern.cumulativePeriods = [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)]
        pattern.cumulativeValues = [0d, 0.7d, 0.8d, 0.95d, 1.0d]

        assertEquals 'next payout index 0 months', 1, pattern.nextPayoutIndex(0)
        assertEquals 'next payout index 6 months', 1, pattern.nextPayoutIndex(6)
        assertEquals 'next payout index 12 months', 2, pattern.nextPayoutIndex(12), 1E-10
        assertEquals 'next payout index 18 months', 2, pattern.nextPayoutIndex(18)
        assertEquals 'next payout index 36 months', 4, pattern.nextPayoutIndex(36), 1E-10
        assertEquals 'next payout index 48 months', null, pattern.nextPayoutIndex(48)
        assertEquals 'next payout index 60 months', null, pattern.nextPayoutIndex(60)
    }

//    void testAdjustedIncrementalFactor() {
//        Pattern pattern = new Pattern()
//        pattern.cumulativePeriods = [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)]
//        pattern.cumulativeValues = [0d, 0.7d, 0.8d, 0.95d, 1.0d]
//        DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
//        DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
//        DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)
//        DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)
//        DateTime date20140101 = new DateTime(2014,1,1,0,0,0,0)
//
//        assertEquals 'update date == pattern date 12', 0.7, pattern.adjustedIncrementalFactor(date20100101, date20110101, 12)
//        assertEquals 'update date == pattern date 24', 0.1, pattern.adjustedIncrementalFactor(date20100101, date20110101, 24), EPSILON
//        assertEquals 'update date == pattern date 36', 0.15 * 731 / 366, pattern.adjustedIncrementalFactor(date20100101, date20110101, 36)
//        assertEquals 'update date == pattern date 48', 0.05 * 1096 / 366, pattern.adjustedIncrementalFactor(date20100101, date20110101, 48)
//
//    }
}
