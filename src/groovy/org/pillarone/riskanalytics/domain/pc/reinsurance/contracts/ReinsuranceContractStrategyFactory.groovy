package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

/**
 * @deprecated access directly the method getStrategy in RCT
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class ReinsuranceContractStrategyFactory {

    static IReinsuranceContractStrategy getContractStrategy(ReinsuranceContractType type, Map parameters) {
        return ReinsuranceContractType.getStrategy(type, parameters)
    }

}