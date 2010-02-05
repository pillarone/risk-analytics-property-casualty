package org.pillarone.riskanalytics.domain.pc.claims

import org.joda.time.DateTime
import org.joda.time.Period

import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.MultiLinesPerilsReinsuranceContractTests
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket

/**
 * ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class HistoricalClaimsTests extends GroovyTestCase {

    void testUsage() {
        HistoricalClaims historicalClaims = new HistoricalClaims()
        historicalClaims.parmEventLosses = new TableMultiDimensionalParameter([
                // only the first three claims above should pass through the filter
                // (incurred & paid dates both strictly before the start of simulation):
                ['2008-12-31', '2009-01-01', '2009-12-31', '2009-01-01', '2009-12-17', '2009-12-31', '2010-01-01', ], // dates incurred
                ['2009-01-15', '2009-12-31', '2009-12-31', '2010-01-01', '2010-01-01', '2010-01-15', '2010-01-16', ], // dates paid
                [       1000d,        2000d,        4000d,        8000d,       16000d,       32000d,       64000d, ], // amounts incurred
                [        100d,         200d,         400d,         800d,        1600d,        3200d,        6400d, ], // amounts paid
            ],["Date Incurred", "Date Paid", "Amount Incurred", "Amount Paid"])
        historicalClaims.iterationScope = MultiLinesPerilsReinsuranceContractTests.getIterationScope(new DateTime(2010,1,1,0,0,0,0), Period.years(2))

        historicalClaims.doCalculation()

        assertEquals '3 claims in 1st period and 1st iteration', 3, historicalClaims.outClaims.size()

        ClaimDevelopmentPacket claim;
        double fractionOfPeriod;

        claim = (ClaimDevelopmentPacket) historicalClaims.outClaims[0];
        fractionOfPeriod = claim.getFractionOfPeriod()
        assertEquals 'claim 1 ultimate = 1K', 1000, claim.ultimate
        assertEquals 'claim 1 paid = 10%', 10, 100d*claim.paid/claim.ultimate
        assertEquals 'claim 1 original period = -2', -2, claim.getOriginalPeriod()
        assertEquals 'claim 1 fraction of period = 1-1/<366>', 366, 0.001d*Math.round(1000d/(1-fractionOfPeriod))
        assertEquals 'claim 1 fraction of period same as in its event', fractionOfPeriod, claim.event.date

        claim = (ClaimDevelopmentPacket) historicalClaims.outClaims[1];
        fractionOfPeriod = claim.getFractionOfPeriod()
        assertEquals 'claim 2 ultimate = 1K', 2000, claim.ultimate
        assertEquals 'claim 2 paid = 10%', 10, 100d*claim.paid/claim.ultimate
        assertEquals 'claim 2 original period = -1', -1, claim.getOriginalPeriod()
        assertEquals 'claim 2 fraction of period = 0', 0, fractionOfPeriod
        assertEquals 'claim 2 fraction of period same as in its event', fractionOfPeriod, claim.event.date

        claim = (ClaimDevelopmentPacket) historicalClaims.outClaims[2];
        fractionOfPeriod = claim.getFractionOfPeriod()
        assertEquals 'claim 3 ultimate = 1K', 4000, claim.ultimate
        assertEquals 'claim 3 paid = 10%', 10, 100d*claim.paid/claim.ultimate
        assertEquals 'claim 3 original period = -1', -1, claim.getOriginalPeriod()
        assertEquals 'claim 3 fraction of period = 1-1/<365>', 365, 0.001d*Math.round(1000d/(1-claim.getFractionOfPeriod()))
        assertEquals 'claim 3 fraction of period same as in its event', fractionOfPeriod, claim.event.date
    }

}
