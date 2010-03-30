package org.pillarone.riskanalytics.domain.pc.claims.allocation

import java.util.Map.Entry
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable

/**
 * @author martin.melchior (at) fhnw (dot) ch
 * @author Michael-Noe (at) Web (dot) de
 */
class RiskAllocatorTests extends GroovyTestCase {

    private static PacketList<UnderwritingInfo> getMockExposureData(int n) {
        // prepare mock list of exposure info
        PacketList<UnderwritingInfo> underwritingInfos = []
        for (int it = 1; it <= n; it++) {
            underwritingInfos << new UnderwritingInfo(premiumWrittenAsIf: 100d * it, numberOfPolicies: it, sumInsured: 1000d * (2 * it - 1), maxSumInsured: 1000d * 2 * it)
        }
        underwritingInfos
    }

    void testGetRiskMap() {
        List<UnderwritingInfo> underwritingInfos = getMockExposureData(5)

        Map<Double, ExposureInfo> expMap = RiskToBandAllocatorStrategy.getRiskMap(underwritingInfos)
        assertEquals(underwritingInfos.size(), expMap.size())
        for (int it = 1; it <= underwritingInfos.size(); it++) {
            assertTrue(expMap.containsKey(1000d * 2 * it))
            assertEquals(underwritingInfos[it - 1], expMap[1000d * 2 * it])
        }

        // test for overlapping bands leading to exceptions
        underwritingInfos << new ExposureInfo(premiumWrittenAsIf: 120d * 2, numberOfPolicies: 10, sumInsured: 1000d * (2 * 2 - 1), maxSumInsured: 1000d * 2 * 2)
        try {
            expMap = RiskToBandAllocatorStrategy.getRiskMap(underwritingInfos)
            fail()
        } catch (Exception ex) {
            // fine
        }
    }


    void testAllocateAttritionalClaims() {
        int n = 3;
        PacketList<UnderwritingInfo> underwritingInfos = getMockExposureData(n)
        List<Double> si = []
        List<Double> weight = []
        for (UnderwritingInfo exp: underwritingInfos) {
            si << exp.maxSumInsured
            weight << 1d / n
        }

        double value = 1000d
        PacketList<Claim> inClaims = []
        inClaims << new Claim(claimType: ClaimType.ATTRITIONAL, value: value)

        TableMultiDimensionalParameter table = new TableMultiDimensionalParameter(
            [si, weight], ['maximum sum insured', 'portion']
        )
        AllocationTable allocationTable = new AllocationTable(table: table)
        RiskAllocator allocator = new RiskAllocator(
            parmRiskAllocatorStrategy: RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.RISKTOBAND, [:]),
            inClaims: inClaims,
            inUnderwritingInfo: underwritingInfos)
        allocator.inTargetDistribution << allocationTable
        allocator.doCalculation()
        PacketList<Claim> outClaims = allocator.outClaims
        assertNotNull(outClaims)
        assertEquals(n, outClaims.size())
        for (int i = 0; i < n; i++) {
            Claim claim = outClaims[i]
            assertEquals(value / n, claim.ultimate)
            assertNotNull(claim.exposure)
            assertEquals(claim.exposure, underwritingInfos[i])
        }
    }

    void testAllocateLargeClaims() {
        int n = 10
        PacketList<UnderwritingInfo> underwritingInfos = getMockExposureData(n)
        List<Double> si = []
        List<Double> weight = []
        for (UnderwritingInfo exp: underwritingInfos) {
            si << exp.maxSumInsured
            weight << 1.0 / n
        }

        // generate the claims: # claims per band increasing with the band id
        int multiplier = 50
        PacketList<Claim> inClaims = getClaimsIncr(underwritingInfos, multiplier)
        int numOfClaims = inClaims.size()

        // do the allocation
        TableMultiDimensionalParameter table = new TableMultiDimensionalParameter(
            [si, weight], ['maximum sum insured', 'portion']
        )
        AllocationTable allocationTable = new AllocationTable(table: table)
        RiskAllocator allocator = new RiskAllocator(
            parmRiskAllocatorStrategy: RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.RISKTOBAND, [:]),
            inClaims: inClaims,
            inUnderwritingInfo: underwritingInfos)
        allocator.inTargetDistribution << allocationTable
        allocator.doCalculation()

        // test the allocation
        assertTrue(testAllocation(allocator.outClaims, allocationTable.getMap('maximum sum insured', 'portion'), inClaims))

        // generate the claims: # claims per band decreasing with the band id
        inClaims = getClaimsDecr(underwritingInfos, multiplier)
        numOfClaims = inClaims.size()

        // do the allocation
        allocator = new RiskAllocator(
            parmRiskAllocatorStrategy: RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.RISKTOBAND, [:]),
            inClaims: inClaims,
            inUnderwritingInfo: underwritingInfos)
        allocator.inTargetDistribution << allocationTable
        allocator.doCalculation()

        // test the allocation
        PacketList<Claim> outClaims = allocator.outClaims
        assertFalse(testAllocation(outClaims, allocationTable.getMap('maximum sum insured', 'portion'), inClaims))
        Map<Double, Double> actualDistribution = [:]
        for (int k = n; k > 0; k--) {
            double maxSI = underwritingInfos[k - 1].maxSumInsured
            actualDistribution[maxSI] = k * multiplier / numOfClaims
        }
        assertTrue(testAllocation(outClaims, actualDistribution, inClaims))
    }

    // generate claims: Lower bands have more claims than higher bands
    PacketList<Claim> getClaimsIncr(List<ExposureInfo> underwritingInfos, int multiplier) {
        int n = underwritingInfos.size()
        int numOfClaims = multiplier * n * (n + 1) / 2
        PacketList<Claim> inClaims = []
        for (int k = n; k > 0; k--) {
            ExposureInfo exposure = underwritingInfos[n - k]
            double value = 0.5d * (exposure.sumInsured + exposure.maxSumInsured)
            for (int j = 0; j < k * multiplier; j++) {
                inClaims << new Claim(claimType: ClaimType.SINGLE, value: value)
            }
        }
        return inClaims
    }

    // generate claims: Lower bands have less claims than higher bands
    PacketList<Claim> getClaimsDecr(List<ExposureInfo> underwritingInfos, int multiplier) {
        int n = underwritingInfos.size()
        int numOfClaims = multiplier * n * (n + 1) / 2
        PacketList<Claim> inClaims = []
        for (int k = n; k > 0; k--) {
            ExposureInfo exposure = underwritingInfos[k - 1]
            double value = 0.5d * (exposure.sumInsured + exposure.maxSumInsured)
            for (int j = 0; j < k * multiplier; j++) {
                inClaims << new Claim(claimType: ClaimType.SINGLE, value: value)
            }
        }
        return inClaims
    }

    boolean testAllocation(List<Claim> outClaims, Map<Double, Double> targetDistribution, List<Claim> inClaims) {
        assertNotNull(outClaims)
        int numOfClaims = inClaims.size()
        assertEquals(numOfClaims, outClaims.size())
        Map<Double, Integer> frequencyDistr = [:]
        for (int i = 0; i < numOfClaims; i++) {
            Claim claim = outClaims[i]
            assertNotNull(claim.exposure)
            double maxSI = claim.exposure.maxSumInsured
            if (!frequencyDistr.containsKey(maxSI)) {
                frequencyDistr[maxSI] = 0
            }
            frequencyDistr[maxSI] += 1
        }

        // test whether the resulting distribution is sufficiently close to the target
        // todo: revise - originally a stricter version was in use...
        double diff = 0;
        for (Entry<Double, Double> entry: targetDistribution.entrySet()) {
            Double maxSI = entry.key
            int expected = Math.round(entry.value * numOfClaims)
            diff += Math.abs(expected - frequencyDistr[maxSI])
        }
        return diff / numOfClaims < 0.005
    }
}