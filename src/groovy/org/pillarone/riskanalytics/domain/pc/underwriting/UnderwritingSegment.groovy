package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): this component has a none linear effect on simulation time.
class UnderwritingSegment extends Component implements IUnderwritingInfoMarker {

    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)

    double parmPricePerExposureUnit = 0
    double parmWrittenExposure = 0
    Exposure parmExposureDefinition = Exposure.ABSOLUTE

    public void doCalculation() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoPacketFactory.createPacket()
        underwritingInfo.premium = parmWrittenExposure * parmPricePerExposureUnit
        underwritingInfo.numberOfPolicies = parmExposureDefinition == Exposure.NUMBER_OF_POLICIES ? parmWrittenExposure : Double.NaN
        underwritingInfo.exposureDefinition = parmExposureDefinition
        underwritingInfo.origin = this
        outUnderwritingInfo << underwritingInfo
    }
}