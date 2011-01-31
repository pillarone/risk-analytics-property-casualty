package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
class SurplusProgramTests extends GroovyTestCase {

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
                maxSumInsured: 400,
                premium: 2050)
        // third band: ceded = (400-100)/500 = 0.6
        UnderwritingInfo info2 = new UnderwritingInfo(
                numberOfPolicies: 50,
                sumInsured: 500,
                maxSumInsured: 800,
                premium: 4050)
        List<UnderwritingInfo> uwInfos = []
        uwInfos << info0 << info1 << info2
        return uwInfos
    }

    private static List<Claim> getMockAttritionalClaims() {
        List<Claim> claims = []
        claims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 50d)
        claims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 150d)
        claims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 300d)
        claims << new Claim(claimType: ClaimType.ATTRITIONAL, value: 450d)
        return claims
    }

    private static List<Claim> getMockLargeClaims() {
        List<Claim> claims = []
        claims << new Claim(claimType: ClaimType.SINGLE, value: 50d)
        claims << new Claim(claimType: ClaimType.SINGLE, value: 150d)
        claims << new Claim(claimType: ClaimType.SINGLE, value: 300d)
        claims << new Claim(claimType: ClaimType.SINGLE, value: 450d)
        return claims
    }

    void testUsage() {
        // underwriting info
        List<UnderwritingInfo> underwritingInfo = getMockUnderwritingInfo()

        // reinsurance program
        SurplusProgram surplus = new SurplusProgram()
        surplus.wire()

        // set parameters for the surplus
        surplus.subSurplus.parmContractStrategy = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.SURPLUS,
                ["retention": 100,
                 "lines": 3,
                 "commission": 0.0,
                 "defaultCededLossShare": 0d,
                 "coveredByReinsurer": 1d])
        surplus.inUnderwritingInfo.addAll getMockUnderwritingInfo()
        surplus.inClaims.addAll getMockAttritionalClaims()
        surplus.inClaims.addAll getMockLargeClaims()

        // allocator and its configuration
        surplus.subAttritionalAllocation.parmAllocationTable = new TableMultiDimensionalParameter(
                [[100d, 400d, 800d], [0.25, 0.5, 0.25]], ['maximum sum insured', 'portion']
        )
        surplus.subLargeAllocation.parmAllocationTable = new TableMultiDimensionalParameter(
                [[100d, 400d, 800d], [0.25, 0.5, 0.25]], ['maximum sum insured', 'portion']
        )

        List surplusNetClaims = (new TestProbe(surplus, "outClaimsNet")).result
        List surplusCededClaims = (new TestProbe(surplus, "outClaims")).result

        surplus.start()

        // create map with the ceded per band claims
        Map<Double, List<Claim>> mapNetAttr = [:]
        Map<Double, Double> mapAggrNetAttr = [:]
        Map<Double, List<Claim>> mapNetLarge = [:]
        Map<Double, Double> mapAggrNetLarge = [:]
        for (Claim claim: surplusNetClaims) {
            UnderwritingInfo expInfo = claim.exposure
            if (claim.claimType == ClaimType.ATTRITIONAL) {
                if (!mapNetAttr.containsKey(expInfo.maxSumInsured)) {
                    mapNetAttr[expInfo.maxSumInsured] = []
                }
                if (!mapAggrNetAttr.containsKey(expInfo.maxSumInsured)) {
                    mapAggrNetAttr[expInfo.maxSumInsured] = 0d
                }
                mapNetAttr[expInfo.maxSumInsured] << claim
                mapAggrNetAttr[expInfo.maxSumInsured] += claim.ultimate
            } else if (claim.claimType == ClaimType.SINGLE) {
                if (!mapNetLarge.containsKey(expInfo.maxSumInsured)) {
                    mapNetLarge[expInfo.maxSumInsured] = []
                }
                if (!mapAggrNetLarge.containsKey(expInfo.maxSumInsured)) {
                    mapAggrNetLarge[expInfo.maxSumInsured] = 0d
                }
                mapNetLarge[expInfo.maxSumInsured] << claim
                mapAggrNetLarge[expInfo.maxSumInsured] += claim.ultimate
            }
        }

        Map<Double, List<Claim>> mapCededAttr = [:]
        Map<Double, Double> mapAggrCededAttr = [:]
        Map<Double, List<Claim>> mapCededLarge = [:]
        Map<Double, Double> mapAggrCededLarge = [:]
        for (Claim claim: surplusCededClaims) {
            UnderwritingInfo expInfo = claim.exposure
            if (claim.claimType == ClaimType.ATTRITIONAL) {
                if (!mapCededAttr.containsKey(expInfo.maxSumInsured)) {
                    mapCededAttr[expInfo.maxSumInsured] = []
                }
                if (!mapAggrCededAttr.containsKey(expInfo.maxSumInsured)) {
                    mapAggrCededAttr[expInfo.maxSumInsured] = 0d
                }
                mapCededAttr[expInfo.maxSumInsured] << claim
                mapAggrCededAttr[expInfo.maxSumInsured] += claim.ultimate
            } else if (claim.claimType == ClaimType.SINGLE) {
                if (!mapCededLarge.containsKey(expInfo.maxSumInsured)) {
                    mapCededLarge[expInfo.maxSumInsured] = []
                }
                if (!mapAggrCededLarge.containsKey(expInfo.maxSumInsured)) {
                    mapAggrCededLarge[expInfo.maxSumInsured] = 0d
                }
                mapCededLarge[expInfo.maxSumInsured] << claim
                mapAggrCededLarge[expInfo.maxSumInsured] += claim.ultimate
            }
        }

        assertTrue 4 == mapCededAttr[100d].size()
        assertTrue 4 == mapCededAttr[400d].size()
        assertTrue 4 == mapCededAttr[800d].size()
        assertEquals "aggr ceded attr claim band 1", 250.0, mapAggrCededAttr[400d]
        assertEquals "aggr ceded attr claim band 2", 142.5, mapAggrCededAttr[800d]

        assertTrue 4 == mapNetAttr[100d].size()
        assertTrue 4 == mapNetAttr[400d].size()
        assertTrue 4 == mapNetAttr[800d].size()
        assertEquals "aggr net attr claim band 0", 225.0, mapAggrNetAttr[100d]
        assertEquals "aggr net attr claim band 1", 225.0, mapAggrNetAttr[400d]
        assertEquals "aggr net attr claim band 2", 95.0, mapAggrNetAttr[800d]

        assertTrue 1 == mapCededLarge[100d].size()
        assertTrue 2 == mapCededLarge[400d].size()
        assertTrue 1 == mapCededLarge[800d].size()
        assertEquals "aggr ceded large claim band 1", 275.0, mapAggrCededLarge[400d]
        assertEquals "aggr ceded large claim band 2", 270.0, mapAggrCededLarge[800d]

        assertTrue 1 == mapNetLarge[100d].size()
        assertTrue 2 == mapNetLarge[400d].size()
        assertTrue 1 == mapNetLarge[800d].size()
        assertEquals "aggr net large claim band 0", 50.0, mapAggrNetLarge[100d]
        assertEquals "aggr net large claim band 1", 175.0, mapAggrNetLarge[400d]
        assertEquals "aggr net large claim band 2", 180.0, mapAggrNetLarge[800d]
    }
}