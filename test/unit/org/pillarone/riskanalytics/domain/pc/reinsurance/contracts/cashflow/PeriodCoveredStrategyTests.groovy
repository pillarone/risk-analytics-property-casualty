package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PeriodCoveredStrategyTests extends GroovyTestCase {

    DateTime date20100101 = new DateTime(2010,01,01,0,0,0,0)
    DateTime date20100127 = new DateTime(2010,01,27,0,0,0,0)
    DateTime date20100331 = new DateTime(2010,03,31,0,0,0,0)
    DateTime date20101212 = new DateTime(2010,12,12,0,0,0,0)
    DateTime date20110101 = new DateTime(2011,01,01,0,0,0,0)

    void testIsCovered() {
        ICoverPeriod periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100127, 'end': date20101212])
        assertTrue 'Full year is covered by range of year', periodCoveredStrategy.isCovered(date20100101, date20110101)
        assertFalse 'End date before start of cover period', periodCoveredStrategy.isCovered(date20100101, date20100101)
        assertFalse 'Start date after end of cover period', periodCoveredStrategy.isCovered(date20110101, date20110101)
    }

    void testGetStartAsFractionOfPeriod() {
        ICoverPeriod periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100101, 'end': date20101212])
        assertEquals 'start equal begin of period', 0, periodCoveredStrategy.getStartAsFractionOfPeriod(date20100101, date20110101)

        periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100127, 'end': date20101212])
        assertEquals 'start after begin of period', 26 / 365d, periodCoveredStrategy.getStartAsFractionOfPeriod(date20100101, date20110101)
        assertEquals 'start after begin of period, end equal end of period', 26 / 345d, periodCoveredStrategy.getStartAsFractionOfPeriod(date20100101, date20101212)
    }

    void testGetEndAsFractionOfPeriod() {
        ICoverPeriod periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100101, 'end': date20110101])
        assertEquals 'end equal end of period ', 1, periodCoveredStrategy.getEndAsFractionOfPeriod(date20100101, date20110101)
        assertEquals 'end after end of period', 1, periodCoveredStrategy.getEndAsFractionOfPeriod(date20100101, date20101212)

        periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100127, 'end': date20101212])
        assertEquals 'end before end of period', 345 / 365d, periodCoveredStrategy.getEndAsFractionOfPeriod(date20100101, date20110101)
        assertEquals 'end equal end of period', 1, periodCoveredStrategy.getEndAsFractionOfPeriod(date20100101, date20101212)
    }

    void testGetCoverDuration() {
        ICoverPeriod periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100101, 'end': date20110101])
        CoverDuration coverDuration = periodCoveredStrategy.getCoverDuration(date20100101, date20110101)
        assertTrue '1. begin of period', coverDuration.isCovered(0)
        assertTrue '1. mid of period', coverDuration.isCovered(0.5)
        assertTrue '1. end of period', coverDuration.isCovered(1)

        periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100101, 'end': date20101212])
        coverDuration = periodCoveredStrategy.getCoverDuration(date20100101, date20110101)
        assertTrue '2. begin of period', coverDuration.isCovered(0)
        assertTrue '2. mid of period', coverDuration.isCovered(0.5)
        assertFalse '2. end of period', coverDuration.isCovered(1)

        periodCoveredStrategy = CoverPeriodType.getCoverPeriod(
                CoverPeriodType.PERIOD, ['start': date20100127, 'end': date20101212])
        coverDuration = periodCoveredStrategy.getCoverDuration(date20100101, date20110101)
        assertFalse '2. begin of period', coverDuration.isCovered(0)
        assertTrue '2. mid of period', coverDuration.isCovered(0.5)
        assertFalse '2. end of period', coverDuration.isCovered(1)
    }
}
