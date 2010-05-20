package org.pillarone.riskanalytics.domain.pc.utils

import org.pillarone.riskanalytics.domain.utils.DateTimeUtilities
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.text.SimpleDateFormat
import org.joda.time.Period

/**
 * We expect mapDateToPeriod(date, startDate) to "rasterize" dates to signed
 * integer values within calendar-year-length periods starting on the given
 * startDate, always rounding date down before truncating fractional periods.
 *
 * ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class DateTimeUtilitiesTests extends GroovyTestCase {

    DateTime date20081213 = new DateTime(2008,12,13,0,0,0,0)
    DateTime date20090516 = new DateTime(2009,5,16,0,0,0,0)
    DateTime date20090504 = new DateTime(2009,5,4,0,0,0,0)
    DateTime date20090524 = new DateTime(2009,5,24,0,0,0,0)
    DateTime date20091024 = new DateTime(2009,10,24,0,0,0,0)
    DateTime date20091115= new DateTime(2009,11,15,0,0,0,0)
    DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
    DateTime date20100201 = new DateTime(2010,2,1,0,0,0,0)
    DateTime date20100401 = new DateTime(2010,4,1,0,0,0,0)
    DateTime date20100501 = new DateTime(2010,5,1,0,0,0,0)
    DateTime date20100701 = new DateTime(2010,7,1,0,0,0,0)
    DateTime date20100704 = new DateTime(2010,7,4,0,0,0,0)
    DateTime date20101001 = new DateTime(2010,10,1,0,0,0,0)
    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110201 = new DateTime(2011,2,1,0,0,0,0)
    DateTime date20110401 = new DateTime(2011,4,1,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)
    DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)

    void testConvertToDateTime() {

        List stableDates = [
                '1969-12-31',  // boundary case for possible glitch
                '1970-01-01',
                '1999-12-31',
                '2000-01-01',
                '2000-08-31',  // 08 would be illegal in octal
                '2000-08-09',  // 08, 09 would be illegal in octal
                '2099-12-31',
                '2999-12-31',
                '9999-12-31',
                '10000-01-01',
                '99999-12-31',
        ]
        for (int i = 0; i < stableDates.size(); i++) {
            String inputString = stableDates[i]
            DateTime interpretedDate = DateTimeUtilities.convertToDateTime(inputString)
            String checkString = ISODateTimeFormat.date().print(interpretedDate);
            assertEquals "Date interpreted matches input string $inputString", inputString, checkString
            // todo(bgi)?: assertEquals "convertToDateTime & Joda interpretations match", ...
        }

        Map interpretations = [
                   '0-1-1':'-0001-12-29',
                   '1-1-1': '0000-12-29',
                  '10-1-1': '0009-12-29',
                 '100-1-1': '0099-12-29',
                '1000-1-1': '1000-01-05',
                '2000-1-1': '2000-01-01',
        ]
        for (String inputString in interpretations.keySet()) {
            DateTime interpretedDate = DateTimeUtilities.convertToDateTime(inputString)
            String checkString = ISODateTimeFormat.date().print(interpretedDate);
            String expectedString = interpretations[inputString]
            assertEquals "Date $inputString interpreted as $expectedString", expectedString, checkString
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List illegalDateFields = [
                '2000-0-0',
                '2000-00-00',
                '2000-01-32',
                '2000-02-30',
                '2000-03-32',
                '2000-04-31',
                '2000-05-32',
                '2000-06-31',
                '2000-07-32',
                '2000-08-32',
                '2000-09-31',
                '2000-10-32',
                '2000-11-31',
                '2000-12-32',
        ]
        for (int i = 0; i < illegalDateFields.size(); i++) {
            String inputString = illegalDateFields[i]
            DateTime interpretedDate = DateTimeUtilities.convertToDateTime(inputString)
            String jodaString = ISODateTimeFormat.date().print(interpretedDate);
            String javaString = formatter.print(interpretedDate)
            assertEquals "illegalDate '$inputString' interpreted by Joda as '$jodaString', by Java as '$javaString' (Java string should be null)", null, javaString
        }

        List missingDateFields = [
                '-', '-1', '1-', '1-1',
                '--', '--1', '-1-', '-1-1', '1--', '1--1', '1-1-',
        ]
        for (int i = 0; i < missingDateFields.size(); i++) {
            String inputString = missingDateFields[i]
            shouldFail RuntimeException, { DateTimeUtilities.convertToDateTime(inputString) }
        }

        assertTrue 'Empty string returns null DateTime', DateTimeUtilities.convertToDateTime('') == null
        shouldFail RuntimeException, { DateTimeUtilities.convertToDateTime('2000') }
        shouldFail RuntimeException, { DateTimeUtilities.convertToDateTime('a-b-c') }
    }

    void testMapDateToPeriod() {

        DateTime date20090101 = new DateTime(2009,1,1,0,0,0,0)
        DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
        DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)

        DateTime date20091231 = new DateTime(2009,12,31,0,0,0,0)
        DateTime date20101231 = new DateTime(2010,12,31,0,0,0,0)
        DateTime date20111231 = new DateTime(2011,12,31,0,0,0,0)

        DateTime date20090701 = new DateTime(2009,7,1,0,0,0,0)
        DateTime date20090630 = new DateTime(2009,6,30,0,0,0,0)

        DateTime date20080229 = new DateTime(2008,2,29,0,0,0,0)

        DateTime date20080301 = new DateTime(2008,3,1,0,0,0,0)

        assertEquals 'dates in same year (year-end to next-year-start): floor(0.01)=0', 0, DateTimeUtilities.mapDateToPeriod(date20100101, date20091231)
        assertEquals 'one year after start date (year-start to next-year-start): floor(1.00)=1', 1, DateTimeUtilities.mapDateToPeriod(date20110101, date20100101)
        assertEquals 'one year after start date (year-start to next-year-end): floor(1.99)=1', 1, DateTimeUtilities.mapDateToPeriod(date20111231, date20100101)
        assertEquals 'one year before start date (year-start to prev-year-start): floor(-1.00)=-1', -1, DateTimeUtilities.mapDateToPeriod(date20090101, date20100101)
        assertEquals 'one year before start date (year end to start of same calendar year): floor(-0.99)=-1', -1, DateTimeUtilities.mapDateToPeriod(date20100101, date20101231)
        assertEquals 'one year before start date (month-start to prev-month-end): floor(-0.01)=-1', -1, DateTimeUtilities.mapDateToPeriod(date20080229, date20080301)
        assertEquals 'one year before start date (year-start to prev-year-end): floor(-0.01)=-1', -1, DateTimeUtilities.mapDateToPeriod(date20091231, date20100101)
        assertEquals 'dates in same year (year start to end): floor(0.99)=0', 0, DateTimeUtilities.mapDateToPeriod(date20101231, date20100101)
        // check that rounding doesn't have a boundary value at the half-year: +6mo -> period 0, but -6mo -> period -1
        assertEquals 'round under-half-year down', 0, DateTimeUtilities.mapDateToPeriod(date20090630, date20090101)
        assertEquals 'round exact-half-year down', 0, DateTimeUtilities.mapDateToPeriod(date20090701, date20090101)
        assertEquals 'round under-half-year less than zero down to a full year before start of first period', -1, DateTimeUtilities.mapDateToPeriod(date20090101, date20090630)
        assertEquals 'round exact-half-year less than zero down to a full year before start of first period', -1, DateTimeUtilities.mapDateToPeriod(date20090101, date20090701)

        /**
         *  Test periods beginning at the beginning of the second quarter (i.e., the day after a possible leap day).
         *  These tests are given in the method's JavaDoc.
         */

        DateTime startOfPrevPeriod = new DateTime(2009,3,1,0,0,0,0);
        DateTime startOfThisPeriod = new DateTime(2010,3,1,0,0,0,0);
        DateTime startOfNextPeriod = new DateTime(2011,3,1,0,0,0,0);
        DateTime lastPointInPrevPeriod = new DateTime(2010,2,28,23,59,59,999);
        DateTime lastPointInThisPeriod = new DateTime(2011,2,28,23,59,59,999);
        DateTime lastPointInNextPeriod = new DateTime(2012,2,29,23,59,59,999);

        assertEquals 'same period, from March 1',  0, DateTimeUtilities.mapDateToPeriod(startOfThisPeriod, startOfThisPeriod)
        assertEquals 'next period, from March 1',  1, DateTimeUtilities.mapDateToPeriod(startOfNextPeriod, startOfThisPeriod)
        assertEquals 'prev period, from March 1', -1, DateTimeUtilities.mapDateToPeriod(startOfPrevPeriod, startOfThisPeriod)
        assertEquals 'millisecond test: period 0 upper bound, from March 1 (one full period)',  0, DateTimeUtilities.mapDateToPeriod(lastPointInThisPeriod, startOfThisPeriod)
        assertEquals 'millisecond test: period 1 upper bound, from March 1',  1, DateTimeUtilities.mapDateToPeriod(lastPointInNextPeriod, startOfThisPeriod)
        assertEquals 'millisecond test: period -1 upper bound, from March 1', -1, DateTimeUtilities.mapDateToPeriod(lastPointInPrevPeriod, startOfThisPeriod)
    }

    void testMapDateToFractionOfPeriod() {
        assertEquals 'first of year = 0',                        0,                           DateTimeUtilities.mapDateToFractionOfPeriod(new DateTime(2010, 1, 1,0,0,0,0))
        assertEquals 'second of normal year = 1/365',       1d/365,                           DateTimeUtilities.mapDateToFractionOfPeriod(new DateTime(2010, 1, 2,0,0,0,0))
        assertEquals 'second of leap year = 1/366',         1d/366,                           DateTimeUtilities.mapDateToFractionOfPeriod(new DateTime(2012, 1, 2,0,0,0,0))
        assertEquals 'last day of normal year = 1-1/<365>',    365, 0.001*Math.round(1000/(1d-DateTimeUtilities.mapDateToFractionOfPeriod(new DateTime(2010,12,31,0,0,0,0))))
        assertEquals 'last day of leap year = 1-1/<366>',      366, 0.001*Math.round(1000/(1d-DateTimeUtilities.mapDateToFractionOfPeriod(new DateTime(2012,12,31,0,0,0,0))))
    }

    void testSimulationPeriodLength() {
        assertEquals "3 months", 3, DateTimeUtilities.simulationPeriodLength(date20100101, date20100401).getMonths()
        assertEquals "3 months", -3, DateTimeUtilities.simulationPeriodLength(date20100401, date20100101).getMonths()
        assertEquals "6 months", 6, DateTimeUtilities.simulationPeriodLength(date20100101, date20100701).getMonths()
        assertEquals "9 months", 9, DateTimeUtilities.simulationPeriodLength(date20100101, date20101001).getMonths()
        assertEquals "12 months -> 0 months", 0, DateTimeUtilities.simulationPeriodLength(date20100101, date20110101).getMonths()
        assertEquals "12 months -> 1 year", 1, DateTimeUtilities.simulationPeriodLength(date20100101, date20110101).getYears()
        assertEquals "13 months -> 1", 1, DateTimeUtilities.simulationPeriodLength(date20100101, date20110201).getMonths()
    }

    void testSimulationPeriod() {
        Period month1 = new Period(0, 1, 0, 0,0,0,0,0)
        Period month3 = new Period(0, 3, 0, 0,0,0,0,0)
        Period month6 = new Period(0, 6, 0, 0,0,0,0,0)
        Period month12 = new Period(0, 12, 0, 0,0,0,0,0)

        assertEquals "date20110201, -12/1", -12, DateTimeUtilities.simulationPeriod(date20120101, month1, date20110101)
        assertEquals "date20110201, -12/3", -4, DateTimeUtilities.simulationPeriod(date20120101, month3, date20110101)
        assertEquals "date20110201, -12/6", -2, DateTimeUtilities.simulationPeriod(date20120101, month6, date20110101)
        assertEquals "date20110201, -12/12", -1, DateTimeUtilities.simulationPeriod(date20120101, month12, date20110101)

        assertEquals "date20110201, -9/1", -9, DateTimeUtilities.simulationPeriod(date20120101, month1, date20110401)
        assertEquals "date20110201, -9/3", -3, DateTimeUtilities.simulationPeriod(date20120101, month3, date20110401)
        assertEquals "date20110201, -9/3", -2, DateTimeUtilities.simulationPeriod(date20120101, month3, date20110701)
        assertEquals "date20110201, -6/6", -1, DateTimeUtilities.simulationPeriod(date20120101, month6, date20110701)

        assertEquals "date2010201, date20110101, -4", -4, DateTimeUtilities.simulationPeriod(date20110101, month3, date20100201)
        assertEquals "date2010501, date20110101, -3", -3, DateTimeUtilities.simulationPeriod(date20110101, month3, date20100501)

        assertEquals "date20110101, date20110201, 0", 0, DateTimeUtilities.simulationPeriod(date20110101, month3, date20110201)
        assertEquals "date20110101, date20110401, 1", 1, DateTimeUtilities.simulationPeriod(date20110101, month3, date20110401)
    }

    void testGetDate() {
        assertEquals "-8/0.5 -> date20090516", date20090516, DateTimeUtilities.getDate(date20100101, date20100201, -8, 0.5)
        assertEquals "6/0.1 -> date20100704", date20100704, DateTimeUtilities.getDate(date20100101, date20100201, 6, 0.1)
        assertEquals "-3/0.75 -> date20091024", date20091024, DateTimeUtilities.getDate(date20100101, date20100201, -3, 0.75)
        assertEquals "-1/0.5 -> date20091115", date20091115, DateTimeUtilities.getDate(date20100101, date20100401, -1, 0.5)
        assertEquals "-2/0.95 -> date20081213", date20081213, DateTimeUtilities.getDate(date20100101, date20110101, -2, 0.95)
    }

    void testDateAsDouble() {
        assertEquals "start = simulation start", 0, DateTimeUtilities.dateAsDouble(date20100101, date20110101, date20100101)
        assertEquals "end = first period end", 1, DateTimeUtilities.dateAsDouble(date20100101, date20110101, date20110101)
        assertEquals "end = third period end", 3, DateTimeUtilities.dateAsDouble(date20100101, date20110101, date20130101)
        assertEquals "end = third period end", 90/365d, DateTimeUtilities.dateAsDouble(date20100101, date20110101, date20100401)
        assertEquals "end = third period end", (365+90)/365d, DateTimeUtilities.dateAsDouble(date20100101, date20110101, date20110401)
    }
}