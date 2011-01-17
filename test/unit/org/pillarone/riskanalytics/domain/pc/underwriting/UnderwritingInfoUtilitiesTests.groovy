package org.pillarone.riskanalytics.domain.pc.underwriting

/**
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class UnderwritingInfoUtilitiesTests extends GroovyTestCase {

  void testAggregate() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo doubledUnderwritingInfo = UnderwritingInfoUtilities.aggregate([underwritingInfo, underwritingInfo])

        assertEquals "premium written", 2 * underwritingInfo.premium,  doubledUnderwritingInfo.premium
        assertEquals "number of policies", 2 * underwritingInfo.numberOfPolicies,  doubledUnderwritingInfo.numberOfPolicies
    }

    void testDifference() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo zeroUnderwritingInfo = UnderwritingInfoUtilities.difference(underwritingInfo, underwritingInfo)

        assertEquals "premium written", 0,  zeroUnderwritingInfo.premium
        assertEquals "number of policies", 0,  zeroUnderwritingInfo.numberOfPolicies
    }

    void testDifferenceOfLists() {
        UnderwritingInfo underwritingInfoGross1 = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo underwritingInfoGross2 = UnderwritingInfoTests.getUnderwritingInfo()
        underwritingInfoGross2.premium = 2500
        UnderwritingInfo underwritingInfoCeded = UnderwritingInfoTests.getUnderwritingInfo()
        underwritingInfoCeded.premium = 1500
        List<UnderwritingInfo> netUnderwritingInfo = UnderwritingInfoUtilities.difference(
                [underwritingInfoGross1, underwritingInfoGross2],
                [underwritingInfoCeded, underwritingInfoCeded])

        assertEquals "premium written", 500,  netUnderwritingInfo[0].premium
        assertEquals "number of policies", 0,  netUnderwritingInfo[0].numberOfPolicies
        assertEquals "premium written", 1000,  netUnderwritingInfo[1].premium
        assertEquals "number of policies", 0,  netUnderwritingInfo[1].numberOfPolicies
    }
}