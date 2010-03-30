package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * The RiskBands class allows one to specify (in tabular format) a set of risk bands,
 * each (row) of which defines the values (columns):
 * - maximum sum insured (packet property: maxSumInsured)
 * - average sum insured (packet property: sumInsured)
 * - premium (lower limit; packet property: premiumWritten)
 * - number of policies (packet property: numberOfPolicies)
 *
 * An instance will emit outUnderwritingInfo packets, one per segment/band defined
 * (i.e. one underwriting packet for each row in the table), with the property names
 * indicated above. In addition, each packet will have a property premiumWrittenAsIf,
 * identical to premiumWritten, an origin pointing to the Riskband instance emitting the packet,
 * and a self-referential originalUnderwritingInfo property (i.e. pointing to the packet itself).
 *
 * @author martin.melchior (at) fhnw (dot) ch, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RiskBands extends Component implements IUnderwritingInfoMarker {

    AbstractMultiDimensionalParameter parmUnderwritingInformation = new TableMultiDimensionalParameter([[0d], [0d], [0d], [0d]],
            ['maximum sum insured', 'average sum insured', 'premium', 'number of policies']
    )

    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)

    public void doCalculation() {
        for (int i = 1; i < parmUnderwritingInformation.rowCount; i++) {
            UnderwritingInfo underwritingInfo = UnderwritingInfoPacketFactory.createPacket()
            // todo : safer column selection
            underwritingInfo.premiumWritten = parmUnderwritingInformation.getValueAt(i, 2)
            underwritingInfo.premiumWrittenAsIf = underwritingInfo.premiumWritten
            underwritingInfo.maxSumInsured = parmUnderwritingInformation.getValueAt(i, 0)
            underwritingInfo.sumInsured = parmUnderwritingInformation.getValueAt(i, 1)
            underwritingInfo.numberOfPolicies = parmUnderwritingInformation.getValueAt(i, 3)
            underwritingInfo.origin = this
            underwritingInfo.originalUnderwritingInfo = underwritingInfo
            outUnderwritingInfo << underwritingInfo
        }
    }
}