package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests

class UnderwritingInfoAggregatorTests extends GroovyTestCase {

    void testNoGrossUnderwritingInfoFound() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfoAggregator aggregator = new UnderwritingInfoAggregator()

        aggregator.inUnderwritingInfoCeded << underwritingInfo

        shouldFail(IllegalStateException, {
            aggregator.doCalculation()
        })
    }

    void testAggregation() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo underwritingInfoCeded = UnderwritingInfoTests.getUnderwritingInfo2()

        UnderwritingInfoAggregator aggregator = new UnderwritingInfoAggregator()

        aggregator.inUnderwritingInfoGross << underwritingInfo
        aggregator.inUnderwritingInfoCeded << underwritingInfoCeded

        // the probes are used for wiring the out channels
        def probeGross = new TestProbe(aggregator, "outUnderwritingInfoGross")
        def probeCeded = new TestProbe(aggregator, "outUnderwritingInfoCeded")
        def probeNet = new TestProbe(aggregator, "outUnderwritingInfoNet")

        aggregator.doCalculation()

        assertTrue "gross uw info not modified", UnderwritingInfoUtilities.sameContent(underwritingInfo, aggregator.outUnderwritingInfoGross[0])
        assertTrue "ceded uw info not modified", UnderwritingInfoUtilities.sameContent(underwritingInfoCeded, aggregator.outUnderwritingInfoCeded[0])
        assertEquals "net premium written", underwritingInfo.premiumWritten - underwritingInfoCeded.premiumWritten, aggregator.outUnderwritingInfoNet[0].premiumWritten
        assertEquals "net commission", underwritingInfo.commission - underwritingInfoCeded.commission, aggregator.outUnderwritingInfoNet[0].commission
        assertEquals "net premium written as if", underwritingInfo.premiumWrittenAsIf - underwritingInfoCeded.premiumWrittenAsIf, aggregator.outUnderwritingInfoNet[0].premiumWrittenAsIf
        assertEquals "net sum insured", underwritingInfo.sumInsured - underwritingInfoCeded.sumInsured, aggregator.outUnderwritingInfoNet[0].sumInsured
        assertEquals "net max sum insured", underwritingInfo.maxSumInsured - underwritingInfoCeded.maxSumInsured, aggregator.outUnderwritingInfoNet[0].maxSumInsured
        assertEquals "net number of policies", underwritingInfo.numberOfPolicies, aggregator.outUnderwritingInfoNet[0].numberOfPolicies
    }

    void testAggregationSeveral() {
        UnderwritingInfo underwritingInfo0 = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo underwritingInfo1 = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo underwritingInfoCeded0 = UnderwritingInfoTests.getUnderwritingInfo2()
        UnderwritingInfo underwritingInfoCeded1 = UnderwritingInfoTests.getUnderwritingInfo2()

        UnderwritingInfoAggregator aggregator = new UnderwritingInfoAggregator()

        aggregator.inUnderwritingInfoGross << underwritingInfo0 << underwritingInfo1
        aggregator.inUnderwritingInfoCeded << underwritingInfoCeded0 << underwritingInfoCeded1

        // the probes are used for wiring the out channels
        def probeGross = new TestProbe(aggregator, "outUnderwritingInfoGross")
        def probeCeded = new TestProbe(aggregator, "outUnderwritingInfoCeded")
        def probeNet = new TestProbe(aggregator, "outUnderwritingInfoNet")

        aggregator.doCalculation()

        assertTrue "# gross uw info", 1 == aggregator.outUnderwritingInfoGross.size()
        assertTrue "# ceded uw info", 1 == aggregator.outUnderwritingInfoCeded.size()
        assertTrue "# net uw info", 1 == aggregator.outUnderwritingInfoNet.size()

        double grossPremiumWritten = underwritingInfo0.premiumWritten + underwritingInfo1.premiumWritten
        double cededPremiumWritten = underwritingInfoCeded0.premiumWritten + underwritingInfoCeded1.premiumWritten

        assertEquals "gross premium written", grossPremiumWritten, aggregator.outUnderwritingInfoGross[0].premiumWritten
        assertEquals "ceded premium written", cededPremiumWritten, aggregator.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals "net premium written", grossPremiumWritten - cededPremiumWritten, aggregator.outUnderwritingInfoNet[0].premiumWritten
        assertEquals "net number of policies", underwritingInfo0.numberOfPolicies + underwritingInfo1.numberOfPolicies, aggregator.outUnderwritingInfoGross[0].numberOfPolicies
    }

    /*private boolean compareUnderwritingValues(UnderwritingInfo underwritingInfo1, UnderwritingInfo underwritingInfo2) {
        return underwritingInfo1.premiumWritten == underwritingInfo2.premiumWritten &&
                underwritingInfo1.commission == underwritingInfo2.commission &&
                underwritingInfo1.premiumWrittenAsIf == underwritingInfo2.premiumWrittenAsIf &&
                underwritingInfo1.sumInsured == underwritingInfo2.sumInsured &&
                underwritingInfo1.maxSumInsured == underwritingInfo2.maxSumInsured
    }*/
}