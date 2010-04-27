package org.pillarone.riskanalytics.domain.pc.underwriting

/**
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class UnderwritingInfoUtilitiesTests extends GroovyTestCase {

  void testAggregate() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo doubledUnderwritingInfo = UnderwritingInfoUtilities.aggregate([underwritingInfo, underwritingInfo])

        assertEquals "premium written", 2 * underwritingInfo.premiumWritten,  doubledUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", 2 * underwritingInfo.premiumWrittenAsIf,  doubledUnderwritingInfo.premiumWrittenAsIf
        assertEquals "number of policies", 2 * underwritingInfo.numberOfPolicies,  doubledUnderwritingInfo.numberOfPolicies
    }

    void testDifference() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo zeroUnderwritingInfo = UnderwritingInfoUtilities.difference(underwritingInfo, underwritingInfo)

        assertEquals "premium written", 0,  zeroUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", 0,  zeroUnderwritingInfo.premiumWrittenAsIf
        assertEquals "number of policies", 0,  zeroUnderwritingInfo.numberOfPolicies
    }

    void testDifferenceOfLists() {
        UnderwritingInfo underwritingInfoGross1 = UnderwritingInfoTests.getUnderwritingInfo()
        UnderwritingInfo underwritingInfoGross2 = UnderwritingInfoTests.getUnderwritingInfo()
        underwritingInfoGross2.premiumWritten = 2500
        UnderwritingInfo underwritingInfoCeded = UnderwritingInfoTests.getUnderwritingInfo()
        underwritingInfoCeded.premiumWritten = 1500
        List<UnderwritingInfo> netUnderwritingInfo = UnderwritingInfoUtilities.difference(
                [underwritingInfoGross1, underwritingInfoGross2],
                [underwritingInfoCeded, underwritingInfoCeded])

        assertEquals "premium written", 500,  netUnderwritingInfo[0].premiumWritten
        assertEquals "premium written as if", 0,  netUnderwritingInfo[0].premiumWrittenAsIf
        assertEquals "number of policies", 100,  netUnderwritingInfo[0].numberOfPolicies
        assertEquals "premium written", 1000,  netUnderwritingInfo[1].premiumWritten
        assertEquals "premium written as if", 0,  netUnderwritingInfo[1].premiumWrittenAsIf
        assertEquals "number of policies", 100,  netUnderwritingInfo[1].numberOfPolicies
    }
}