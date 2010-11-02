package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CXLContractStrategyTests extends GroovyTestCase {

    Event event0 = new Event(fractionOfPeriod: 0d)
    Event event1 = new Event(fractionOfPeriod: 0.1d)
    Event event2 = new Event(fractionOfPeriod: 0.2d)
    Event event3 = new Event(fractionOfPeriod: 0.3d)
    Event event4 = new Event(fractionOfPeriod: 0.4d)

    Claim claim0Event0 = new Claim(event: event0, claimType: ClaimType.EVENT, value: 0d)
    Claim claim10Event0 = new Claim(event: event0, claimType: ClaimType.EVENT, value: 10d)
    Claim claim20Event0 = new Claim(event: event0, claimType: ClaimType.EVENT, value: 20d)
    Claim claim30Event0 = new Claim(event: event0, claimType: ClaimType.EVENT, value: 30d)
    Claim claim10Event1 = new Claim(event: event1, claimType: ClaimType.EVENT, value: 10d)
    Claim claim15Event1 = new Claim(event: event1, claimType: ClaimType.EVENT, value: 15d)
    Claim claim35Event1 = new Claim(event: event1, claimType: ClaimType.EVENT, value: 35d)
    Claim claim30Event2 = new Claim(event: event2, claimType: ClaimType.EVENT, value: 30d)
    Claim claim60Event2 = new Claim(event: event2, claimType: ClaimType.EVENT, value: 60d)
    Claim claim40Event3 = new Claim(event: event3, claimType: ClaimType.EVENT, value: 40d)
    Claim claim60Event3 = new Claim(event: event3, claimType: ClaimType.EVENT, value: 60d)
    Claim claim120Event4 = new Claim(event: event4, claimType: ClaimType.EVENT, value: 120d)

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.CXL,
                        ["attachmentPoint": 20,
                                "limit": 30,
                                "aggregateLimit": 100,
                                "aggregateDeductible": 0,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]))
    }

    static ReinsuranceContract getContract1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.CXL,
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
                        ReinsuranceContractType.CXL,
                        ["attachmentPoint": 20,
                                "limit": 30,
                                "aggregateLimit": 100,
                                "aggregateDeductible": 20,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premium": 100,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([0.2], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]))
    }

    void testAttritionalClaims() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)

        ReinsuranceContract cxl = getContract0()

        cxl.inClaims << attrClaim100

        def probeCXLnet = new TestProbe(cxl, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        cxl.doCalculation()

        assertTrue 1 == cxl.outCoveredClaims.size()
        assertTrue 0 == cxl.outCoveredClaims[0].ultimate
        assertEquals "attrClaim100", attrClaim100.ultimate, cxl.outUncoveredClaims[0].ultimate
    }

    void testLargeClaims() {
        Claim claim0 = new Claim(claimType: ClaimType.SINGLE, value: 0d)
        Claim claim80 = new Claim(claimType: ClaimType.SINGLE, value: 80d)
        Claim claim90 = new Claim(claimType: ClaimType.SINGLE, value: 90d)

        ReinsuranceContract cxl = getContract0()

        cxl.inClaims << claim0 << claim80 << claim90

        def probeCXLnet = new TestProbe(cxl, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        cxl.doCalculation()
        assertTrue "# ceded claims", 3 == cxl.outCoveredClaims.size()
        assertTrue 0 == cxl.outCoveredClaims[0].ultimate
        assertTrue 0 == cxl.outCoveredClaims[1].ultimate
        assertTrue 0 == cxl.outCoveredClaims[2].ultimate
        assertEquals "claim0", claim0.ultimate, cxl.outUncoveredClaims[0].ultimate
        assertEquals "claim80", claim80.ultimate, cxl.outUncoveredClaims[1].ultimate
        assertEquals "claim90", claim90.ultimate, cxl.outUncoveredClaims[2].ultimate
    }

    void testEventClaims() {
        ReinsuranceContract cxl = getContract0()

        cxl.inClaims << claim0Event0 << claim10Event0 << claim20Event0 << claim30Event0 << claim10Event1
        cxl.inClaims << claim15Event1 << claim35Event1 << claim30Event2 << claim60Event2 << claim40Event3
        cxl.inClaims << claim60Event3 << claim120Event4

        def probeCXLnet = new TestProbe(cxl, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        cxl.doCalculation()
        assertTrue "number of ceded claims", 12 == cxl.outCoveredClaims.size()  // claim0Event0 and claim120Event4 don't have a ceded part
        // [id -1] as the first claim won't have any ceded part
        assertEquals("claim10Event0", claim10Event0.ultimate * 0.5, cxl.outCoveredClaims[1].ultimate)
        assertEquals("claim10Event1", claim10Event1.ultimate * 0.5, cxl.outCoveredClaims[4].ultimate)
        assertEquals("claim30Event2", claim30Event2.ultimate / 3, cxl.outCoveredClaims[7].ultimate)
        assertEquals("claim40Event3", claim40Event3.ultimate / 10, cxl.outCoveredClaims[9].ultimate)
    }

    void testEventClaimsAggregateDeductible() {
        ReinsuranceContract cxl = getContract2()

        cxl.inClaims << claim0Event0 << claim10Event0 << claim20Event0 << claim30Event0 << claim10Event1
        cxl.inClaims << claim15Event1 << claim35Event1 << claim30Event2 << claim60Event2 << claim40Event3
        cxl.inClaims << claim60Event3 << claim120Event4

        def probeCXLnet = new TestProbe(cxl, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims

        cxl.doCalculation()
        assertTrue "number of ceded claims", 12 == cxl.outCoveredClaims.size()  // claim0Event0 and claim120Event4 don't have a ceded part
        // [id -1] as the first claim won't have any ceded part
        assertEquals("claim10Event0", claim10Event0.ultimate * 0.4, cxl.outCoveredClaims[1].ultimate)
        assertEquals("claim10Event1", claim10Event1.ultimate * 0.4, cxl.outCoveredClaims[4].ultimate)
        assertEquals("claim30Event2", claim30Event2.ultimate / 3 * 0.8, cxl.outCoveredClaims[7].ultimate)
        assertEquals("claim40Event3", claim40Event3.ultimate / 10 * 0.8, cxl.outCoveredClaims[9].ultimate)
    }

    void testEventClaimsAggregateDeductiblePMO_1092() {
        ReinsuranceContract cxl = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.CXL,
                        ["attachmentPoint": 25,
                                "limit": 30,
                                "aggregateLimit": 90,
                                "aggregateDeductible": 4,
                                "premiumBase": PremiumBase.ABSOLUTE,
                                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap()),
                                "premium": 1,
                                "reinstatementPremiums": new TableMultiDimensionalParameter([1d, 1d], ['Reinstatement Premium']),
                                "coveredByReinsurer": 1d]))


        Claim claim30Event0 = new Claim(event: event0, claimType: ClaimType.EVENT, value: 30d)
        Claim claim30Event1 = new Claim(event: event1, claimType: ClaimType.EVENT, value: 30d)
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premiumWritten: 100)
        cxl.inClaims << claim30Event0 << claim30Event1
        cxl.inUnderwritingInfo << underwritingInfo

        // needed in order to trigger the calculation of ceded uw info
        def probeCXLCededUwInfo = new TestProbe(cxl, "outCoverUnderwritingInfo")

        cxl.doCalculation()
        assertTrue "number of ceded claims", 2 == cxl.outCoveredClaims.size()
        assertEquals "claim10Event0", 3, cxl.outCoveredClaims[0].ultimate
        assertEquals "claim10Event1", 3, cxl.outCoveredClaims[1].ultimate
        assertEquals "underwriting info", 1.2, cxl.outCoverUnderwritingInfo[0].premiumWritten

        // apply the same calculations twice to make sure everything is properly reset between periods/iterations
        cxl.reset()
        cxl.inClaims << claim30Event0 << claim30Event1
        cxl.inUnderwritingInfo << underwritingInfo
        cxl.doCalculation()
        assertTrue "number of ceded claims", 2 == cxl.outCoveredClaims.size()
        assertEquals "claim10Event0", 3, cxl.outCoveredClaims[0].ultimate
        assertEquals "claim10Event1", 3, cxl.outCoveredClaims[1].ultimate
        assertEquals "underwriting info", 1.2, cxl.outCoverUnderwritingInfo[0].premiumWritten
    }

    void testGetCededUnderwritingInfoGNPI() {
        ReinsuranceContract cxl = getContract1()
        UnderwritingInfo grossUnderwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()

        cxl.parmContractStrategy.premiumBase = PremiumBase.GNPI
        UnderwritingInfo cededUnderwritingInfo = cxl.parmContractStrategy.calculateCoverUnderwritingInfo(grossUnderwritingInfo, 0)

        assertEquals "premium written", cxl.parmContractStrategy.premium * grossUnderwritingInfo.premiumWritten, cededUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", cxl.parmContractStrategy.premium * grossUnderwritingInfo.premiumWrittenAsIf, cededUnderwritingInfo.premiumWrittenAsIf
    }

    void testGetCededUnderwritingInfoROE() {
        ReinsuranceContract cxl = getContract1()
        UnderwritingInfo grossUnderwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        cxl.parmContractStrategy.premiumBase = PremiumBase.RATE_ON_LINE
        Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = new HashMap<UnderwritingInfo, Double>(1);
        grossPremiumSharesPerBand.put(grossUnderwritingInfo, 1);
        cxl.parmContractStrategy.grossPremiumSharesPerBand = grossPremiumSharesPerBand
        UnderwritingInfo cededUnderwritingInfo = cxl.parmContractStrategy.calculateCoverUnderwritingInfo(grossUnderwritingInfo, 0d)

        assertEquals "premium written", cxl.parmContractStrategy.premium * cxl.parmContractStrategy.grossPremiumSharesPerBand.get(grossUnderwritingInfo) * cxl.parmContractStrategy.limit, cededUnderwritingInfo.premiumWritten
        assertEquals "premium written as if", cxl.parmContractStrategy.premium * cxl.parmContractStrategy.grossPremiumSharesPerBand.get(grossUnderwritingInfo) * cxl.parmContractStrategy.limit, cededUnderwritingInfo.premiumWrittenAsIf
    }

    void testGetCededUnderwritingInfoIAE() {
        ReinsuranceContract cxl = getContract1()
        UnderwritingInfo underwritingInfo = UnderwritingInfoTests.getUnderwritingInfo()
        cxl.parmContractStrategy.premiumBase = PremiumBase.NUMBER_OF_POLICIES
        shouldFail(IllegalArgumentException) {
            cxl.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0d)
        }
    }

    void testReinstatementPremiumAllUsed() {
        ReinsuranceContract cxl = getContract0()
        cxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        cxl.inClaims << claim0Event0 << claim10Event0 << claim20Event0 << claim30Event0 << claim10Event1
        cxl.inClaims << claim15Event1 << claim35Event1 << claim30Event2 << claim60Event2 << claim40Event3
        cxl.inClaims << claim60Event3 << claim120Event4

        def probecxl = new TestProbe(cxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        cxl.doCalculation()

        double usedReinstatements = 7 / 3

        assertEquals "premium written", cxl.parmContractStrategy.premium * (1 + cxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                cxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }

    void testReinstatementPremiumNoneUsed() {
        ReinsuranceContract cxl = getContract0()
        cxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        cxl.inClaims << claim0Event0 << claim20Event0 //<< claim30Event0

        def probecxl = new TestProbe(cxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        cxl.doCalculation()

        double usedReinstatements = 0d

        assertEquals "premium written", cxl.parmContractStrategy.premium * (1 + cxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                cxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }

    void testReinstatementPremiumInstantRefill() {
        ReinsuranceContract cxl = getContract0()
        cxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        cxl.inClaims << claim30Event0

        def probecxl = new TestProbe(cxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        cxl.doCalculation()

        double usedReinstatements = 1d / 3d

        assertEquals "premium written",
                cxl.parmContractStrategy.premium * (1 + cxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                cxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }

    void testReinstatementPremiumOneUsed() {
        ReinsuranceContract cxl = getContract0()
        cxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        cxl.inClaims << claim0Event0 << claim10Event0 << claim20Event0 << claim30Event0

        def probecxl = new TestProbe(cxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        cxl.doCalculation()

        double usedReinstatements = 1d

        assertEquals "premium written", cxl.parmContractStrategy.premium * (1 + cxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                cxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }

    void testReinstatementPremiumOneAndAHalfUsed() {
        ReinsuranceContract cxl = getContract0()
        cxl.inUnderwritingInfo << UnderwritingInfoTests.getUnderwritingInfo()

        cxl.inClaims << claim0Event0 << claim10Event0 << claim20Event0 << claim30Event0 << claim35Event1

        def probecxl = new TestProbe(cxl, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of cover underwriting info

        cxl.doCalculation()

        double usedReinstatements = 1.5

        assertEquals "premium written", cxl.parmContractStrategy.premium * (1 + cxl.parmContractStrategy.reinstatementPremiums.values[0] * usedReinstatements),
                cxl.outCoverUnderwritingInfo[0].premiumWritten, 1e-6
    }
}