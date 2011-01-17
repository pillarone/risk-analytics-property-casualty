package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategyTests
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.StopLossContractStrategyTests
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategyTests
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceProgram3SerialContractsTests extends GroovyTestCase {

    void testWiring() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)
        ReinsuranceProgram3SerialContracts program = new ReinsuranceProgram3SerialContracts()
        program.wire()

        program.subContract1.parmContractStrategy = QuotaShareContractStrategyTests.getQuotaShareContract(0.5).parmContractStrategy
        program.subContract2.parmContractStrategy = WXLContractStrategyTests.getContract0().parmContractStrategy
        program.subContract3.parmContractStrategy = StopLossContractStrategyTests.getContractSL0().parmContractStrategy

        program.inClaims << attrClaim100 << largeClaim60
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 0)
        underwritingInfo.originalUnderwritingInfo = underwritingInfo
        program.inUnderwritingInfo << underwritingInfo

        assertTrue program.outClaimsCeded.isEmpty()
        assertTrue program.outClaimsNet.isEmpty()
        assertTrue program.subContract1.outCoveredClaims.isEmpty()
        assertTrue program.subContract1.outUncoveredClaims.isEmpty()
        assertTrue program.subContract2.outCoveredClaims.isEmpty()
        assertTrue program.subContract2.outUncoveredClaims.isEmpty()
        assertTrue program.subContract3.outCoveredClaims.isEmpty()
        assertTrue program.subContract3.outUncoveredClaims.isEmpty()

        def probeClaimsCeded = new TestProbe(program, "outClaimsCeded")
        List claimsCeded = probeClaimsCeded.result

        def probeClaimsNet = new TestProbe(program, "outClaimsNet")
        List claimsNet = probeClaimsNet.result

        program.start()
        assertEquals("number of ceded claims", 2, claimsCeded.size())
        assertEquals("number of net claims", 2, claimsNet.size())

        assertTrue program.outClaimsCeded.isEmpty()
        assertTrue program.outClaimsNet.isEmpty()
        assertTrue program.subContract1.outCoveredClaims.isEmpty()
        assertTrue program.subContract1.outUncoveredClaims.isEmpty()
        assertTrue program.subContract2.outCoveredClaims.isEmpty()
        assertTrue program.subContract2.outUncoveredClaims.isEmpty()
        assertTrue program.subContract3.outCoveredClaims.isEmpty()
        assertTrue program.subContract3.outUncoveredClaims.isEmpty()
    }

    void testClaimValues() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d)

        ReinsuranceProgram3SerialContracts program = new ReinsuranceProgram3SerialContracts()
        program.wire()

        program.subContract1.parmContractStrategy = QuotaShareContractStrategyTests.getQuotaShareContract(0.5).parmContractStrategy
        program.subContract2.parmContractStrategy = WXLContractStrategyTests.getContract0().parmContractStrategy
        program.subContract3.parmContractStrategy = StopLossContractStrategyTests.getContractSL0().parmContractStrategy

        program.inClaims << attrClaim100 << largeClaim60
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 0)
        underwritingInfo.originalUnderwritingInfo = underwritingInfo
        program.inUnderwritingInfo << underwritingInfo

        def probeClaimsCeded = new TestProbe(program, "outClaimsCeded")
        List claimsCeded = probeClaimsCeded.result

        def probeClaimsNet = new TestProbe(program, "outClaimsNet")
        List claimsNet = probeClaimsNet.result

        program.start()

        assertEquals("ceded attritional claim", 50, claimsCeded[0].ultimate)
        assertEquals("net attritional claim", 50, claimsNet[0].ultimate)
        assertEquals("ceded large claim", 40, claimsCeded[1].ultimate)
        assertEquals("net large claim", 20, claimsNet[1].ultimate, 0.000001)
    }

    void testUnderwriting() {
        Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d)
        Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, value: 60d)
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 1000, numberOfPolicies: 10,
                exposureDefinition: Exposure.ABSOLUTE)
        underwritingInfo.originalUnderwritingInfo = underwritingInfo

        ReinsuranceProgram3SerialContracts program = new ReinsuranceProgram3SerialContracts()
        program.wire()

        program.subContract1.parmContractStrategy = QuotaShareContractStrategyTests.getQuotaShareContract(0.5).parmContractStrategy
        program.subContract2.parmContractStrategy = WXLContractStrategyTests.getContract0().parmContractStrategy
        program.subContract3.parmContractStrategy = StopLossContractStrategyTests.getContractSL0().parmContractStrategy

        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << underwritingInfo

        def probeUnderwritingInfoContract1Net = new TestProbe(program.subContract1, "outCoverUnderwritingInfo")
        List uwInfoNet1 = probeUnderwritingInfoContract1Net.result

        def probeUnderwritingInfoContract2Net = new TestProbe(program.subContract2, "outCoverUnderwritingInfo")
        List uwInfoNet2 = probeUnderwritingInfoContract2Net.result

        def probeUnderwritingInfoContract3Net = new TestProbe(program.subContract3, "outCoverUnderwritingInfo")
        List uwInfoNet3 = probeUnderwritingInfoContract3Net.result

        def probeUnderwritingInfoNet = new TestProbe(program.subContract1, "outCoverUnderwritingInfo")
        List uwInfoNet = probeUnderwritingInfoNet.result

        program.start()

        assertEquals "underwriting info after contract1", 1, uwInfoNet1.size()
        assertEquals "underwriting info after contract2", 1, uwInfoNet2.size()
        assertEquals "underwriting info after contract3", 1, uwInfoNet3.size()
        assertEquals "net underwriting", 1, uwInfoNet.size()
    }
}