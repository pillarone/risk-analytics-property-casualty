package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareMultiSlidingScaleContractStrategyTests extends GroovyTestCase {

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(
                        ReinsuranceContractType.QUOTASHAREMULTISLIDINGSCALE, ["quotaShare": 0.5d, "commission": 0.0d, "coveredByReinsurer": 1d,
                        "levels": new TableMultiDimensionalParameter([[0d, 0.4d, 0.6d], [0.3d, 0.2d, 0.1d]], ['claim level', 'rate level'])]))
    }

    void testCalculateCededPremiumOnly() {
        Claim attrClaim10 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 10d)
        Claim sglClaim70 = new Claim(claimType: ClaimType.SINGLE, ultimate: 70d)
        Claim sglClaim20 = new Claim(claimType: ClaimType.SINGLE, ultimate: 20d)

        UnderwritingInfo premiumAttrClaim10 = new UnderwritingInfo(premiumWritten: 200d)
        UnderwritingInfo premiumSglClaim70 = new UnderwritingInfo(premiumWritten: 120d)
        UnderwritingInfo premiumSglClaim20 = new UnderwritingInfo(premiumWritten: 180d)

        ReinsuranceContract contract = getContract0()
        contract.inClaims << attrClaim10 << sglClaim70 << sglClaim20
        contract.inUnderwritingInfo << premiumAttrClaim10 << premiumSglClaim70 << premiumSglClaim20

        def probe = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "premium quotaShare1a", 90, contract.outCoverUnderwritingInfo[0].premiumWritten
        assertEquals "premium quotaShare1b", 54, contract.outCoverUnderwritingInfo[1].premiumWritten
        assertEquals "premium quotaShare1c", 81, contract.outCoverUnderwritingInfo[2].premiumWritten

        Claim attrClaim50 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 50d)
        Claim sglClaim40 = new Claim(claimType: ClaimType.SINGLE, ultimate: 40d)
        Claim sglClaim10 = new Claim(claimType: ClaimType.SINGLE, ultimate: 10d)

        UnderwritingInfo premiumAttrClaim50 = new UnderwritingInfo(premiumWritten: 400d)
        UnderwritingInfo premiumSglClaim40 = new UnderwritingInfo(premiumWritten: 500d)
        UnderwritingInfo premiumSglClaim110 = new UnderwritingInfo(premiumWritten: 480d)

        contract = getContract0()
        contract.inClaims << attrClaim50 << sglClaim40 << sglClaim10
        contract.inUnderwritingInfo << premiumAttrClaim50 << premiumSglClaim40 << premiumSglClaim110

        probe = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims

        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "premium quotaShare2a", 160, contract.outCoverUnderwritingInfo[0].premiumWritten
        assertEquals "premium quotaShare2b", 200, contract.outCoverUnderwritingInfo[1].premiumWritten
        assertEquals "premium quotaShare2c", 192, contract.outCoverUnderwritingInfo[2].premiumWritten


        Claim attrClaim60 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 60d)
        Claim sglClaim30 = new Claim(claimType: ClaimType.SINGLE, ultimate: 30d)
        Claim sglClaim0 = new Claim(claimType: ClaimType.SINGLE, ultimate: 0d)

        UnderwritingInfo premiumAttrClaim90 = new UnderwritingInfo(premiumWritten: 640d)
        UnderwritingInfo premiumSglClaim30 = new UnderwritingInfo(premiumWritten: 500d)
        UnderwritingInfo premiumSglClaim0 = new UnderwritingInfo(premiumWritten: 480d)

        contract = getContract0()
        contract.inClaims << attrClaim60 << sglClaim30 << sglClaim0
        contract.inUnderwritingInfo << premiumAttrClaim90 << premiumSglClaim30 << premiumSglClaim0

        probe = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 0, contract.outUncoveredClaims.size()
        assertEquals "outClaims.size", 3, contract.outCoveredClaims.size()
        assertEquals "premium quotaShare3a", 224, contract.outCoverUnderwritingInfo[0].premiumWritten
        assertEquals "premium quotaShare3b", 175, contract.outCoverUnderwritingInfo[1].premiumWritten
        assertEquals "premium quotaShare3c", 168, contract.outCoverUnderwritingInfo[2].premiumWritten
    }
}