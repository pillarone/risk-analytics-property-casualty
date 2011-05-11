package org.pillarone.riskanalytics.domain.pc.filter

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['SEGMENT','FILTER'])
class DynamicSegmentFilters extends DynamicComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    PacketList<UnderwritingInfo> inUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim)
    PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim)
    PacketList<Claim> inClaimsNet = new PacketList<Claim>(Claim)

    public void wire() {
        replicateInChannels this, 'inUnderwritingInfoGross'
        replicateInChannels this, 'inUnderwritingInfoCeded'
        replicateInChannels this, 'inUnderwritingInfoNet'
        replicateInChannels this, 'inClaimsGross'
        replicateInChannels this, 'inClaimsCeded'
        replicateInChannels this, 'inClaimsNet'
    }

    public SegmentFilter createDefaultSubComponent() {
        SegmentFilter segmentFilter = new SegmentFilter()
        return segmentFilter
    }

    public String getGenericSubComponentName() {
        return "segmentFilter"
    }
}