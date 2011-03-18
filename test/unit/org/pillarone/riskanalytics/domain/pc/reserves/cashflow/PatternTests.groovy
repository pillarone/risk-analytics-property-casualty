package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.joda.time.Period
import org.jfree.data.time.Month
import org.joda.time.Months

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PatternTests extends GroovyTestCase {

    static double EPSILON = 1E-10

    Pattern pattern


    @Override protected void setUp() {
        pattern = new Pattern()
        pattern.cumulativePeriods = [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)]
        pattern.cumulativeValues = [0d, 0.7d, 0.8d, 0.95d, 1.0d]
    }

    void testOutstandingShare() {
        assertEquals 'remaining after 0 months', 1.0, pattern.outstandingShare(0)
        assertEquals 'remaining after 6 months', 0.65, pattern.outstandingShare(6)
        assertEquals 'remaining after 11 months', (0.3 + 0.7 * 1.0/12.0), pattern.outstandingShare(11), EPSILON
        assertEquals 'remaining after 12 months', 0.3, pattern.outstandingShare(12), EPSILON
        assertEquals 'remaining after 18 months', 0.25, pattern.outstandingShare(18)
        assertEquals 'remaining after 36 months', 0.05, pattern.outstandingShare(36), 1E-10
        assertEquals 'remaining after 48 months', 0.0, pattern.outstandingShare(48)
        assertEquals 'remaining after 60 months', 0.0, pattern.outstandingShare(60)
    }

    void testNextPayoutIndex() {
        assertEquals 'next payout index 0 months', 1, pattern.nextPayoutIndex(0)
        assertEquals 'next payout index 6 months', 1, pattern.nextPayoutIndex(6)
        assertEquals 'next payout index 12 months', 2, pattern.nextPayoutIndex(12), 1E-10
        assertEquals 'next payout index 18 months', 2, pattern.nextPayoutIndex(18)
        assertEquals 'next payout index 36 months', 4, pattern.nextPayoutIndex(36), 1E-10
        assertEquals 'next payout index 48 months', null, pattern.nextPayoutIndex(48)
        assertEquals 'next payout index 60 months', null, pattern.nextPayoutIndex(60)
    }

    void testThisOrNextPayoutIndex() {
        assertEquals 'next payout index 0 months', 0, pattern.thisOrNextPayoutIndex(0)
        assertEquals 'next payout index 6 months', 1, pattern.thisOrNextPayoutIndex(6)
        assertEquals 'next payout index 12 months', 1, pattern.thisOrNextPayoutIndex(12), 1E-10
        assertEquals 'next payout index 18 months', 2, pattern.thisOrNextPayoutIndex(18)
        assertEquals 'next payout index 36 months', 3, pattern.thisOrNextPayoutIndex(36), 1E-10
        assertEquals 'next payout index 48 months', 4, pattern.thisOrNextPayoutIndex(48)
        assertEquals 'next payout index 60 months', null, pattern.thisOrNextPayoutIndex(60)
    }

    void testAdjustedIncrementalFactor() {
        assertEquals '0 months elapsed', 0.7d, pattern.adjustedIncrementalFactor(0)
        assertEquals '6 months elapsed', 0.35d, pattern.adjustedIncrementalFactor(6)
        assertEquals '12 months elapsed', 0.1d, pattern.adjustedIncrementalFactor(12), EPSILON
        assertEquals '15 months elapsed', 0.075, pattern.adjustedIncrementalFactor(15), EPSILON
        assertEquals '18 months elapsed', 0.05, pattern.adjustedIncrementalFactor(18), EPSILON
        assertEquals '24 months elapsed', 0.15d, pattern.adjustedIncrementalFactor(24), EPSILON
        assertEquals '27 months elapsed', 0.15d * 0.75, pattern.adjustedIncrementalFactor(27), EPSILON
        assertEquals '30 months elapsed', 0.15d * 0.5, pattern.adjustedIncrementalFactor(30), EPSILON
    }

    void testIncrementFactor() {
        assertEquals 'development period 0', 0d, pattern.incrementFactor(0)
        assertEquals 'development period 1', 0.7d, pattern.incrementFactor(1)
        assertEquals 'development period 2', 0.1d, pattern.incrementFactor(2), EPSILON
        assertEquals 'development period 3', 0.15d, pattern.incrementFactor(3), EPSILON
        assertEquals 'development period 4', 0.05d, pattern.incrementFactor(4), EPSILON

        assertEquals 'elapsed months 0, oustanding share 1', 0d, pattern.incrementFactor(0, 1d)
        assertEquals 'elapsed months 0, oustanding share 0.5', 0d, pattern.incrementFactor(0, 0.5d)

//        assertEquals 'elapsed month 0, period', 0, pattern.incrementFactor(Period.months(0))
//        assertEquals 'elapsed month 0, period', 0, pattern.incrementFactor(Period.months(6))
//        assertEquals 'elapsed month 0, period', 0, pattern.incrementFactor(Period.months(12))
//        assertEquals 'elapsed month 0, period', 0, pattern.incrementFactor(Period.months(24))
//        assertEquals 'elapsed month 0, period', 0, pattern.incrementFactor(Period.months(36))
//        assertEquals 'elapsed month 0, period', 0, pattern.incrementFactor(Period.months(48))
    }

    void testIncrementFactorElapsed() {
        assertEquals 'increment factor elapsed 0, outstanding 1', 0.7, pattern.incrementFactorElapsed(0, 1)
        assertEquals 'increment factor elapsed 0, outstanding 0.5', 1.4, pattern.incrementFactorElapsed(0, 0.5)
        assertEquals 'increment factor elapsed 6, outstanding 1', 0.35, pattern.incrementFactorElapsed(6, 1)
        assertEquals 'increment factor elapsed 12, outstanding 1', 0.1, pattern.incrementFactorElapsed(12, 1), EPSILON
        assertEquals 'increment factor elapsed 18, outstanding 1', 0.05, pattern.incrementFactorElapsed(18, 1), EPSILON
        assertEquals 'increment factor elapsed 24, outstanding 1', 0.15, pattern.incrementFactorElapsed(24, 1), EPSILON
        assertEquals 'increment factor elapsed 36, outstanding 1', 0.05, pattern.incrementFactorElapsed(36, 1), EPSILON
        assertEquals 'increment factor elapsed 42, outstanding 1', 0.025, pattern.incrementFactorElapsed(42, 1), EPSILON
        assertEquals 'increment factor elapsed 48, outstanding 1', null, pattern.incrementFactorElapsed(48, 1)
    }

    void testIncrementMonths() {
        assertEquals 'increment months period 0', 0, pattern.incrementMonths(0)
        assertEquals 'increment months period 1', 12, pattern.incrementMonths(1)
        assertEquals 'increment months period 2', 12, pattern.incrementMonths(2)
        assertEquals 'increment months period 3', 12, pattern.incrementMonths(3)
        assertEquals 'increment months period 4', 12, pattern.incrementMonths(4)
        assertEquals 'increment months period 6', null, pattern.incrementMonths(6)
    }
}
