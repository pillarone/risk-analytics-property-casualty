package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBandsTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RiskToBandAllocatorStrategyTests extends GroovyTestCase {

    void testUsage() {
        RiskToBandAllocatorStrategy strategy = new RiskToBandAllocatorStrategy()
        List<Claim> claims = []
        Claim claimFire100 = new Claim(claimType: ClaimType.ATTRITIONAL, value: 100d, fractionOfPeriod: 0.2)
        Claim claimHull60 = new Claim(claimType: ClaimType.SINGLE, value: 60d, fractionOfPeriod: 0.3)
        Claim claimLegal200 = new Claim(claimType: ClaimType.SINGLE, value: 200d, fractionOfPeriod: 0.1)
        claims << claimFire100 << claimHull60 << claimLegal200
        List<UnderwritingInfo> underwritingInfos = []
        underwritingInfos.addAll RiskBandsTests.getUnderwritingInfos()
        PacketList<Claim> allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)

        assertEquals '#packets', 5, allocatedClaims.size()
    }

    private static PacketList<UnderwritingInfo> getMockExposureData(int n) {
        // prepare mock list of exposure info
        PacketList<UnderwritingInfo> underwritingInfos = []
        for (int i = 1; i <= n; i++) {
            underwritingInfos << new UnderwritingInfo(
                    premium: 100d * i,
                    numberOfPolicies: i,
                    sumInsured: 1000d * (2 * i - 1),
                    maxSumInsured: 1000d * 2 * i)
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
        double totalPremium = underwritingInfos.premium.sum()

        double value = 1000d
        PacketList<Claim> claims = []
        claims << new Claim(claimType: ClaimType.ATTRITIONAL, value: value)

        RiskToBandAllocatorStrategy strategy = new RiskToBandAllocatorStrategy()
        PacketList<Claim> allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)

        assertNotNull(allocatedClaims)
        assertEquals(n, allocatedClaims.size())
        for (int i = 0; i < n; i++) {
            Claim claim = allocatedClaims[i]
            assertEquals "ultimate $i", value * underwritingInfos[i].premium / totalPremium, claim.ultimate
            assertNotNull "exposure $i not null", claim.exposure
            assertEquals "exposure $i", claim.exposure, underwritingInfos[i]
        }
    }

    // todo(sku): verify allocation of single claims
    void testAllocateLargeIncreasingClaims() {
        int n = 10
        PacketList<UnderwritingInfo> underwritingInfos = getMockExposureData(n)

        // generate the claims: # claims per band increasing with the band id
        int multiplier = 50
        PacketList<Claim> claims = getClaimsIncr(underwritingInfos, multiplier)
        int numOfClaims = claims.size()

        RiskToBandAllocatorStrategy strategy = new RiskToBandAllocatorStrategy()
        PacketList<Claim> allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)

        assertEquals '#packets incr', numOfClaims, allocatedClaims.size()


        // generate the claims: # claims per band decreasing with the band id
        claims = getClaimsDecr(underwritingInfos, multiplier)
        numOfClaims = claims.size()

        // test the allocation
        allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)
        assertEquals '#packets decr', numOfClaims, allocatedClaims.size()
    }

    // generate claims: Lower bands have more claims than higher bands
    PacketList<Claim> getClaimsIncr(List<UnderwritingInfo> underwritingInfos, int multiplier) {
        int n = underwritingInfos.size()
        int numOfClaims = multiplier * n * (n + 1) / 2
        PacketList<Claim> claims = []
        for (int k = n; k > 0; k--) {
            UnderwritingInfo exposure = underwritingInfos[n - k]
            double value = 0.5d * (exposure.sumInsured + exposure.maxSumInsured)
            for (int j = 0; j < k * multiplier; j++) {
                claims << new Claim(claimType: ClaimType.SINGLE, value: value)
            }
        }
        return claims
    }

    // generate claims: Lower bands have less claims than higher bands
    PacketList<Claim> getClaimsDecr(List<UnderwritingInfo> underwritingInfos, int multiplier) {
        int n = underwritingInfos.size()
        int numOfClaims = multiplier * n * (n + 1) / 2
        PacketList<Claim> claims = []
        for (int k = n; k > 0; k--) {
            UnderwritingInfo exposure = underwritingInfos[k - 1]
            double value = 0.5d * (exposure.sumInsured + exposure.maxSumInsured)
            for (int j = 0; j < k * multiplier; j++) {
                claims << new Claim(claimType: ClaimType.SINGLE, value: value)
            }
        }
        return claims
    }
}