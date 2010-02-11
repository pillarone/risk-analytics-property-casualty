package org.pillarone.riskanalytics.domain.pc.reinsurance.programs.cashflow

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.MultiLinesPerilsReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.MultiLinesPerilsReinsuranceContractTests
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket

/**
 * ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class MultiLinesPerilsReinsuranceProgramTests extends GroovyTestCase {

    void testUsage() {
        MultiLinesPerilsReinsuranceProgram program = new MultiLinesPerilsReinsuranceProgram()

        MultiLinesPerilsReinsuranceContract quotaShare25 = MultiLinesPerilsReinsuranceContractTests.getContractFullCoverAllLinesPerils()
        quotaShare25.name = 'Quota Share 25%'
        quotaShare25.parmInuringPriority = 0
        program.addSubComponent quotaShare25

        MultiLinesPerilsReinsuranceContract quotaShare50 = MultiLinesPerilsReinsuranceContractTests.getQuotaShare50FullCoverAllLinesPerils()
        quotaShare50.name = 'Quota Share 50%'
        quotaShare50.parmInuringPriority = 1
        program.addSubComponent quotaShare50

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        program.inClaims << claim1000 << claim800

        program.wire()

        List qs25ClaimsNet = new TestProbe(quotaShare25, "outUncoveredClaims").result
        List qs25ClaimsCeded = new TestProbe(quotaShare25, "outCoveredClaims").result

        List qs50ClaimsNet = new TestProbe(quotaShare50, "outUncoveredClaims").result
        List qs50ClaimsCeded = new TestProbe(quotaShare50, "outCoveredClaims").result

        List programClaimsNet = new TestProbe(program, 'outClaimsNet').result
        List programClaimsCeded = new TestProbe(program, "outClaimsCeded").result

        program.start()

        assertEquals 'number of ceded claims after quotaShare25', 2, qs25ClaimsCeded.size()
        assertEquals 'number of net claims after quotaShare25', 2, qs25ClaimsNet.size()
        assertEquals 'number of ceded claims after quotaShare50', 2, qs50ClaimsCeded.size()
        assertEquals 'number of net claims after quotaShare50', 2, qs50ClaimsNet.size()
        assertEquals 'number of net claims after program', 2, programClaimsNet.size()

        assertEquals 'claim1000 ceded incurred after quotaShare25', 250, qs25ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after quotaShare50', 375, qs50ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after program', 625, programClaimsCeded[0].incurred
        
        assertEquals 'claim1000 net incurred after quotaShare25', 750, qs25ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after quotaShare50', 375, qs50ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after program', 375, programClaimsNet[0].incurred

        assertEquals 'claim800 ceded incurred after quotaShare25', 200, qs25ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after quotaShare50', 300, qs50ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after program', 500, programClaimsCeded[1].incurred
        
        assertEquals 'claim800 net incurred after quotaShare25', 600, qs25ClaimsNet[1].incurred
        assertEquals 'claim800 net incurred after quotaShare50', 300, qs50ClaimsNet[1].incurred
        assertEquals 'claim800 net incurred after program', 300, programClaimsNet[1].incurred
    }

    void testUsageSecondBasedOnCeded() {
        MultiLinesPerilsReinsuranceProgram program = new MultiLinesPerilsReinsuranceProgram()
        
        MultiLinesPerilsReinsuranceContract quotaShare25 = MultiLinesPerilsReinsuranceContractTests.getContractFullCoverAllLinesPerils()
        quotaShare25.name = 'Quota Share 25%'
        quotaShare25.parmInuringPriority = 0
        program.addSubComponent quotaShare25

        MultiLinesPerilsReinsuranceContract quotaShare50 = MultiLinesPerilsReinsuranceContractTests.getQuotaShare50FullCoverAllLinesPerils()
        quotaShare50.name = 'Quota Share 50%'
        quotaShare50.parmInuringPriority = 1
        quotaShare50.parmBasedOn = ReinsuranceContractBase.CEDED 
        program.addSubComponent quotaShare50

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        program.inClaims << claim1000 << claim800

        program.wire()

        List qs25ClaimsNet = new TestProbe(quotaShare25, "outUncoveredClaims").result
        List qs25ClaimsCeded = new TestProbe(quotaShare25, "outCoveredClaims").result

        List qs50ClaimsNet = new TestProbe(quotaShare50, "outUncoveredClaims").result
        List qs50ClaimsCeded = new TestProbe(quotaShare50, "outCoveredClaims").result

        List programClaimsNet = new TestProbe(program, 'outClaimsNet').result
        List programClaimsCeded = new TestProbe(program, "outClaimsCeded").result

        program.start()

        assertEquals 'number of ceded claims after quotaShare25', 2, qs25ClaimsCeded.size()
        assertEquals 'number of net claims after quotaShare25', 2, qs25ClaimsNet.size()
        assertEquals 'number of ceded claims after quotaShare50', 2, qs50ClaimsCeded.size()
        assertEquals 'number of net claims after quotaShare50', 2, qs50ClaimsNet.size()
        assertEquals 'number of net claims after program', 2, programClaimsNet.size()

        assertEquals 'claim1000 ceded incurred after quotaShare25', 250, qs25ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after quotaShare50', 125, qs50ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after program', 375, programClaimsCeded[0].incurred
        
        assertEquals 'claim1000 net incurred after quotaShare25', 750, qs25ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after quotaShare50', 125, qs50ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after program', 625, programClaimsNet[0].incurred

        assertEquals 'claim800 ceded incurred after quotaShare25', 200, qs25ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after quotaShare50', 100, qs50ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after program', 300, programClaimsCeded[1].incurred
        
        assertEquals 'claim800 net incurred after quotaShare25', 600, qs25ClaimsNet[1].incurred
        assertEquals 'claim800 net incurred after quotaShare50', 100, qs50ClaimsNet[1].incurred
        assertEquals 'claim800 net incurred after program', 500, programClaimsNet[1].incurred
    }

    void testUsageThirdBasedOnCeded() {
        MultiLinesPerilsReinsuranceProgram program = new MultiLinesPerilsReinsuranceProgram()

        MultiLinesPerilsReinsuranceContract quotaShare25_1 = MultiLinesPerilsReinsuranceContractTests.getContractFullCoverAllLinesPerils()
        quotaShare25_1.name = 'Quota Share 25% #1'
        quotaShare25_1.parmInuringPriority = 0
        program.addSubComponent quotaShare25_1
        
        MultiLinesPerilsReinsuranceContract quotaShare25_2 = MultiLinesPerilsReinsuranceContractTests.getContractFullCoverAllLinesPerils()
        quotaShare25_2.name = 'Quota Share 25% #2'
        quotaShare25_2.parmInuringPriority = 0
        program.addSubComponent quotaShare25_2

        MultiLinesPerilsReinsuranceContract quotaShare50 = MultiLinesPerilsReinsuranceContractTests.getQuotaShare50FullCoverAllLinesPerils()
        quotaShare50.name = 'Quota Share 50%'
        quotaShare50.parmInuringPriority = 1
        quotaShare50.parmBasedOn = ReinsuranceContractBase.CEDED 
        program.addSubComponent quotaShare50

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        program.inClaims << claim1000 << claim800

        program.wire()

        List qs25_1ClaimsNet = new TestProbe(quotaShare25_1, "outUncoveredClaims").result
        List qs25_1ClaimsCeded = new TestProbe(quotaShare25_1, "outCoveredClaims").result

        List qs25_2ClaimsNet = new TestProbe(quotaShare25_2, "outUncoveredClaims").result
        List qs25_2ClaimsCeded = new TestProbe(quotaShare25_2, "outCoveredClaims").result

        List qs50ClaimsNet = new TestProbe(quotaShare50, "outUncoveredClaims").result
        List qs50ClaimsCeded = new TestProbe(quotaShare50, "outCoveredClaims").result

        List programClaimsNet = new TestProbe(program, 'outClaimsNet').result
        List programClaimsCeded = new TestProbe(program, "outClaimsCeded").result

        program.start()

        assertEquals 'number of ceded claims after quotaShare25_1', 2, qs25_1ClaimsCeded.size()
        assertEquals 'number of net claims after quotaShare25_1', 2, qs25_1ClaimsNet.size()
        assertEquals 'number of ceded claims after quotaShare25_2', 2, qs25_2ClaimsCeded.size()
        assertEquals 'number of net claims after quotaShare25_2', 2, qs25_2ClaimsNet.size()
        assertEquals 'number of ceded claims after quotaShare50', 2, qs50ClaimsCeded.size()
        assertEquals 'number of net claims after quotaShare50', 2, qs50ClaimsNet.size()
        assertEquals 'number of net claims after program', 2, programClaimsNet.size()

        assertEquals 'claim1000 ceded incurred after quotaShare25_1', 250, qs25_1ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after quotaShare25_2', 250, qs25_2ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after quotaShare50', 250, qs50ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after program', 750, programClaimsCeded[0].incurred
        
        assertEquals 'claim1000 net incurred after quotaShare25_1', 750, qs25_1ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after quotaShare25_2', 750, qs25_2ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after quotaShare50', 250, qs50ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after program', 250, programClaimsNet[0].incurred

        assertEquals 'claim800 ceded incurred after quotaShare25_1', 200, qs25_1ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after quotaShare25_2', 200, qs25_2ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after quotaShare50', 200, qs50ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after program', 600, programClaimsCeded[1].incurred
        
        assertEquals 'claim800 net incurred after quotaShare25_1', 600, qs25_1ClaimsNet[1].incurred
        assertEquals 'claim800 net incurred after quotaShare25_2', 600, qs25_2ClaimsNet[1].incurred
        assertEquals 'claim800 net incurred after quotaShare50', 200, qs50ClaimsNet[1].incurred
        assertEquals 'claim800 net incurred after program', 200, programClaimsNet[1].incurred
    }

    /**
     * For this test case, contracts 1, 2 & 3 have quota shares a, b & c respectively.
     * Contracts 1 & 2 have inuring priority 0 and contract 3 has inuring priority 1,
     * and is based on NET. We want to test that the results really are based on NET
     * and not on CEDED (i.e., we want a+b != 0.5). We expect the net and ceded amounts
     * for contract 3 and thus as well for the overall program to differ from their
     * respective amounts if contract 3 were based on CEDED.
     */
    void testUsageThirdBasedOnNet() {
        MultiLinesPerilsReinsuranceProgram program = new MultiLinesPerilsReinsuranceProgram()
        double eps = 0.0000000001

        // set up a contract (100a% quota share) for 4 years (1.1.2010--31.12.2013)
        // with inuring priority 0 and iteration scope starting 1.1.2010
        double shareContract1 = 0.2
        MultiLinesPerilsReinsuranceContract qs1 = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, shareContract1, 0)
        qs1.name = 'Quota Share #1 (20%)'
        qs1.parmInuringPriority = 0 // default
        program.addSubComponent qs1

        double shareContract2 = 0.4
        MultiLinesPerilsReinsuranceContract qs2 = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, shareContract2, 0)
        qs2.name = 'Quota Share #2 (40%)'
        qs2.parmInuringPriority = 0 // default
        program.addSubComponent qs2

        double shareContract3 = 0.5
        MultiLinesPerilsReinsuranceContract qs3 = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, shareContract3, 0)
        qs3.name = 'Quota Share #3 (50%)'
        qs3.parmInuringPriority = 1
        qs3.parmBasedOn = ReinsuranceContractBase.NET // default
        program.addSubComponent qs3

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        program.inClaims << claim1000 << claim800

        program.wire()

        List qs1_ClaimsNet = new TestProbe(qs1, "outUncoveredClaims").result
        List qs1_ClaimsCeded = new TestProbe(qs1, "outCoveredClaims").result

        List qs2_ClaimsNet = new TestProbe(qs2, "outUncoveredClaims").result
        List qs2_ClaimsCeded = new TestProbe(qs2, "outCoveredClaims").result

        List qs3_ClaimsNet = new TestProbe(qs3, "outUncoveredClaims").result
        List qs3_ClaimsCeded = new TestProbe(qs3, "outCoveredClaims").result

        List programClaimsNet = new TestProbe(program, 'outClaimsNet').result
        List programClaimsCeded = new TestProbe(program, "outClaimsCeded").result

        program.start()

        assertEquals 'number of ceded claims after qs1', 2, qs1_ClaimsCeded.size()
        assertEquals 'number of net claims after qs1', 2, qs1_ClaimsNet.size()
        assertEquals 'number of ceded claims after qs2', 2, qs2_ClaimsCeded.size()
        assertEquals 'number of net claims after qs2', 2, qs2_ClaimsNet.size()
        assertEquals 'number of ceded claims after qs3', 2, qs3_ClaimsCeded.size()
        assertEquals 'number of net claims after qs3', 2, qs3_ClaimsNet.size()
        assertEquals 'number of net claims after program', 2, programClaimsNet.size()

        assertEquals 'qs3 based on Net after qs1 and _2', ReinsuranceContractBase.NET, qs3.parmBasedOn

        assertEquals 'claim1000 ceded incurred after qs1', 1000*shareContract1, qs1_ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after qs2', 1000*shareContract2, qs2_ClaimsCeded[0].incurred
        assertEquals 'claim1000 ceded incurred after qs3', 1000*shareContract3*(1-shareContract1-shareContract2), qs3_ClaimsCeded[0].incurred, eps
        assertEquals 'claim1000 ceded incurred after program', 1000*((shareContract1+shareContract2+shareContract3)-(shareContract1+shareContract2)*shareContract3), programClaimsCeded[0].incurred, eps

        assertEquals 'claim1000 net incurred after qs1', 1000*(1-shareContract1), qs1_ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after qs2', 1000*(1-shareContract2), qs2_ClaimsNet[0].incurred
        assertEquals 'claim1000 net incurred after qs3', 1000*shareContract3*(1-(shareContract1+shareContract2)), qs3_ClaimsNet[0].incurred, eps
        assertEquals 'claim1000 net incurred after program', 1000*(1-(shareContract1+shareContract2))*(1-shareContract3), programClaimsNet[0].incurred, eps

        assertEquals 'claim800 ceded incurred after qs1', 800*shareContract1, qs1_ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after qs2', 800*shareContract2, qs2_ClaimsCeded[1].incurred
        assertEquals 'claim800 ceded incurred after qs3', 800*shareContract3*(1-shareContract1-shareContract2), qs3_ClaimsCeded[1].incurred, eps
        assertEquals 'claim800 ceded incurred after program', 800*((shareContract1+shareContract2+shareContract3)-(shareContract1+shareContract2)*shareContract3), programClaimsCeded[1].incurred, eps

        assertEquals 'claim800 net incurred after qs1', 800*(1-shareContract1), qs1_ClaimsNet[1].incurred, eps
        assertEquals 'claim800 net incurred after qs2', 800*(1-shareContract2), qs2_ClaimsNet[1].incurred, eps
        assertEquals 'claim800 net incurred after qs3', 800*shareContract3*(1-(shareContract1+shareContract2)), qs3_ClaimsNet[1].incurred, eps
        assertEquals 'claim800 net incurred after program', 800*(1-(shareContract1+shareContract2))*(1-shareContract3), programClaimsNet[1].incurred, eps
    }
}
