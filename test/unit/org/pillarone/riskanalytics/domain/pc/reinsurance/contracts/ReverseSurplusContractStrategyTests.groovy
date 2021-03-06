package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 * @author Michael-Noe (at) Web (dot) de
 */
class ReverseSurplusContractStrategyTests extends GroovyTestCase {

    static final double EPSILON = 1E-10

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.SURPLUSREVERSE,
                        ["retention": 100,
                         "lines": 5,
                         "defaultCededLossShare": 0d,
                         "coveredByReinsurer": 1d]),
                simulationScope: CommissionTests.getTestSimulationScope())
    }

    static ReinsuranceContract getContract1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                    ReinsuranceContractType.SURPLUSREVERSE,
                    ["retention": 100,
                     "lines": 5,
                     "coveredByReinsurer": 1d,
                     "defaultCededLossShare": 0.5]),
                simulationScope: CommissionTests.getTestSimulationScope())
    }

    private static List<UnderwritingInfo> getMockUnderwritingInfo() {
        // first band: ceded = 0
        UnderwritingInfo info0 = new UnderwritingInfo(
                numberOfPolicies: 1000,
                sumInsured: 80,
                maxSumInsured: 100,
                premium: 5050)
        // second band: ceded = (200-100)/200 = 0.5
        UnderwritingInfo info1 = new UnderwritingInfo(
                numberOfPolicies: 100,
                sumInsured: 200,
                maxSumInsured: 1000,
                premium: 2050)
        // third band: ceded = (500-100)/500 = 0.8
        UnderwritingInfo info2 = new UnderwritingInfo(
                numberOfPolicies: 50,
                sumInsured: 500,
                maxSumInsured: 800,
                premium: 4050)
        // 4th band: ceded = 500/1500 = 0.333333333333333
        UnderwritingInfo info3 = new UnderwritingInfo(
                numberOfPolicies: 20,
                sumInsured: 1500,
                maxSumInsured: 1800,
                premium: 8050)
        List<UnderwritingInfo> uwInfos = []
        uwInfos << info0 << info1 << info2 << info3
        return uwInfos
    }

 private static List<UnderwritingInfo> getMockUnderwritingInfo1() {
        //besser noch anders machen  (evtl statt UnderwrittingInfo ExposerInfo nehmen)
        UnderwritingInfo info4 = new UnderwritingInfo(
                numberOfPolicies: 1,
                sumInsured: 0,
                premium: 0)
        return getMockUnderwritingInfo() << info4
    }

    void testCalculateCededClaimsOnly() {
        List<UnderwritingInfo> exposures = getMockUnderwritingInfo()
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d, exposure: exposures[0])
        Claim attrClaim101 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d, exposure: exposures[1])
        Claim attrClaim102 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d, exposure: exposures[2])

        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, exposure: exposures[0])
        Claim largeClaim61 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, exposure: exposures[1])
        Claim largeClaim62 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, exposure: exposures[2])
        Claim attrClaim303 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 300d, exposure: exposures[3])

        ReinsuranceContract contract = getContract0()
        assertTrue contract.outCoveredClaims.isEmpty()

        contract.inClaims << attrClaim100 << attrClaim101 << attrClaim102 << largeClaim60 << largeClaim61 << largeClaim62 << attrClaim303
        def probeNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 7, contract.outUncoveredClaims.size()
        assertEquals "outClaimsNet Attr 0", 0, contract.outUncoveredClaims[0].ultimate
        assertEquals "outClaimsNet Attr 1", 50, contract.outUncoveredClaims[1].ultimate
        assertEquals "outClaimsNet Attr 2", 80, contract.outUncoveredClaims[2].ultimate
        assertEquals "outClaimsNet Large 0", 0, contract.outUncoveredClaims[3].ultimate
        assertEquals "outClaimsNet Large 1", 30, contract.outUncoveredClaims[4].ultimate
        assertEquals "outClaimsNet Large 2", 48, contract.outUncoveredClaims[5].ultimate
        assertEquals "outClaimsNet Attr 3", 100, contract.outUncoveredClaims[6].ultimate, EPSILON
        assertEquals "outClaims.size", 7, contract.outCoveredClaims.size()
        assertEquals "outClaims Attr 0", 100, contract.outCoveredClaims[0].ultimate, EPSILON
        assertEquals "outClaims Attr 1", 50, contract.outCoveredClaims[1].ultimate
        assertEquals "outClaims Attr 2", 20, contract.outCoveredClaims[2].ultimate, EPSILON
        assertEquals "outClaims Large 60", 60, contract.outCoveredClaims[3].ultimate
        assertEquals "outClaims Large 61", 30, contract.outCoveredClaims[4].ultimate
        assertEquals "outClaims Large 62", 12, contract.outCoveredClaims[5].ultimate, EPSILON
        assertEquals "outClaims attClaim303", 200, contract.outCoveredClaims[6].ultimate, EPSILON

        contract.reset()
        assertTrue contract.outCoveredClaims.isEmpty()
    }

    void testCalculateCededClaimsOnly2() {
          List<UnderwritingInfo> exposures = getMockUnderwritingInfo1()
          Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
          Claim attrClaim101 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 150d)
          Claim attrClaim102 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 200d)
          Claim attrClaim303 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 300d)

          Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, exposure: exposures[0])
          Claim largeClaim61 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, exposure: exposures[1])
          Claim largeClaim62 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, exposure: exposures[2])
          Claim largeClaim63 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, exposure: exposures[4])


          ReinsuranceContract contract = getContract1()
          assertTrue contract.outCoveredClaims.isEmpty()
          contract.inClaims << attrClaim100 << attrClaim101 << attrClaim102 << attrClaim303 << largeClaim60 << largeClaim61 << largeClaim62 <<largeClaim63
          def probeNet = new TestProbe(contract, "outUncoveredClaims")    // needed in order to trigger the calculation of net claims
          contract.doCalculation()

          assertEquals "outClaimsNet.size", 8, contract.outUncoveredClaims.size()
          assertEquals "outClaimsNet Attr 0", 50, contract.outUncoveredClaims[0].ultimate
          assertEquals "outClaimsNet Attr 1", 75, contract.outUncoveredClaims[1].ultimate
          assertEquals "outClaimsNet Attr 2", 100, contract.outUncoveredClaims[2].ultimate
          assertEquals "outClaimsNet Attr 3", 150, contract.outUncoveredClaims[3].ultimate
          assertEquals "outClaimsNet Large 0", 0, contract.outUncoveredClaims[4].ultimate
          assertEquals "outClaimsNet Large 1", 30, contract.outUncoveredClaims[5].ultimate
          assertEquals "outClaimsNet Large 2", 48, contract.outUncoveredClaims[6].ultimate
          assertEquals "outClaimsNet Large 3", 00, contract.outUncoveredClaims[7].ultimate


          assertEquals "outClaims.size", 8, contract.outCoveredClaims.size()
          assertEquals "outClaims Attr 0", 50, contract.outCoveredClaims[0].ultimate
          assertEquals "outClaims Attr 1", 75, contract.outCoveredClaims[1].ultimate
          assertEquals "outClaims Attr 2", 100, contract.outCoveredClaims[2].ultimate
          assertEquals "outClaims Attr 3", 150, contract.outCoveredClaims[3].ultimate

          assertEquals "outClaims Large 0", 60, contract.outCoveredClaims[4].ultimate
          assertEquals "outClaims Large 1", 30, contract.outCoveredClaims[5].ultimate
          assertEquals "outClaims Large 2", 12, contract.outCoveredClaims[6].ultimate, EPSILON
          assertEquals "outClaims Large 3", 60, contract.outCoveredClaims[7].ultimate

          contract.reset()
          assertTrue contract.outCoveredClaims.isEmpty()
      }

    void testGetCededUnderwritingInfo2() {
        ReinsuranceContract contract = getContract1()
        List<UnderwritingInfo> inUnderwritingInfos = getMockUnderwritingInfo()
        List<UnderwritingInfo> coverUnderwritingInfo = []
        for(UnderwritingInfo underwritingInfo : inUnderwritingInfos) {
            coverUnderwritingInfo << contract.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0).copy()
        }

        assertEquals "coverUnderwritingInfo.size", inUnderwritingInfos.size(), coverUnderwritingInfo.size()
        assertEquals "premium written 0", 1d * inUnderwritingInfos[0].premium, coverUnderwritingInfo[0].premium, EPSILON
        assertEquals "premium written 1", 0.5 * inUnderwritingInfos[1].premium, coverUnderwritingInfo[1].premium
        assertEquals "premium written 2", 0.2 * inUnderwritingInfos[2].premium, coverUnderwritingInfo[2].premium, EPSILON
    }

    void testGetCededUnderwritingInfo() {
        ReinsuranceContract contract = getContract1()
        List<UnderwritingInfo> inUnderwritingInfos = getMockUnderwritingInfo()
        List<UnderwritingInfo> coverUnderwritingInfo = []
        for(UnderwritingInfo underwritingInfo : inUnderwritingInfos) {
            coverUnderwritingInfo << contract.parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, 0).copy()
        }

        assertEquals "coverUnderwritingInfo.size", inUnderwritingInfos.size(), coverUnderwritingInfo.size()
        assertEquals "premium written 0", 1d * inUnderwritingInfos[0].premium, coverUnderwritingInfo[0].premium
        assertEquals "premium written 1", 0.5 * inUnderwritingInfos[1].premium, coverUnderwritingInfo[1].premium
        assertEquals "premium written 2", 0.2 * inUnderwritingInfos[2].premium, coverUnderwritingInfo[2].premium, EPSILON
        // todo (sku): finalize discussion with meli
        /*assertEquals "premium written as if 0", 0d * inUnderwritingInfos[0].premiumWrittenAsIf, coverUnderwritingInfo[0].premiumWrittenAsIf
        assertEquals "premium written as if 1", 0.5 * inUnderwritingInfos[0].premiumWrittenAsIf, coverUnderwritingInfo[0].premiumWrittenAsIf
        assertEquals "premium written as if 2", 0.6 * inUnderwritingInfos[0].premiumWrittenAsIf, coverUnderwritingInfo[0].premiumWrittenAsIf*/
    }
}
