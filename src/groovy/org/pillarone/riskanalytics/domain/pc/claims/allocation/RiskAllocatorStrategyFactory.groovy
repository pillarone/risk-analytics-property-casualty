package org.pillarone.riskanalytics.domain.pc.claims.allocation

/**
 * @deprecated newer version available in domain.pc.claims package
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class RiskAllocatorStrategyFactory {

    @Deprecated
    static IRiskAllocatorStrategy getAllocatorStrategy(RiskAllocatorType type, Map parameters) {
        RiskAllocatorType.getStrategy(type, parameters)
    }

}