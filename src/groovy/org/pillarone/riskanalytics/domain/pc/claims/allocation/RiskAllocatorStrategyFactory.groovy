package org.pillarone.riskanalytics.domain.pc.claims.allocation

import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.DistributionModified

/**
 * @deprecated newer version available in domain.pc.claims package
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class RiskAllocatorStrategyFactory {

    private static IRiskAllocatorStrategy getNone() {
        return new TrivialRiskAllocatorStrategy()
    }

    private static IRiskAllocatorStrategy getSumInsuredGenerator(RandomDistribution distribution, DistributionModified modification, double bandMean) {
        return new SumInsuredGeneratorRiskAllocatorStrategy(distribution: distribution, modification: modification, bandMean: bandMean)
    }

    private static IRiskAllocatorStrategy getRiskToBand() {
        return new RiskToBandAllocatorStrategy()
    }

    static IRiskAllocatorStrategy getAllocatorStrategy(RiskAllocatorType type, Map parameters) {
        IRiskAllocatorStrategy riskAllocator
        switch (type) {
            case RiskAllocatorType.NONE:
                riskAllocator = getNone()
                break
            case RiskAllocatorType.RISKTOBAND:
                riskAllocator = getRiskToBand()
                break
            case RiskAllocatorType.SUMINSUREDGENERATOR:
                riskAllocator = getSumInsuredGenerator(parameters["distribution"], parameters["modification"], parameters["bandMean"])
                break
        }
        return riskAllocator
    }
}