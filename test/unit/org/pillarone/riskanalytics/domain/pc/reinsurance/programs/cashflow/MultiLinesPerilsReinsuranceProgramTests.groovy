package org.pillarone.riskanalytics.domain.pc.reinsurance.programs.cashflow

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.MultiLinesPerilsReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.MultiLinesPerilsReinsuranceContractTests
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.util.TestPretendInChannelWired

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

    void testUnderwritingInfo() {
        MultiLinesPerilsReinsuranceProgram program = new MultiLinesPerilsReinsuranceProgram()

        double commission = 0.1
        // set up a quota share contract for 4 years (1.1.2010--31.12.2013)
        // with inuring priority 0 and iteration scope starting 1.1.2010
        // and quota share value given by the variable:
        double qs1Value = 0.2
        MultiLinesPerilsReinsuranceContract qs1 = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, qs1Value, 0, commission)
        qs1.name = 'Quota Share #1 (20%)'
        qs1.parmInuringPriority = 0 // default
        program.addSubComponent qs1

        double qs2Value = 0.4
        MultiLinesPerilsReinsuranceContract qs2 = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, qs2Value, 0, commission)
        qs2.name = 'Quota Share #2 (40%)'
        qs2.parmInuringPriority = 0 // default
        program.addSubComponent qs2

        double qs3Value = 0.5
        MultiLinesPerilsReinsuranceContract qs3 = MultiLinesPerilsReinsuranceContractTests.getAllLinesPerilsQuotaShareContract(2010, 2010, 4, qs3Value, 0, commission)
        qs3.name = 'Quota Share #3 (50%)'
        qs3.parmInuringPriority = 1
        qs3.parmBasedOn = ReinsuranceContractBase.NET // default
        program.addSubComponent qs3

        ClaimDevelopmentPacket claim1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 500, reserved: 500, changeInReserves: 500, originalClaim: new Claim())
        ClaimDevelopmentPacket claim800 = new ClaimDevelopmentPacket(incurred: 800, paid: 480, reserved: 320, changeInReserves: 480, originalClaim: new Claim())
        program.inClaims << claim1000 << claim800

        UnderwritingInfo originalUnderwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50)
        UnderwritingInfo originalUnderwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5)
        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: 50, originalUnderwritingInfo: originalUnderwritingInfo200)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: 5, originalUnderwritingInfo: originalUnderwritingInfo100)
        program.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        TestPretendInChannelWired inUnderwritingInfoWired = new TestPretendInChannelWired(program, "inUnderwritingInfo")
        program.wire()

        List qs1UWInfoNet = new TestProbe(qs1, 'outNetAfterCoverUnderwritingInfo').result
        List qs1UWInfoCeded = new TestProbe(qs1, 'outCoverUnderwritingInfo').result
        List qs1UWInfoAll = new TestProbe(program, 'outUnderwritingInfo').result

        List qs2UWInfoNet = new TestProbe(qs2, 'outNetAfterCoverUnderwritingInfo').result
        List qs2UWInfoCeded = new TestProbe(qs2, 'outCoverUnderwritingInfo').result
        List qs2UWInfoAll = new TestProbe(program, 'outUnderwritingInfo').result

        List qs3UWInfoNet = new TestProbe(qs3, 'outNetAfterCoverUnderwritingInfo').result
        List qs3UWInfoCeded = new TestProbe(qs3, 'outCoverUnderwritingInfo').result
        List qs3UWInfoAll = new TestProbe(program, 'outUnderwritingInfo').result

        List programUWInfoNet = new TestProbe(program, 'outNetAfterCoverUnderwritingInfo').result
        List programUWInfoCeded = new TestProbe(program, 'outCoverUnderwritingInfo').result
        List programUWInfoAll = new TestProbe(program, 'outUnderwritingInfo').result

        program.start()

        assertEquals 'number of UnderwritingInfo packets ceded after qs1', 2, qs1UWInfoCeded.size()
        assertEquals 'number of UnderwritingInfo packets ceded after qs2', 2, qs2UWInfoCeded.size()
        assertEquals 'number of UnderwritingInfo packets ceded after qs3', 2, qs3UWInfoCeded.size()
        assertEquals 'number of UnderwritingInfo packets ceded after program', 2, programUWInfoCeded.size()
        assertEquals 'number of UnderwritingInfo packets net after qs1', 2, qs1UWInfoNet.size()
        assertEquals 'number of UnderwritingInfo packets net after qs2', 2, qs2UWInfoNet.size()
        assertEquals 'number of UnderwritingInfo packets net after qs3', 2, qs3UWInfoNet.size()
        assertEquals 'number of UnderwritingInfo packets net after program', 2, programUWInfoNet.size()
        assertEquals 'number of UnderwritingInfo packets after qs1', 2, qs1UWInfoAll.size()
        assertEquals 'number of UnderwritingInfo packets after qs2', 2, qs2UWInfoAll.size()
        assertEquals 'number of UnderwritingInfo packets after qs3', 2, qs3UWInfoAll.size()
        assertEquals 'number of UnderwritingInfo packets after program', 2, programUWInfoAll.size()

        // compute expected ratios
        double layer1Ceded = qs1Value + qs2Value // 0.6
        double layer1Net = 1.0 - layer1Ceded // 0.4
        double layer2Ceded = layer1Net * qs3Value // 0.2
        double layer2Net = layer1Net * (1 - qs3Value) // 0.2
        double totalCeded = layer1Ceded + layer2Ceded // 0.8
        double totalNet = 1 - totalCeded // 0.2

        double eps = 1E-10
        assertEquals 'consistency of test case internal variables', totalNet, layer2Net//, eps

        assertEquals 'underwritinginfo200 ceded premium written after qs1', 200*qs1Value, qs1UWInfoCeded[0].premiumWritten
        assertEquals 'underwritinginfo200 ceded commission after qs1', 200*qs1Value*commission, qs1UWInfoCeded[0].commission
        assertEquals 'underwritinginfo200 net premium written after qs1', 200*(1-qs1Value), qs1UWInfoNet[0].premiumWritten, eps
        assertEquals 'underwritinginfo200 net commission after qs1', 200*qs1Value*commission, qs1UWInfoNet[0].commission

        assertEquals 'underwritinginfo100 ceded premium written after qs1', 100*qs1Value, qs1UWInfoCeded[1].premiumWritten
        assertEquals 'underwritinginfo100 ceded commission after qs1', 100*qs1Value*commission, qs1UWInfoCeded[1].commission
        assertEquals 'underwritinginfo100 net premium written after qs1', 100*(1-qs1Value), qs1UWInfoNet[1].premiumWritten, eps
        assertEquals 'underwritinginfo100 net commission after qs1', 100*qs1Value*commission, qs1UWInfoNet[1].commission

        assertEquals 'underwritinginfo200 ceded premium written after qs2', 200*qs2Value, qs2UWInfoCeded[0].premiumWritten
        assertEquals 'underwritinginfo200 ceded commission after qs2', 200*qs2Value*commission, qs2UWInfoCeded[0].commission
        assertEquals 'underwritinginfo200 net premium written after qs2', 200*(1-qs2Value), qs2UWInfoNet[0].premiumWritten, eps
        assertEquals 'underwritinginfo200 net commission after qs2', 200*qs2Value*commission, qs2UWInfoNet[0].commission

        assertEquals 'underwritinginfo100 ceded premium written after qs2', 100*qs2Value, qs2UWInfoCeded[1].premiumWritten
        assertEquals 'underwritinginfo100 ceded commission after qs2', 100*qs2Value*commission, qs2UWInfoCeded[1].commission
        assertEquals 'underwritinginfo100 net premium written after qs2', 100*(1-qs2Value), qs2UWInfoNet[1].premiumWritten, eps
        assertEquals 'underwritinginfo100 net commission after qs2', 100*qs2Value*commission, qs2UWInfoNet[1].commission

        assertEquals 'underwritinginfo200 ceded premium written after qs3', 200*layer1Net*qs3Value, qs3UWInfoCeded[0].premiumWritten, eps
        assertEquals 'underwritinginfo200 ceded commission after qs3', 200*layer1Net*qs3Value*commission, qs3UWInfoCeded[0].commission, eps
        assertEquals 'underwritinginfo200 net premium written after qs3', 200*layer1Net*(1-qs3Value), qs3UWInfoNet[0].premiumWritten, eps
        assertEquals 'underwritinginfo200 net commission after qs3', 200*layer1Net*qs3Value*commission, qs3UWInfoNet[0].commission, eps

        assertEquals 'underwritinginfo100 ceded premium written after qs3', 100*layer1Net*qs3Value, qs3UWInfoCeded[1].premiumWritten, eps
        assertEquals 'underwritinginfo100 ceded commission after qs3', 100*layer1Net*qs3Value*commission, qs3UWInfoCeded[1].commission, eps
        assertEquals 'underwritinginfo100 net premium written after qs3', 100*layer1Net*(1-qs3Value), qs3UWInfoNet[1].premiumWritten, eps
        assertEquals 'underwritinginfo100 net commission after qs3', 100*layer1Net*qs3Value*commission, qs3UWInfoNet[1].commission, eps

        assertEquals 'underwritinginfo200 ceded premium written after program', 200*totalCeded, programUWInfoCeded[0].premiumWritten
        assertEquals 'underwritinginfo200 ceded commission after program', 200*totalCeded*commission, programUWInfoCeded[0].commission
        assertEquals 'underwritinginfo200 net premium written after program', 200*totalNet, programUWInfoNet[0].premiumWritten, eps
        assertEquals 'underwritinginfo200 net commission after program', 200*totalCeded*commission, programUWInfoNet[0].commission

        assertEquals 'underwritinginfo100 ceded premium written after program', 100*totalCeded, programUWInfoCeded[1].premiumWritten
        assertEquals 'underwritinginfo100 ceded commission after program', 100*totalCeded*commission, programUWInfoCeded[1].commission
        assertEquals 'underwritinginfo100 net premium written after program', 100*totalNet, programUWInfoNet[1].premiumWritten, eps
        assertEquals 'underwritinginfo100 net commission after program', 100*totalCeded*commission, programUWInfoNet[1].commission
    }
}
