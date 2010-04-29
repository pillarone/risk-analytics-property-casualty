package org.pillarone.riskanalytics.domain.pc.claims

/**
 * @deprecated use (org.pillarone.riskanalytics.domain.pc.claims.)RiskAllocatorType.getStrategy
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class RiskAllocatorStrategyFactory {

    @Deprecated
    static IRiskAllocatorStrategy getAllocatorStrategy(RiskAllocatorType type, Map parameters) {
        RiskAllocatorType.getStrategy(type, parameters)
    }

}