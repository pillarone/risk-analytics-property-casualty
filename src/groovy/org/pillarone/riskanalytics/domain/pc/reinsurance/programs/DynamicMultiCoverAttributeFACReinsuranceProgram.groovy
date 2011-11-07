package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.domain.pc.constants.IncludeType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCoverAttributeFACReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicMultiCoverAttributeFACReinsuranceProgram extends DynamicMultiCoverAttributeReinsuranceProgram {

    public MultiCoverAttributeFACReinsuranceContract createDefaultSubComponent() {
        MultiCoverAttributeFACReinsuranceContract contract = new MultiCoverAttributeFACReinsuranceContract(
                parmInuringPriority: 0,
                parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]),
                parmCover: CoverAttributeStrategyType.getStrategy(
                        CoverAttributeStrategyType.ALL, ['reserves': IncludeType.NOTINCLUDED]))
        return contract
    }

}