package org.pillarone.riskanalytics.domain.pc.aggregators


import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

class UnderwritingInfoMergerTests extends GroovyTestCase {

    void testNoGrossUnderwritingInfoFound() {
        CededUnderwritingInfo underwritingInfo = UnderwritingInfoTests.getCededUnderwritingInfo()
        UnderwritingInfoAggregator aggregator = new UnderwritingInfoAggregator()

        aggregator.inUnderwritingInfoCeded << underwritingInfo

        shouldFail(IllegalStateException, {
            aggregator.doCalculation()
        })
    }

    void testMerge() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        CededUnderwritingInfo underwritingInfoCeded = UnderwritingInfoTests.getCededUnderwritingInfo2()
        underwritingInfoCeded.originalUnderwritingInfo = underwritingInfo
        underwritingInfo.originalUnderwritingInfo = underwritingInfo

        UnderwritingInfoMerger merger = new UnderwritingInfoMerger()

        merger.inUnderwritingInfoGross << underwritingInfo
        merger.inUnderwritingInfoCeded << underwritingInfoCeded

        // the probes are used for wiring the out channels
        def probeGross = new TestProbe(merger, "outUnderwritingInfoGross")
        def probeCeded = new TestProbe(merger, "outUnderwritingInfoCeded")
        def probeNet = new TestProbe(merger, "outUnderwritingInfoNet")

        merger.doCalculation()

        assertTrue "gross uw info not modified", UnderwritingInfoUtilities.sameContent(underwritingInfo, merger.outUnderwritingInfoGross[0])
        assertTrue "ceded uw info not modified", UnderwritingInfoUtilities.sameContent(underwritingInfoCeded, merger.outUnderwritingInfoCeded[0])
        assertEquals "net premium written", underwritingInfo.premium - underwritingInfoCeded.premium, merger.outUnderwritingInfoNet[0].premium
        assertEquals "net commission", underwritingInfo.commission - underwritingInfoCeded.commission, merger.outUnderwritingInfoNet[0].commission
        assertEquals "net sum insured", underwritingInfo.sumInsured - underwritingInfoCeded.sumInsured, merger.outUnderwritingInfoNet[0].sumInsured
        assertEquals "net max sum insured", underwritingInfo.maxSumInsured - underwritingInfoCeded.maxSumInsured, merger.outUnderwritingInfoNet[0].maxSumInsured
        assertEquals "net number of policies", underwritingInfo.numberOfPolicies, merger.outUnderwritingInfoNet[0].numberOfPolicies
    }

    void testMergeSeveral() {
        UnderwritingInfo underwritingInfo0 = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo underwritingInfo1 = underwritingInfo0.copy()
        CededUnderwritingInfo underwritingInfoCeded0 = UnderwritingInfoTests.getCededUnderwritingInfo2()
        CededUnderwritingInfo underwritingInfoCeded1 = UnderwritingInfoTests.getCededUnderwritingInfo2()
        underwritingInfoCeded0.originalUnderwritingInfo = underwritingInfo0.originalUnderwritingInfo = underwritingInfo0
        underwritingInfoCeded1.originalUnderwritingInfo = underwritingInfo1.originalUnderwritingInfo = underwritingInfo1

        UnderwritingInfoMerger merger = new UnderwritingInfoMerger()

        merger.inUnderwritingInfoGross << underwritingInfo0 << underwritingInfo1
        merger.inUnderwritingInfoCeded << underwritingInfoCeded0 << underwritingInfoCeded1

        // the probes are used for wiring the out channels
        def probeGross = new TestProbe(merger, "outUnderwritingInfoGross")
        def probeCeded = new TestProbe(merger, "outUnderwritingInfoCeded")
        def probeNet = new TestProbe(merger, "outUnderwritingInfoNet")

        merger.doCalculation()

        assertTrue "# gross uw info", 2 == merger.outUnderwritingInfoGross.size()
        assertTrue "# ceded uw info", 2 == merger.outUnderwritingInfoCeded.size()
        assertTrue "# net uw info", 2 == merger.outUnderwritingInfoNet.size()

        assertTrue "gross premium written", UnderwritingInfoUtilities.sameContent(underwritingInfo0, merger.outUnderwritingInfoGross[0])
        assertTrue "ceded premium written", CededUnderwritingInfoUtilities.sameContent(underwritingInfoCeded0, merger.outUnderwritingInfoCeded[0])
        assertEquals "net premium written", underwritingInfo0.premium - underwritingInfoCeded0.premium, merger.outUnderwritingInfoNet[0].premium
        assertEquals "net number of policies", underwritingInfo0.numberOfPolicies, merger.outUnderwritingInfoNet[0].numberOfPolicies
    }
}

class TestUnderwritingInfoContainer extends Component {
    PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded = new PacketList(CededUnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList(UnderwritingInfo)

    public void doCalculation() {
    }
}