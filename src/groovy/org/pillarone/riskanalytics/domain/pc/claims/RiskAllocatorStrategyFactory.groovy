package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.DistributionModified

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RiskAllocatorStrategyFactory {

    private static IRiskAllocatorStrategy getNone() {
        return new TrivialRiskAllocatorStrategy()
    }

    private static IRiskAllocatorStrategy getSumInsuredGenerator(RandomDistribution distribution, DistributionModified modification, double bandMean) {
        return new SumInsuredGeneratorRiskAllocatorStrategy(distribution: distribution, modification: modification, bandMean: bandMean)
    }

    private static IRiskAllocatorStrategy getRiskToBand(RiskBandAllocationBaseLimited allocationBase) {
        return new RiskToBandAllocatorStrategy(allocationBase: allocationBase)
    }

    static IRiskAllocatorStrategy getAllocatorStrategy(RiskAllocatorType type, Map parameters) {
        IRiskAllocatorStrategy riskAllocator
        switch (type) {
            case RiskAllocatorType.NONE:
                riskAllocator = getNone()
                break
            case RiskAllocatorType.RISKTOBAND:
                riskAllocator = getRiskToBand(parameters["allocationBase"])
                break
            case RiskAllocatorType.SUMINSUREDGENERATOR:
                riskAllocator = getSumInsuredGenerator(parameters["distribution"], parameters["modification"], parameters["bandMean"])
                break
        }
        return riskAllocator
    }
}