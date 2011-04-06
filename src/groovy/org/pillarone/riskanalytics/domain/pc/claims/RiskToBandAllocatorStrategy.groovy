package org.pillarone.riskanalytics.domain.pc.claims

import java.util.Map.Entry
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */

// TODO event claims not (yet) considered
// TODO discuss whether for the attritional losses it would be sufficient to first do an aggregation
// TODO discuss: the order of the outclaims is no longer the same as the claims!
class RiskToBandAllocatorStrategy extends AbstractParameterObject implements IRiskAllocatorStrategy {

    RiskBandAllocationBaseLimited allocationBase = RiskBandAllocationBaseLimited.PREMIUM

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.RISKTOBAND
    }

    public Map getParameters() {
        ['allocationBase': allocationBase]
    }

    public PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<UnderwritingInfo> underwritingInfos) {
        // get risk map
        Map<Double, UnderwritingInfo> riskMap = getRiskMap(underwritingInfos)

        // allocate the claims
        Map<Double, Double> targetDistributionMaxSI = getTargetDistribution(underwritingInfos, allocationBase)
        targetDistributionMaxSI = convertKeysToDouble(targetDistributionMaxSI)

        PacketList<Claim> allocatedClaims = new PacketList(Claim)
        if (targetDistributionMaxSI) {
            Map<Double, List<Claim>> largeClaimsAllocation = allocateSingleClaims(
                    filterClaimsByType(claims, ClaimType.SINGLE), riskMap, targetDistributionMaxSI)
            for (Entry<Double, List<Claim>> entry: largeClaimsAllocation.entrySet()) {
                UnderwritingInfo exposure = riskMap[entry.key]
                for (Claim claim: entry.value) {
                    if (claim.hasExposureInfo()) throw new IllegalArgumentException("RiskToBandAllocatorStrategy.impossibleExposureRemap")
                    claim.exposure = exposure
                    allocatedClaims << claim
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

    private List<Claim> getAllocatedClaims(List<Claim> claims, ClaimType claimType, Map<Double, UnderwritingInfo> riskMap, Map<Double, Double> targetDistribution) {
        Map<Double, List<Claim>> aggrAllocation = allocateClaims(
                filterClaimsByType(claims, claimType), riskMap, targetDistribution
        )
        List<Claim> allocatedClaims = new ArrayList<Claim>(aggrAllocation.size());
        for (Entry<Double, List<Claim>> entry: aggrAllocation.entrySet()) {
            UnderwritingInfo exposure = riskMap[entry.key]
            for (Claim claim: entry.value) {
//                if (claim.hasExposureInfo()) throw new IllegalArgumentException("RiskToBandAllocatorStrategy.impossibleExposureRemap")
                claim.exposure = exposure
                allocatedClaims << claim
            }
        }
        return allocatedClaims
    }

    // todo(sku): refactor and simplify and re-enable custom and loss probability (idea: add property map in packet tUnderwritingInfo)
    private static Map<Double, Double> getTargetDistribution(List<UnderwritingInfo> underwritingInfos, RiskBandAllocationBaseLimited allocationBase) {
        Map<Double, Double> targetDistribution = [:]
        for (UnderwritingInfo underwritingInfo: underwritingInfos) {
            if (allocationBase.is(RiskBandAllocationBaseLimited.NUMBER_OF_POLICIES)) {
                targetDistribution.put(underwritingInfo.getMaxSumInsured(), underwritingInfo.getNumberOfPolicies())
            }
            else if (allocationBase.is(RiskBandAllocationBaseLimited.PREMIUM)) {
                targetDistribution.put(underwritingInfo.getMaxSumInsured(), underwritingInfo.getPremium())
            }
        }
        Double sum = (Double) targetDistribution.values().sum()
        for (Entry<Double, Double> entry: targetDistribution.entrySet()) {
            entry.value /= sum
        }
        return targetDistribution
    }

    /**
     *  This function is temporarily needed as the db returns BigDecimal and the GUI may return
     *  Integer values and the map works properly only with Double keys. Once we can ensure that
     *  we always get double values, it will be obsolete.
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

    private Map<Double, List<Claim>> allocateClaims(List<Claim> claims,
                                                    Map<Double, UnderwritingInfo> riskMap,
                                                    Map<Double, Double> targetDistribution) {
        Map<Double, List<Claim>> lossToRiskMap = [:]
        for (Claim claim: claims) {
            for (Entry<Double, Double> entry: targetDistribution.entrySet()) {
                Claim copy = claim.copy()
                copy.originalClaim = copy
                copy.scale(entry.value)
                if (!lossToRiskMap.containsKey(entry.key)) {
                    lossToRiskMap[entry.key] = new ArrayList<Claim>()
                }
                lossToRiskMap[entry.key] << copy
            }
        }
        lossToRiskMap
    }

    private Map<Double, List<Claim>> allocateSingleClaims(List<Claim> claims,
                                                          Map<Double, UnderwritingInfo> riskMap,
                                                          Map<Double, Double> targetDistribution) {
        // initial allocation of the losses: just consider the bounds given in the risk map
        Map<Double, List<Claim>> lossToRiskMap = [:]
        Map<Double, Integer> lossCounts = [:]
        List<Double> upperBounds = riskMap.keySet().sort()
        int numOfBands = riskMap.size()
        for (Claim claim: claims) {
            int index = Collections.binarySearch(upperBounds, claim.getUltimate())
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
    private static Map<Double, UnderwritingInfo> getRiskMap(List<UnderwritingInfo> exposures) {
        Map<Double, UnderwritingInfo> riskMap = [:]
        for (UnderwritingInfo exposure: exposures) {
            double maxSumInsured = exposure.maxSumInsured
            if (maxSumInsured > 0) {
                if (riskMap.containsKey(maxSumInsured)) {
                    throw new IllegalStateException("RiskToBandAllocatorStrategy.noDisjointRiskBands")
                }
                riskMap[maxSumInsured] = exposure
            }
        }
        return riskMap
    }

    private static List<Claim> filterClaimsByType(List<Claim> claims, ClaimType claimType) {
        List<Claim> claimsToAllocate = []
        for (Claim claim: claims) {
            if (claim.claimType == claimType) {
                claimsToAllocate << claim
            }
        }
        return claimsToAllocate
    }
}
