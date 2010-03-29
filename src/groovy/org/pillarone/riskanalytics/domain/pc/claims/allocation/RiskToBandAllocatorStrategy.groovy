package org.pillarone.riskanalytics.domain.pc.claims.allocation

import java.util.Map.Entry
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable
import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.claims.ClaimWithExposure
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @deprecated newer version available in domain.pc.claims package
 *
 * @author martin.melchior (at) fhnw (dot) ch
 */

// TODO event claims not (yet) considered
// TODO discuss whether for the attritional losses it would be sufficient to first do an aggregation
// TODO discuss: the order of the outclaims is no longer the same as the claims!
@Deprecated
class RiskToBandAllocatorStrategy implements IRiskAllocatorStrategy, IParameterObject {

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.RISKTOBAND
    }

    public Map getParameters() {
        [:]
    }

    public PacketList<UnderwritingInfo> updateSumInsured(List<UnderwritingInfo> underwritingInfos) {
        return underwritingInfos
    }

    public PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<AllocationTable> targetDistribution, List<UnderwritingInfo> underwritingInfos) {
        assert targetDistribution != null

        // get risk map
        Map<Double, ExposureInfo> riskMap = getRiskMap(underwritingInfos)

        // allocate the claims  *************************
        Map<Double, Double> targetDistributionMaxSI = targetDistribution?.get(0).getMap('maximum sum insured', 'portion')
        targetDistributionMaxSI = convertKeysToDouble(targetDistributionMaxSI)

        PacketList<Claim> allocatedClaims = new PacketList(Claim)
        if (targetDistributionMaxSI) {
            Map<Double, List<Claim>> largeClaimsAllocation = allocateLargeClaims(
                    filterClaimsByType(claims, ClaimType.SINGLE), riskMap, targetDistributionMaxSI)
            for (Entry<Double, List<Claim>> entry: largeClaimsAllocation.entrySet()) {
                ExposureInfo expInfo = riskMap[entry.key]
                for (Claim claim: entry.value) {
                    ClaimWithExposure claimExt = ClaimUtilities.getClaimWithExposure(claim)
                    claimExt.exposure = expInfo
                    allocatedClaims << claimExt
                }
            }

            allocatedClaims.addAll(getAllocatedClaims(claims, ClaimType.ATTRITIONAL, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(getAllocatedClaims(claims, ClaimType.EVENT, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(getAllocatedClaims(claims, ClaimType.AGGREGATED, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(getAllocatedClaims(claims, ClaimType.AGGREGATED_ATTRITIONAL, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(getAllocatedClaims(claims, ClaimType.AGGREGATED_EVENT, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(getAllocatedClaims(claims, ClaimType.AGGREGATED_SINGLE, riskMap, targetDistributionMaxSI))
        }
        return allocatedClaims
    }

    private List<Claim> getAllocatedClaims(List<Claim> claims, ClaimType claimType, Map<Double, ExposureInfo> riskMap, Map<Double, Double> targetDistribution) {
        Map<Double, List<ClaimWithExposure>> aggrEventClaimsAllocation = allocateClaims(
                filterClaimsByType(claims, claimType), riskMap, targetDistribution
        )
        List<Claim> allocatedClaims = new ArrayList<Claim>(aggrEventClaimsAllocation.size());
        for (Entry<Double, List<ClaimWithExposure>> entry: aggrEventClaimsAllocation.entrySet()) {
            ExposureInfo expInfo = riskMap[entry.key]
            for (ClaimWithExposure claim: entry.value) {
                claim.exposure = expInfo
                allocatedClaims << claim
            }
        }
        return allocatedClaims
    }

    /**
     *  This function is temporarily needed as the db returns BigDecimal and the GUI may return
     *  Integer values and the map works only porperly with Double keys. Once we can make sure
     *  to get double values it will be obsolet.
     */
    // todo: try to remove this function
    private Map<Double, Double> convertKeysToDouble(Map<Double, Double> targetDistributionMaxSI) {
        boolean keysAreDoubles = targetDistributionMaxSI.keySet().iterator().next() instanceof Double
        if (!keysAreDoubles) {
            Map<Double, Double> mapWithConvertedKeys = [:]
            targetDistributionMaxSI.each {key, value ->
                mapWithConvertedKeys.put(key.toDouble(), value)
            }
            targetDistributionMaxSI = mapWithConvertedKeys
        }
        return targetDistributionMaxSI
    }

    private Map<Double, List<ClaimWithExposure>> allocateClaims(List<Claim> claims,
                                                                Map<Double, ExposureInfo> riskMap,
                                                                Map<Double, Double> targetDistribution) {
        Map<Double, List<ClaimWithExposure>> lossToRiskMap = [:]
        for (Claim claim: claims) {
            for (Entry<Double, Double> entry: targetDistribution.entrySet()) {
                ClaimWithExposure claimExt = ClaimUtilities.getClaimWithExposure(claim)
                claimExt.value *= entry.value
                claimExt.exposure = riskMap[entry.key]
                if (!lossToRiskMap.containsKey(entry.key)) {
                    lossToRiskMap[entry.key] = new ArrayList<ClaimWithExposure>()
                }
                lossToRiskMap[entry.key] << claimExt
            }
        }
        lossToRiskMap
    }

    private Map<Double, List<Claim>> allocateLargeClaims(List<Claim> claims,
                                                         Map<Double, ExposureInfo> riskMap,
                                                         Map<Double, Double> targetDistribution) {
        // initial allocation of the losses: just consider the bounds given in the risk map
        Map<Double, List<Claim>> lossToRiskMap = [:]
        Map<Double, Integer> lossCounts = [:]
        List<Double> upperBounds = riskMap.keySet().sort()
        int numOfBands = riskMap.size()
        for (Claim claim: claims) {
            int index = Collections.binarySearch(upperBounds, claim.ultimate)
            if (index < 0) index = -index - 1
            double bound = upperBounds.get(Math.min(index, numOfBands - 1))
            if (!lossToRiskMap[bound]) lossToRiskMap[bound] = new ArrayList<Claim>()
            lossToRiskMap[bound] << claim
            if (!lossCounts[bound]) lossCounts[bound] = 0
            lossCounts[bound] += 1
        }
        int numOfClaims = claims.size()

        // compute the delta between the initial and the desired:
        List<Double> toMove = []
        for (int k = 0; k < numOfBands; k++) {
            double bound = upperBounds[k]
            if (!lossCounts.containsKey(bound)) {
                lossCounts[bound] = 0
                lossToRiskMap[bound] = new ArrayList<Claim>()
            }
            toMove[k] = lossCounts[bound] - numOfClaims * targetDistribution[bound]
        }

        // iteratively go through the bands starting at the upper end and reallocate the losses:
        for (int k = numOfBands - 2; k >= 0; k--) {
            double giveAway = Math.max(toMove[k], 0);
            List<Double> targets = []
            double factor = 0d
            for (int j = k + 1; j < numOfBands; j++) {
                double x = -Math.min(toMove[j], 0)
                targets << x
                factor += x
            }

            // normalization factor
            factor = Math.min(factor, giveAway) / factor

            for (int j = k + 1; j < numOfBands; j++) {
                int delta = Math.min(Math.round(factor * targets[j - (k + 1)]), toMove[k])
                if (delta > 0) {
                    toMove[k] -= delta
                    List<Claim> lossList = lossToRiskMap[upperBounds[k]]
                    List<Claim> extracted = lossList[-1..-delta]
                    List<Claim> targetList = lossToRiskMap[upperBounds[j]]
                    for (Claim claim: extracted) {
                        lossList.remove(claim)
                        toMove[j] += 1
                        targetList.add(claim)
                    }
                }
            }
        }
        return lossToRiskMap
    }

    /**
     * Populate a map with the upper boundaries of the risk bands as keys
     */
    private static Map<Double, ExposureInfo> getRiskMap(List<ExposureInfo> exposures) {
        Map<Double, ExposureInfo> riskMap = [:]
        for (ExposureInfo exposure: exposures) {
            double maxSumInsured = exposure.maxSumInsured
            if (maxSumInsured > 0) {
                if (riskMap.containsKey(maxSumInsured)) {
                    throw new IllegalStateException("List with the exposure infos seems not to lead to disjoint risk bands.")
                }
                riskMap[maxSumInsured] = exposure
            }
        }
        return riskMap
    }

    private static List<Claim> filterClaimsByType(List<Claim> claims, ClaimType claimType) {
        List<Claim> claimsToAllocate = []
        claims.each {Claim claim ->
            if (claim.claimType == claimType) {
                claimsToAllocate << claim
            }
        }
        return claimsToAllocate
    }
}