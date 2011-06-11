package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory as PRC

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiLineReinsuranceContractWithDefault
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLineDynamicReinsuranceProgramWithDefault extends DynamicReinsuranceProgram {

    PacketList<ReinsurerDefault> inReinsurersDefault = new PacketList(ReinsurerDefault)

    public MultiLineReinsuranceContractWithDefault createDefaultSubComponent() {
        MultiLineReinsuranceContractWithDefault contract = new MultiLineReinsuranceContractWithDefault(
                parmInuringPriority: 0,
                parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]),
                parmCoveredLines: new ComboBoxTableMultiDimensionalParameter([''], ['Covered Segments'], ISegmentMarker)
        )
        return contract
    }

    public void wire() {
        super.wire()
        int numberOfContracts = componentList.size()
        for (int i = 0; i < numberOfContracts; i++) {
            doWire PRC, getContract(i), 'inReinsurersDefault', this, 'inReinsurersDefault'
        }
    }
}