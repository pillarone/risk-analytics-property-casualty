package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
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