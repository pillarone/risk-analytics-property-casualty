package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoUtilities

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class WXLContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 20,
                                "limit": 30,
                                "aggregateLimit": 100,
                                "aggregateDeductible": 0,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContract1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 80,
                                "limit": 20,
                                "aggregateLimit": 50,
                                "aggregateDeductible": 0,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContract2() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 20,
                                "limit": 30,
                                "aggregateLimit": 100,
                                "aggregateDeductible": 50,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.3], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]))
    }

    void testCalculateCededClaimsOnly() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim50 = new Claim(claimType: ClaimType.SINGLE, ultimate: 50d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)
        Claim largeClaim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract wxl = getContract0()
        wxl.inClaims << attrClaim100 << largeClaim50 << largeClaim60 << largeClaim100 << largeClaim100
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 100)
        def probeCXLCededUwInfo = new TestProbe(wxl, "outCoverUnderwritingInfo")
        wxl.doCalculation()

        assertEquals "outClaimsNet.size()", 0, wxl.outUncoveredClaims.size()
        assertEquals "outClaims.size()", 5, wxl.outCoveredClaims.size()
        assertEquals "wxl, attritional claim 100", 0, wxl.outCoveredClaims[0].ultimate
        assertEquals "wxl, ceded claim 50", 30, wxl.outCoveredClaims[1].ultimate
        assertEquals "wxl, ceded claim 60", 30, wxl.outCoveredClaims[2].ultimate
        assertEquals "wxl, ceded claim 100", 30, wxl.outCoveredClaims[3].ultimate
        assertEquals "wxl, ceded claim 100 (2)", 10, wxl.outCoveredClaims[4].ultimate  // smaller due to the aggregateLimit
        assertEquals "wxl, underwritingInfo", 100, wxl.outCoverUnderwritingInfo[0].fixedPremium
        assertEquals "wxl, underwritingInfo", 100 * 7 / 15d, wxl.outCoverUnderwritingInfo[0].variablePremium, 1E-14
        assertEquals "wxl, underwriting info", 100 + 100 * 7 / 15d, wxl.outCoverUnderwritingInfo[0].premium, 1E-13
    }

    void testCalculateCededClaimsAggregateDeductible() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim50 = new Claim(claimType: ClaimType.SINGLE, ultimate: 50d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)
        Claim largeClaim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)

        ReinsuranceContract wxl = getContract2()
        wxl.inClaims << attrClaim100 << largeClaim50 << largeClaim60 << largeClaim100 << largeClaim100
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 100)
        wxl.inUnderwritingInfo << underwritingInfo

        // needed in order to trigger the calculation of ceded uw info
        def probeCXLCededUwInfo = new TestProbe(wxl, "outCoverUnderwritingInfo")
        def probeCXLNetUwInfo = new TestProbe(wxl, "outNetAfterCoverUnderwritingInfo")


        wxl.doCalculation()

        CededUnderwritingInfo aggregateCeded = CededUnderwritingInfoUtilities.aggregate(wxl.outCoverUnderwritingInfo);

        assertEquals "outClaimsNet.size()", 0, wxl.outUncoveredClaims.size()
        assertEquals "outClaims.size()", 5, wxl.outCoveredClaims.size()
        assertEquals "wxl, attritional claim 100", 0, wxl.outCoveredClaims[0].ultimate
        assertEquals "wxl, ceded claim 50", 15, wxl.outCoveredClaims[1].ultimate
        assertEquals "wxl, ceded claim 60", 15, wxl.outCoveredClaims[2].ultimate
        assertEquals "wxl, ceded claim 100", 15, wxl.outCoveredClaims[3].ultimate
        assertEquals "wxl, ceded claim 100 (2)", 5, wxl.outCoveredClaims[4].ultimate
        assertEquals "underwriting info", 150, wxl.outCoverUnderwritingInfo[0].premium
        assertEquals "underwriting info", 100, wxl.outCoverUnderwritingInfo[0].fixedPremium
        assertEquals "underwriting info", 50, wxl.outCoverUnderwritingInfo[0].variablePremium
        assertEquals "underwriting info", -50, wxl.outNetAfterCoverUnderwritingInfo[0].premium

        wxl.reset()
        // apply the same calculations twice to make sure everything is properly reset between periods/iterations
        wxl.inClaims << attrClaim100 << largeClaim50 << largeClaim60 << largeClaim100 << largeClaim100
        wxl.inUnderwritingInfo << underwritingInfo
        wxl.doCalculation()

        assertEquals "outClaimsNet.size()", 0, wxl.outUncoveredClaims.size()
        assertEquals "outClaims.size()", 5, wxl.outCoveredClaims.size()
        assertEquals "wxl, attritional claim 100", 0, wxl.outCoveredClaims[0].ultimate
        assertEquals "wxl, ceded claim 50", 15, wxl.outCoveredClaims[1].ultimate
        assertEquals "wxl, ceded claim 60", 15, wxl.outCoveredClaims[2].ultimate
        assertEquals "wxl, ceded claim 100", 15, wxl.outCoveredClaims[3].ultimate
        assertEquals "wxl, ceded claim 100 (2)", 5, wxl.outCoveredClaims[4].ultimate
        assertEquals "underwriting info", 150, wxl.outCoverUnderwritingInfo[0].premium
        assertEquals "underwriting info", 100, wxl.outCoverUnderwritingInfo[0].fixedPremium
        assertEquals "underwriting info", 50, wxl.outCoverUnderwritingInfo[0].variablePremium
        assertEquals "underwriting info", -50, wxl.outNetAfterCoverUnderwritingInfo[0].premium
    }

    void testAttritionalClaims() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)

        ReinsuranceContract wxl = getContract0()

        wxl.inClaims << attrClaim100
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 200);

        def probeWXLnet = new TestProbe(wxl, "outUncoveredClaims")
        def probeUI = new TestProbe(wxl, "outCoverUnderwritingInfo")  // needed in order to trigger the calculation of net claims

        wxl.doCalculation()

        assertTrue 1 == wxl.outCoveredClaims.size()
        assertTrue 0 == wxl.outCoveredClaims[0].ultimate
        assertEquals "attrClaim100", attrClaim100.ultimate, wxl.outUncoveredClaims[0].ultimate
        assertEquals "underwriting info", 100, wxl.outCoverUnderwritingInfo[0].premium
        assertEquals "underwriting info", 100, wxl.outCoverUnderwritingInfo[0].fixedPremium
        assertEquals "underwriting info", 0, wxl.outCoverUnderwritingInfo[0].variablePremium
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
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 100)
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 200)

        def probeWXLnet = new TestProbe(wxl, "outUncoveredClaims")
        def probeUI = new TestProbe(wxl, "outCoverUnderwritingInfo")  // needed in order to trigger the calculation of net claims

        wxl.doCalculation()
        assertEquals("claim0", 0, wxl.outCoveredClaims[0].ultimate)
        assertEquals("claim80", 0, wxl.outCoveredClaims[1].ultimate)
        assertEquals("claim90", 10, wxl.outCoveredClaims[2].ultimate)
        assertEquals("claim100", 20, wxl.outCoveredClaims[3].ultimate)
        assertEquals("claim110", 20, wxl.outCoveredClaims[4].ultimate)
        assertEquals("claim120", 0, wxl.outCoveredClaims[5].ultimate)
        assertEquals "under info", 100 / 3d, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1E-14
        assertEquals "underwriting info", 100 / 3d * 0.3, wxl.outCoverUnderwritingInfo[0].variablePremium, 1E-14
        assertEquals "under info", 200 / 3d, wxl.outCoverUnderwritingInfo[1].fixedPremium, 1E-8
        assertEquals "underwriting info", 200 / 3d * 0.3, wxl.outCoverUnderwritingInfo[1].variablePremium, 1E-8
        assertEquals "under info", 100 / 3d * 1.3, wxl.outCoverUnderwritingInfo[0].premium, 1E-8
        assertEquals "underwriting info", 200 / 3d * 1.3, wxl.outCoverUnderwritingInfo[1].premium, 1E-8

        assertEquals("claim0 net", 0, wxl.outUncoveredClaims[0].ultimate)
        assertEquals("claim80 net", 80, wxl.outUncoveredClaims[1].ultimate)
        assertEquals("claim90 net", 80, wxl.outUncoveredClaims[2].ultimate)
        assertEquals("claim100 net", 80, wxl.outUncoveredClaims[3].ultimate)
        assertEquals("claim110 net", 90, wxl.outUncoveredClaims[4].ultimate)
        assertEquals("claim120 net", 120, wxl.outUncoveredClaims[5].ultimate)
    }

    void testCoveredByReinsurer() {
        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, ultimate: 0d)
        Claim claim80 = new Claim(claimType: ClaimType.SINGLE, ultimate: 80d)
        Claim claim90 = new Claim(claimType: ClaimType.SINGLE, ultimate: 90d)
        Claim claim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)
        Claim claim110 = new Claim(claimType: ClaimType.SINGLE, ultimate: 110d)
        Claim claim120 = new Claim(claimType: ClaimType.SINGLE, ultimate: 120d)

        ReinsuranceContract wxl = getContract1()
        wxl.parmContractStrategy.coveredByReinsurer = 0.8

        wxl.inClaims << claim0 << claim80 << claim90 << claim100 << claim110 << claim120
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 100)
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 200)

        def probeWXLnet = new TestProbe(wxl, "outUncoveredClaims")
        def probeUI = new TestProbe(wxl, "outCoverUnderwritingInfo")  // needed in order to trigger the calculation of net claims

        wxl.doCalculation()

        assertEquals("claim0", 0, wxl.outCoveredClaims[0].ultimate)
        assertEquals("claim80", 0, wxl.outCoveredClaims[1].ultimate)
        assertEquals("claim90", 0.8 * 10, wxl.outCoveredClaims[2].ultimate)
        assertEquals("claim100",0.8 * 20, wxl.outCoveredClaims[3].ultimate)
        assertEquals("claim110",0.8 * 20, wxl.outCoveredClaims[4].ultimate)
        assertEquals("claim120", 0, wxl.outCoveredClaims[5].ultimate)
        assertEquals "under info",0.8 * 100 / 3d, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1E-14
        assertEquals "underwriting info",0.8 * 100 / 3d * 0.3, wxl.outCoverUnderwritingInfo[0].variablePremium, 1E-14
        assertEquals "under info",0.8 * 200 / 3d, wxl.outCoverUnderwritingInfo[1].fixedPremium, 1E-8
        assertEquals "underwriting info",0.8 * 200 / 3d * 0.3, wxl.outCoverUnderwritingInfo[1].variablePremium, 1E-8
        assertEquals "under info",0.8 * 100 / 3d * 1.3, wxl.outCoverUnderwritingInfo[0].premium, 1E-8
        assertEquals "underwriting info",0.8 * 200 / 3d * 1.3, wxl.outCoverUnderwritingInfo[1].premium, 1E-8
    }


    void testGetUnderwritingInfoABSOLUTE() {
        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, ultimate: 0d)
        Claim claim80 = new Claim(claimType: ClaimType.SINGLE, ultimate: 80d)
        Claim claim90 = new Claim(claimType: ClaimType.SINGLE, ultimate: 90d)
        Claim claim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)
        Claim claim110 = new Claim(claimType: ClaimType.SINGLE, ultimate: 110d)
        Claim claim120 = new Claim(claimType: ClaimType.SINGLE, ultimate: 120d)

        ReinsuranceContract wxl = getContract1()

        wxl.inClaims << claim0 << claim80 << claim90 << claim100 << claim110 << claim120
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 100)
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 200)

        def probeUI = new TestProbe(wxl, "outCoverUnderwritingInfo")

        wxl.doCalculation()
        assertEquals "under info", 100 / 3d, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1E-14
        assertEquals "underwriting info", 100 / 3d * 0.3, wxl.outCoverUnderwritingInfo[0].variablePremium, 1E-14
        assertEquals "under info", 200 / 3d, wxl.outCoverUnderwritingInfo[1].fixedPremium, 1E-8
        assertEquals "underwriting info", 200 / 3d * 0.3, wxl.outCoverUnderwritingInfo[1].variablePremium, 1E-8
        assertEquals "under info", 100 / 3d * 1.3, wxl.outCoverUnderwritingInfo[0].premium, 1E-8
        assertEquals "underwriting info", 200 / 3d * 1.3, wxl.outCoverUnderwritingInfo[1].premium, 1E-8
    }

    void testGetCededUnderwritingInfoGNPI() {

        ReinsuranceContract wxl = getContract1()
        wxl.parmContractStrategy.premiumBase = PremiumBase.GNPI
        wxl.parmContractStrategy.premium = 0.1
        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, ultimate: 0d)
        Claim claim80 = new Claim(claimType: ClaimType.SINGLE, ultimate: 80d)
        Claim claim90 = new Claim(claimType: ClaimType.SINGLE, ultimate: 90d)
        Claim claim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)
        Claim claim110 = new Claim(claimType: ClaimType.SINGLE, ultimate: 110d)
        Claim claim120 = new Claim(claimType: ClaimType.SINGLE, ultimate: 120d)

        wxl.inClaims << claim0 << claim80 << claim90 << claim100 << claim110 << claim120
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 100)
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 200)

        def probeUI = new TestProbe(wxl, "outCoverUnderwritingInfo")

        wxl.doCalculation()
        assertEquals "under info", 0.1 * 300 / 3d, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1E-14
        assertEquals "underwriting info", 0.1 * 300 / 3d * 0.3, wxl.outCoverUnderwritingInfo[0].variablePremium, 1E-14
        assertEquals "under info", 0.1 * 300 * 2 / 3d, wxl.outCoverUnderwritingInfo[1].fixedPremium, 1E-8
        assertEquals "underwriting info", 0.1 * 300 * 2 / 3d * 0.3, wxl.outCoverUnderwritingInfo[1].variablePremium, 1E-8
        assertEquals "under info", 0.1 * 300 / 3d * 1.3, wxl.outCoverUnderwritingInfo[0].premium, 1E-8
        assertEquals "underwriting info", 0.1 * 300 * 2 / 3d * 1.3, wxl.outCoverUnderwritingInfo[1].premium, 1E-8
    }

    void testGetCededUnderwritingInfoRATEONLINE() {
        ReinsuranceContract wxl = getContract1()
        wxl.parmContractStrategy.premiumBase = PremiumBase.RATE_ON_LINE
        wxl.parmContractStrategy.premium = 0.2

        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, ultimate: 0d)
        Claim claim80 = new Claim(claimType: ClaimType.SINGLE, ultimate: 80d)
        Claim claim90 = new Claim(claimType: ClaimType.SINGLE, ultimate: 90d)
        Claim claim100 = new Claim(claimType: ClaimType.SINGLE, ultimate: 100d)
        Claim claim110 = new Claim(claimType: ClaimType.SINGLE, ultimate: 110d)
        Claim claim120 = new Claim(claimType: ClaimType.SINGLE, ultimate: 120d)

        wxl.inClaims << claim0 << claim80 << claim90 << claim100 << claim110 << claim120
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 100)
        wxl.inUnderwritingInfo << new UnderwritingInfo(premium: 200)

        def probeUI = new TestProbe(wxl, "outCoverUnderwritingInfo")

        wxl.doCalculation()
        assertEquals "under info", 0.2 * 20 / 3d, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1E-14
        assertEquals "underwriting info", 0.2 * 20 / 3d * 0.3, wxl.outCoverUnderwritingInfo[0].variablePremium, 1E-14
        assertEquals "under info", 0.2 * 20 * 2 / 3d, wxl.outCoverUnderwritingInfo[1].fixedPremium, 1E-8
        assertEquals "underwriting info", 0.2 * 20 * 2 / 3d * 0.3, wxl.outCoverUnderwritingInfo[1].variablePremium, 1E-8
        assertEquals "under info", 0.2 * 20 / 3d * 1.3, wxl.outCoverUnderwritingInfo[0].premium, 1E-8
        assertEquals "underwriting info", 0.2 * 20 * 2 / 3d * 1.3, wxl.outCoverUnderwritingInfo[1].premium, 1E-8

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
                wxl.outCoverUnderwritingInfo[0].premium, 1e-6
        assertEquals "premium written", wxl.parmContractStrategy.premium,
                wxl.outCoverUnderwritingInfo[0].fixedPremium, 1e-6
        assertEquals "premium written", wxl.parmContractStrategy.premium * wxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements,
                wxl.outCoverUnderwritingInfo[0].variablePremium, 1e-6
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
                wxl.outCoverUnderwritingInfo[0].premium, 1e-6
        assertEquals "premium written", wxl.parmContractStrategy.premium,
                wxl.outCoverUnderwritingInfo[0].fixedPremium, 1e-6
        assertEquals "premium written", 0d,
                wxl.outCoverUnderwritingInfo[0].variablePremium, 1e-6
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
                wxl.outCoverUnderwritingInfo[0].premium, 1e-6
        assertEquals "premium written", wxl.parmContractStrategy.premium, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1e-6
        assertEquals "premium written", wxl.parmContractStrategy.premium * wxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements,
                wxl.outCoverUnderwritingInfo[0].variablePremium, 1e-6
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
                wxl.outCoverUnderwritingInfo[0].premium, 1e-6
        assertEquals "premium written", wxl.parmContractStrategy.premium, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1e-6
        assertEquals "premium written", wxl.parmContractStrategy.premium * wxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements,
                wxl.outCoverUnderwritingInfo[0].variablePremium, 1e-6
    }

    void testZeroCoverPremiumOnly() {
        ReinsuranceContract wxl = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.WXL,
                        ["attachmentPoint": 0,
                                "limit": 0,
                                "aggregateLimit": 0,
                                "aggregateDeductible": 0,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.3], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]))

        wxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        Claim claim35 = new Claim(claimType: ClaimType.SINGLE, value: 35d)
        Claim claim65 = new Claim(claimType: ClaimType.SINGLE, value: 65d)

        wxl.inClaims << claim65 << claim35

        def probeWXL = new TestProbe(wxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        wxl.doCalculation()
        assertEquals "# ceded claims", 2, wxl.outCoveredClaims.size()
        assertEquals "claim35 ceded", 0, wxl.outCoveredClaims[0].ultimate
        assertEquals "claim65 ceded", 0, wxl.outCoveredClaims[1].ultimate
        assertEquals "premium written", 100, wxl.outCoverUnderwritingInfo[0].premium
        assertEquals "premium written", 100, wxl.outCoverUnderwritingInfo[0].fixedPremium, 1e-6
        assertEquals "premium written", 0, wxl.outCoverUnderwritingInfo[0].variablePremium, 1e-6
    }
}