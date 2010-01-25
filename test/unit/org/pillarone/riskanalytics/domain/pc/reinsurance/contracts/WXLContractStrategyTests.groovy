package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class WXLContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 20,
                         "limit": 30,
                         "aggregateLimit": 100,
                         "premiumBase": PremiumBase.ABSOLUTE,
                         "premium": 100,
                         "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                         "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContract1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 80,
                         "limit": 20,
                         "aggregateLimit": 50,
                         "premiumBase": PremiumBase.ABSOLUTE,
                         "premium": 100,
                         "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                         "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim50 = new Claim(claimType: ClaimType.SINGLE, ultimate: 50d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)
        Claim largeClaim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract wxl = getContract0()
        wxl.inClaims << attrClaim100 << largeClaim50 << largeClaim60 << largeClaim100 << largeClaim100

        wxl.doCalculation()

        assertEquals "outClaimsNet.size()", 0, wxl.outUncoveredClaims.size()
        assertEquals "outClaims.size()", 5, wxl.outCoveredClaims.size()
        assertEquals "wxl, attritional claim 100", 0, wxl.outCoveredClaims[0].ultimate
        assertEquals "wxl, ceded claim 50", 30, wxl.outCoveredClaims[1].ultimate
        assertEquals "wxl, ceded claim 60", 30, wxl.outCoveredClaims[2].ultimate
        assertEquals "wxl, ceded claim 100", 30, wxl.outCoveredClaims[3].ultimate
        assertEquals "wxl, ceded claim 100 (2)", 10, wxl.outCoveredClaims[4].ultimate  // smaller due to the aggregateLimit
    }

    void testAttritionalClaims() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)

        ReinsuranceContract wxl = getContract0()

        wxl.inClaims << attrClaim100

        def probeWXLnet = new TestProbe(wxl, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        wxl.doCalculation()

        assertTrue 1 == wxl.outCoveredClaims.size()
        assertTrue 0 == wxl.outCoveredClaims[0].ultimate
        assertEquals "attrClaim100", attrClaim100.ultimate, wxl.outUncoveredClaims[0].ultimate
    }

    void testLargeClaims() {
        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, ultimate: 0d)
        Claim claim80 = new Claim(claimType: ClaimType.SINGLE, ultimate: 80d)
        Claim claim90 = new Claim(claimType: ClaimType.SINGLE, ultimate: 90d)
        Claim claim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)
        Claim claim110 = new Claim(claimType: ClaimType.SINGLE, ultimate: 110d)
        Claim claim120 = new Claim(claimType: ClaimType.SINGLE, ultimate: 120d)

        ReinsuranceContract wxl = getContract1()

        wxl.inClaims << claim0 << claim80 << claim90 << claim100 << claim110 << claim120

        def probeWXLnet = new TestProbe(wxl, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        wxl.doCalculation()
        assertEquals("claim0", 0, wxl.outCoveredClaims[0].ultimate)
        assertEquals("claim80", 0, wxl.outCoveredClaims[1].ultimate)
        assertEquals("claim90", 10, wxl.outCoveredClaims[2].ultimate)
        assertEquals("claim100", 20, wxl.outCoveredClaims[3].ultimate)
        assertEquals("claim110", 20, wxl.outCoveredClaims[4].ultimate)
        assertEquals("claim120", 0, wxl.outCoveredClaims[5].ultimate)

        assertEquals("claim0 net", 0, wxl.outUncoveredClaims[0].ultimate)
        assertEquals("claim80 net", 80, wxl.outUncoveredClaims[1].ultimate)
        assertEquals("claim90 net", 80, wxl.outUncoveredClaims[2].ultimate)
        assertEquals("claim100 net", 80, wxl.outUncoveredClaims[3].ultimate)
        assertEquals("claim110 net", 90, wxl.outUncoveredClaims[4].ultimate)
        assertEquals("claim120 net", 120, wxl.outUncoveredClaims[5].ultimate)
    }

    void testGetCededUnderwritingInfoGNPI() {
        ReinsuranceContract wxl = getContract1()
        UnderwritingInfo grossUnderwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        wxl.parmContractStrategy.premiumBase = PremiumBase.GNPI
        UnderwritingInfo cededUnderwritingInfo = wxl.parmContractStrategy.calculateCoverUnderwritingInfo(grossUnderwritingInfo)

        assertEquals "premium written", wxl.parmContractStrategy.premium * grossUnderwritingInfo.premiumWritten, cededUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", wxl.parmContractStrategy.premium * grossUnderwritingInfo.premiumWrittenAsIf, cededUnderwritingInfo.premiumWrittenAsIf
    }

    void testGetCededUnderwritingInfoROE() {
        ReinsuranceContract wxl = getContract1()
        UnderwritingInfo grossUnderwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        wxl.parmContractStrategy.premiumBase = PremiumBase.RATE_ON_LINE
        UnderwritingInfo cededUnderwritingInfo = wxl.parmContractStrategy.calculateCoverUnderwritingInfo(grossUnderwritingInfo)

        assertEquals "premium written", wxl.parmContractStrategy.premium * wxl.parmContractStrategy.limit, cededUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", wxl.parmContractStrategy.premium * wxl.parmContractStrategy.limit, cededUnderwritingInfo.premiumWrittenAsIf
    }


    void testGetCededUnderwritingInfoIAE() {
        ReinsuranceContract wxl = getContract1()
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        wxl.parmContractStrategy.premiumBase = PremiumBase.NUMBER_OF_POLICIES
        shouldFail(IllegalArgumentException) {
            wxl.parmContractStrategy.calculateCoverUnderwritingInfo underwritingInfo
        }
    }

    void testReinstatementPremiumAllUsed() {
        ReinsuranceContract wxl = getContract0()
        wxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, value: 0d)
        Claim claim80 = new Claim(claimType: ClaimType.SINGLE, value: 80d)
        Claim claim90 = new Claim(claimType: ClaimType.SINGLE, value: 90d)
        Claim claim100 = new Claim(claimType: ClaimType.SINGLE, value: 100d)
        Claim claim110 = new Claim(claimType: ClaimType.SINGLE, value: 110d)
        Claim claim120 = new Claim(claimType: ClaimType.SINGLE, value: 120d)

        wxl.inClaims << claim0 << claim80 << claim90 << claim100 << claim110 << claim120

        def probeWXL = new TestProbe(wxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        wxl.doCalculation()

        double usedReinstatements = 7 / 3

        assertEquals "premium written", wxl.parmContractStrategy.premium * (1 + wxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                wxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }

    void testReinstatementPremiumNoneUsed() {
        ReinsuranceContract wxl = getContract0()
        wxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, value: 0d)
        Claim claim10 = new Claim(claimType: ClaimType.SINGLE, value: 10d)
        Claim claim20 = new Claim(claimType: ClaimType.SINGLE, value: 20d)
        Claim claim15 = new Claim(claimType: ClaimType.SINGLE, value: 15d)

        wxl.inClaims << claim0 << claim10 << claim15 << claim20

        def probeWXL = new TestProbe(wxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        wxl.doCalculation()

        double usedReinstatements = 0d

        assertEquals "premium written", wxl.parmContractStrategy.premium * (1 + wxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                wxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }

    void testReinstatementPremiumOneUsed() {
        ReinsuranceContract wxl = getContract0()
        wxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        Claim claim25 = new Claim(claimType: ClaimType.SINGLE, value: 25d)
        Claim claim30 = new Claim(claimType: ClaimType.SINGLE, value: 30d)
        Claim claim35 = new Claim(claimType: ClaimType.SINGLE, value: 35d)

        wxl.inClaims << claim25 << claim30 << claim35

        def probeWXL = new TestProbe(wxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        wxl.doCalculation()

        double usedReinstatements = 1d

        assertEquals "premium written", wxl.parmContractStrategy.premium * (1 + wxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                wxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }

    void testReinstatementPremiumOneAndAHalfUsed() {
        ReinsuranceContract wxl = getContract0()
        wxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        Claim claim35 = new Claim(claimType: ClaimType.SINGLE, value: 35d)
        Claim claim65 = new Claim(claimType: ClaimType.SINGLE, value: 65d)

        wxl.inClaims << claim65 << claim35

        def probeWXL = new TestProbe(wxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        wxl.doCalculation()

        double usedReinstatements = 1.5

        assertEquals "premium written", wxl.parmContractStrategy.premium * (1 + wxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                wxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }


}