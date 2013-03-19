package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"UNDERWRITING","FILTER"})
public class UnderwritingFilterByOriginalOrigin extends Component {

    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);
    private PacketList<CededUnderwritingInfo> outUnderwritingInfo = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);

    public void doCalculation() {
        outUnderwritingInfo.addAll(inUnderwritingInfoCeded);
    }

    @Override
    public void filterInChannel(PacketList inChannel, PacketList source) {
        if (inChannel == inUnderwritingInfoCeded) {
            if (source.size() > 0 ) {
                inUnderwritingInfoCeded.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByOriginalOrigin(inUnderwritingInfoGross, source));
            }
        }
        else {
            super.filterInChannel(inChannel, source);
        }
    }

    public PacketList<CededUnderwritingInfo> getOutUnderwritingInfo() {
        return outUnderwritingInfo;
    }

    public void setOutUnderwritingInfo(PacketList<CededUnderwritingInfo> outUnderwritingInfo) {
        this.outUnderwritingInfo = outUnderwritingInfo;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoGross() {
        return inUnderwritingInfoGross;
    }

    public void setInUnderwritingInfoGross(PacketList<UnderwritingInfo> inUnderwritingInfoGross) {
        this.inUnderwritingInfoGross = inUnderwritingInfoGross;
    }

    public PacketList<CededUnderwritingInfo> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }
}