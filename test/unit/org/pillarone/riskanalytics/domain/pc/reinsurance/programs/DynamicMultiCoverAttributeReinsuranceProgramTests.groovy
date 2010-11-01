package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.util.TestPretendInChannelWired
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionTests
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.TestLobComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class DynamicMultiCoverAttributeReinsuranceProgramTests extends GroovyTestCase {

    DynamicMultiCoverAttributeReinsuranceProgram program

    SimulationScope simulationScope = new SimulationScope()

    static Map<String, TestLobComponent> createLobs(List<String> lobNames, Model model = null) {
        Map<String, TestLobComponent> lob = new HashMap()
        for (String lobName: lobNames) {
            lob.put(lobName, new TestLobComponent(name: lobName))
            if (model != null) {
                model.allComponents << lob[lobName]
            }
        }
        return lob
    }

    Map<String, TestLobComponent> lob = createLobs(['motor', 'property', 'legal'], simulationScope.model)

    Claim attrMarketClaim1000 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 1000d, fractionOfPeriod: 0d, lineOfBusiness: lob['motor'])
    Claim largeMarketClaim600 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 600d, fractionOfPeriod: 0d, lineOfBusiness: lob['motor'])

    Claim attrClaim100 = new Claim(claimType: ClaimType.ATTRITIONAL, ultimate: 100d, fractionOfPeriod: 0d, originalClaim: attrMarketClaim1000, lineOfBusiness: lob['motor'])
    Claim largeClaim60 = new Claim(claimType: ClaimType.SINGLE, ultimate: 60d, fractionOfPeriod: 0.1d, originalClaim: largeMarketClaim600, lineOfBusiness: lob['motor'])

    UnderwritingInfo underwritingInfo1 = CommissionTests.getUnderwritingInfoFromSelf(
            origin: new TestComponent(),
            premiumWritten: 2000, premiumWrittenAsIf: 2000,
            numberOfPolicies: 100, exposureDefinition: Exposure.ABSOLUTE, lineOfBusiness: lob['motor'])

    /**
     *  Contract qs2 is based on NET (default for any contract, required for first contracts at the start of a program),
     *  but then change to being based on CEDED for ensuing contracts qs3 & qs1 (a typical business case).
     *  reference implementation in https://issuetracking.intuitive-collaboration.com/jira/secure/attachment/10526/DMCARPT.xls
     */
    void testUsageSerialOrderNetCededCeded() {
        program = new DynamicMultiCoverAttributeReinsuranceProgram()
        MultiCoverAttributeReinsuranceContract quotaShare1 = ReinsuranceWithBouquetCommissionProgramTests.getQuotaShare(20, 9)
        MultiCoverAttributeReinsuranceContract quotaShare2 = ReinsuranceWithBouquetCommissionProgramTests.getQuotaShare(10, 1)
        MultiCoverAttributeReinsuranceContract quotaShare3 = ReinsuranceWithBouquetCommissionProgramTests.getQuotaShare(15, 5)
        quotaShare1.name = "qs1" // this contract is processed third (inuring priority is 9)
        quotaShare2.name = "qs2" // this contract is processed first (inuring priority is 1)
        quotaShare3.name = "qs3" // this contract is processed second (inuring priority is 5)
        quotaShare3.parmBasedOn = ReinsuranceContractBase.CEDED // should change results, also downstream for qs3
        quotaShare1.parmBasedOn = ReinsuranceContractBase.CEDED // should change results, also downstream for qs3
        program.addSubComponent(quotaShare1)
        program.addSubComponent(quotaShare2)
        program.addSubComponent(quotaShare3)

        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << underwritingInfo1

        def inUwInfoWired = new TestPretendInChannelWired(program, "inUnderwritingInfo")

        program.internalWiring()

        List qs1ClaimsCeded = (new TestProbe(quotaShare1, "outCoveredClaims")).result
        List qs2ClaimsCeded = (new TestProbe(quotaShare2, "outCoveredClaims")).result
        List qs3ClaimsCeded = (new TestProbe(quotaShare3, "outCoveredClaims")).result
        List endClaimsCeded = (new TestProbe(program, "outClaimsCeded")).result

        List qs1ClaimsNet = (new TestProbe(quotaShare1, "outUncoveredClaims")).result
        List qs2ClaimsNet = (new TestProbe(quotaShare2, "outUncoveredClaims")).result
        List qs3ClaimsNet = (new TestProbe(quotaShare3, "outUncoveredClaims")).result
        List endClaimsNet = (new TestProbe(program, "outClaimsNet")).result

        List qs1UwInfoCeded = (new TestProbe(quotaShare1, "outCoverUnderwritingInfo")).result
        List qs2UwInfoCeded = (new TestProbe(quotaShare2, "outCoverUnderwritingInfo")).result
        List qs3UwInfoCeded = (new TestProbe(quotaShare3, "outCoverUnderwritingInfo")).result
        List endUwInfoCeded = (new TestProbe(program, "outCoverUnderwritingInfo")).result

        List qs1UwInfoNet = (new TestProbe(quotaShare1, "outNetAfterCoverUnderwritingInfo")).result
        List qs2UwInfoNet = (new TestProbe(quotaShare2, "outNetAfterCoverUnderwritingInfo")).result
        List qs3UwInfoNet = (new TestProbe(quotaShare3, "outNetAfterCoverUnderwritingInfo")).result
        List endUwInfoNet = (new TestProbe(program, "outNetAfterCoverUnderwritingInfo")).result

        program.start()

        assertEquals "# quotaShare1 ceded claims", 2, qs1ClaimsCeded.size()
        assertEquals "# quotaShare2 ceded claims", 2, qs2ClaimsCeded.size()
        assertEquals "# quotaShare3 ceded claims", 2, qs3ClaimsCeded.size()
        assertEquals "# program ceded claims", 6, endClaimsCeded.size()

        assertEquals "# quotaShare1 net claims", 2, qs1ClaimsNet.size()
        assertEquals "# quotaShare2 net claims", 2, qs2ClaimsNet.size()
        assertEquals "# quotaShare3 net claims", 2, qs3ClaimsNet.size()
        assertEquals "# program net claims", 2, endClaimsNet.size()

        assertEquals "quotaShare2 ceded claim", 10, qs2ClaimsCeded[0].ultimate
        assertEquals "quotaShare2 ceded claim", 6, qs2ClaimsCeded[1].ultimate

        assertEquals "quotaShare2 net claim", 90, qs2ClaimsNet[0].ultimate
        assertEquals "quotaShare2 net claim", 54, qs2ClaimsNet[1].ultimate

        assertEquals "quotaShare3 ceded claim", 1.5, qs3ClaimsCeded[0].ultimate
        assertEquals "quotaShare3 ceded claim", 0.9, qs3ClaimsCeded[1].ultimate, 1E-12

        assertEquals "quotaShare3 net claim", 8.5, qs3ClaimsNet[0].ultimate
        assertEquals "quotaShare3 net claim", 5.1, qs3ClaimsNet[1].ultimate

        assertEquals "quotaShare1 ceded claim", 2.3, qs1ClaimsCeded[0].ultimate, 1E-12
        assertEquals "quotaShare1 ceded claim", 1.38, qs1ClaimsCeded[1].ultimate, 1E-12

        assertEquals "quotaShare1 net claim", 9.2, qs1ClaimsNet[0].ultimate
        assertEquals "quotaShare1 net claim", 5.52, qs1ClaimsNet[1].ultimate, 1E-12

        assertEquals "program ceded claim", 10, endClaimsCeded[0].ultimate
        assertEquals "program ceded claim", 6, endClaimsCeded[1].ultimate
        assertEquals "program ceded claim", 1.5, endClaimsCeded[2].ultimate
        assertEquals "program ceded claim", 0.9, endClaimsCeded[3].ultimate, 1E-12
        assertEquals "program ceded claim", 2.3, endClaimsCeded[4].ultimate, 1E-12
        assertEquals "program ceded claim", 1.38, endClaimsCeded[5].ultimate, 1E-12

        assertEquals "program net claim", 86.2, endClaimsNet[0].ultimate
        assertEquals "program net claim", 51.72, endClaimsNet[1].ultimate

        assertEquals "# quotaShare1 ceded uwinfo", 1, qs1UwInfoCeded.size()
        assertEquals "# quotaShare2 ceded uwinfo", 1, qs2UwInfoCeded.size()
        assertEquals "# quotaShare3 ceded uwinfo", 1, qs3UwInfoCeded.size()
        assertEquals "# program ceded uwinfo", 3, endUwInfoCeded.size()

        assertEquals "# quotaShare1 net uwinfo", 1, qs1UwInfoNet.size()
        assertEquals "# quotaShare2 net uwinfo", 1, qs2UwInfoNet.size()
        assertEquals "# quotaShare3 net uwinfo", 1, qs3UwInfoNet.size()
        assertEquals "# program net uwinfo", 1, endUwInfoNet.size()

        assertEquals "quotaShare2 ceded premium", 200, qs2UwInfoCeded[0].premiumWritten
        assertEquals "quotaShare2 net premium", 1800, qs2UwInfoNet[0].premiumWritten

        assertEquals "quotaShare3 ceded premium", 30, qs3UwInfoCeded[0].premiumWritten
        assertEquals "quotaShare3 net premium", 170, qs3UwInfoNet[0].premiumWritten

        assertEquals "quotaShare1 ceded premium", 46, qs1UwInfoCeded[0].premiumWritten
        assertEquals "quotaShare1 net premium", 184, qs1UwInfoNet[0].premiumWritten

        assertEquals "program ceded premium from qs2 (ip1)", 200, endUwInfoCeded[0].premiumWritten
        assertEquals "program ceded premium from qs3 (ip5)", 30, endUwInfoCeded[1].premiumWritten
        assertEquals "program ceded premium from qs1 (ip9)", 46, endUwInfoCeded[2].premiumWritten
        assertEquals "program net premium", 1724, endUwInfoNet[0].premiumWritten
    }

    /**
     *  "pathological" case: contracts change from being based on NET (falsely specified as CEDED), to CEDED, and then back to NET.
     *  reference implementation in https://issuetracking.intuitive-collaboration.com/jira/secure/attachment/10526/DMCARPT.xls
     */
    void testUsageSerialOrderNetCededNet() {
        program = new DynamicMultiCoverAttributeReinsuranceProgram()
        MultiCoverAttributeReinsuranceContract quotaShare1 = ReinsuranceWithBouquetCommissionProgramTests.getQuotaShare(20, 9)
        MultiCoverAttributeReinsuranceContract quotaShare2 = ReinsuranceWithBouquetCommissionProgramTests.getQuotaShare(10, 1)
        MultiCoverAttributeReinsuranceContract quotaShare3 = ReinsuranceWithBouquetCommissionProgramTests.getQuotaShare(15, 5)
        quotaShare1.name = "qs1" // this contract is processed third (inuring priority is 9)
        quotaShare2.name = "qs2" // this contract is processed first (inuring priority is 1)
        quotaShare3.name = "qs3" // this contract is processed second (inuring priority is 5)
        quotaShare2.parmBasedOn = ReinsuranceContractBase.CEDED // should produce warning in LOG but have no effect
        quotaShare3.parmBasedOn = ReinsuranceContractBase.CEDED // should change results, also downstream for qs3
        program.addSubComponent(quotaShare1)
        program.addSubComponent(quotaShare2)
        program.addSubComponent(quotaShare3)

        program.inClaims << attrClaim100 << largeClaim60
        program.inUnderwritingInfo << underwritingInfo1

        def inUwInfoWired = new TestPretendInChannelWired(program, "inUnderwritingInfo")

        program.internalWiring()

        List qs1ClaimsCeded = (new TestProbe(quotaShare1, "outCoveredClaims")).result
        List qs2ClaimsCeded = (new TestProbe(quotaShare2, "outCoveredClaims")).result
        List qs3ClaimsCeded = (new TestProbe(quotaShare3, "outCoveredClaims")).result
        List endClaimsCeded = (new TestProbe(program, "outClaimsCeded")).result

        List qs1ClaimsNet = (new TestProbe(quotaShare1, "outUncoveredClaims")).result
        List qs2ClaimsNet = (new TestProbe(quotaShare2, "outUncoveredClaims")).result
        List qs3ClaimsNet = (new TestProbe(quotaShare3, "outUncoveredClaims")).result
        List endClaimsNet = (new TestProbe(program, "outClaimsNet")).result

        List qs1UwInfoCeded = (new TestProbe(quotaShare1, "outCoverUnderwritingInfo")).result
        List qs2UwInfoCeded = (new TestProbe(quotaShare2, "outCoverUnderwritingInfo")).result
        List qs3UwInfoCeded = (new TestProbe(quotaShare3, "outCoverUnderwritingInfo")).result
        List endUwInfoCeded = (new TestProbe(program, "outCoverUnderwritingInfo")).result

        List qs1UwInfoNet = (new TestProbe(quotaShare1, "outNetAfterCoverUnderwritingInfo")).result
        List qs2UwInfoNet = (new TestProbe(quotaShare2, "outNetAfterCoverUnderwritingInfo")).result
        List qs3UwInfoNet = (new TestProbe(quotaShare3, "outNetAfterCoverUnderwritingInfo")).result
        List endUwInfoNet = (new TestProbe(program, "outNetAfterCoverUnderwritingInfo")).result

        program.start()

        assertEquals "# quotaShare1 ceded claims", 2, qs1ClaimsCeded.size()
        assertEquals "# quotaShare2 ceded claims", 2, qs2ClaimsCeded.size()
        assertEquals "# quotaShare3 ceded claims", 2, qs3ClaimsCeded.size()
        assertEquals "# program ceded claims", 6, endClaimsCeded.size()

        assertEquals "# quotaShare1 net claims", 2, qs1ClaimsNet.size()
        assertEquals "# quotaShare2 net claims", 2, qs2ClaimsNet.size()
        assertEquals "# quotaShare3 net claims", 2, qs3ClaimsNet.size()
        assertEquals "# program net claims", 2, endClaimsNet.size()

        assertEquals "quotaShare2 ceded claim", 10, qs2ClaimsCeded[0].ultimate
        assertEquals "quotaShare2 ceded claim", 6, qs2ClaimsCeded[1].ultimate

        assertEquals "quotaShare2 net claim", 90, qs2ClaimsNet[0].ultimate
        assertEquals "quotaShare2 net claim", 54, qs2ClaimsNet[1].ultimate

        assertEquals "quotaShare3 ceded claim", 1.5, qs3ClaimsCeded[0].ultimate
        assertEquals "quotaShare3 ceded claim", 0.9, qs3ClaimsCeded[1].ultimate, 1E-12

        assertEquals "quotaShare3 net claim", 8.5, qs3ClaimsNet[0].ultimate
        assertEquals "quotaShare3 net claim", 5.1, qs3ClaimsNet[1].ultimate

        assertEquals "quotaShare1 ceded claim", 17.7, qs1ClaimsCeded[0].ultimate
        assertEquals "quotaShare1 ceded claim", 10.62, qs1ClaimsCeded[1].ultimate, 1E-12

        assertEquals "quotaShare1 net claim", 70.8, qs1ClaimsNet[0].ultimate
        assertEquals "quotaShare1 net claim", 42.48, qs1ClaimsNet[1].ultimate, 1E-12

        assertEquals "program ceded claim", 10, endClaimsCeded[0].ultimate
        assertEquals "program ceded claim", 6, endClaimsCeded[1].ultimate
        assertEquals "program ceded claim", 1.5, endClaimsCeded[2].ultimate
        assertEquals "program ceded claim", 0.9, endClaimsCeded[3].ultimate, 1E-12
        assertEquals "program ceded claim", 17.7, endClaimsCeded[4].ultimate
        assertEquals "program ceded claim", 10.62, endClaimsCeded[5].ultimate, 1E-12

        assertEquals "program net claim", 70.8, endClaimsNet[0].ultimate
        assertEquals "program net claim", 42.48, endClaimsNet[1].ultimate

        assertEquals "# quotaShare1 ceded uwinfo", 1, qs1UwInfoCeded.size()
        assertEquals "# quotaShare2 ceded uwinfo", 1, qs2UwInfoCeded.size()
        assertEquals "# quotaShare3 ceded uwinfo", 1, qs3UwInfoCeded.size()
        assertEquals "# program ceded uwinfo", 3, endUwInfoCeded.size()

        assertEquals "# quotaShare1 net uwinfo", 1, qs1UwInfoNet.size()
        assertEquals "# quotaShare2 net uwinfo", 1, qs2UwInfoNet.size()
        assertEquals "# quotaShare3 net uwinfo", 1, qs3UwInfoNet.size()
        assertEquals "# program net uwinfo", 1, endUwInfoNet.size()

        assertEquals "quotaShare2 ceded premium", 200, qs2UwInfoCeded[0].premiumWritten
        assertEquals "quotaShare2 net premium", 1800, qs2UwInfoNet[0].premiumWritten

        assertEquals "quotaShare3 ceded premium", 30, qs3UwInfoCeded[0].premiumWritten
        assertEquals "quotaShare3 net premium", 170, qs3UwInfoNet[0].premiumWritten

        assertEquals "quotaShare1 ceded premium", 354, qs1UwInfoCeded[0].premiumWritten
        assertEquals "quotaShare1 net premium", 1416, qs1UwInfoNet[0].premiumWritten

        assertEquals "program ceded premium from qs2 (ip1)", 200, endUwInfoCeded[0].premiumWritten
        assertEquals "program ceded premium from qs3 (ip5)", 30, endUwInfoCeded[1].premiumWritten
        assertEquals "program ceded premium from qs1 (ip9)", 354, endUwInfoCeded[2].premiumWritten
        assertEquals "program net premium", 1416, endUwInfoNet[0].premiumWritten
    }
}