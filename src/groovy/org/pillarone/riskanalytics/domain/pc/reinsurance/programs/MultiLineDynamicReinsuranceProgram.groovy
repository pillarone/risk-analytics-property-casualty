package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiLineReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLineDynamicReinsuranceProgram extends DynamicReinsuranceProgram {

    public MultiLineReinsuranceContract createDefaultSubComponent() {
        MultiLineReinsuranceContract contract = new MultiLineReinsuranceContract(
                parmInuringPriority: 0,
                parmContractStrategy: ReinsuranceContractStrategyFactory.getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]),
                parmCoveredLines: new ComboBoxTableMultiDimensionalParameter([''], ['Covered Lines'], LobMarker)
        )
        return contract
    }
}