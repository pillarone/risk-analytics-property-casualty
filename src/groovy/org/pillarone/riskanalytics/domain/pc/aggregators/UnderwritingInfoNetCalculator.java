package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

/**
 * The underwriting info aggregator sums up the gross and ceded underwriting info and calculates the net value.
 * Compared to the UnderwritingInfoAggregator ceded out channel contains the same information as the corresponding
 * in channel. This component should be used if aggregate drill down collection has to be possible for ceded out
 * channel.<br>
 * As segmentation structure is not considered, it will be lost.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoNetCalculator extends Component {

    private PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    public void doCalculation() {
        if (inUnderwritingInfoGross.isEmpty() && !inUnderwritingInfoCeded.isEmpty()) {
            throw new IllegalStateException("UnderwritingInfoNetCalculator.onlyCededUnderwritingInfo");
        }

        UnderwritingInfo grossAggregateUnderwritingInfo = UnderwritingInfoPacketFactory.createPacket();
        UnderwritingInfo cededAggregateUnderwritingInfo = UnderwritingInfoPacketFactory.createPacket();
        if (!inUnderwritingInfoGross.isEmpty() && (isSenderWired(outUnderwritingInfoGross) || isSenderWired(outUnderwritingInfoNet))) {
            grossAggregateUnderwritingInfo = UnderwritingInfoUtilities.aggregate(inUnderwritingInfoGross);
            outUnderwritingInfoGross.add(grossAggregateUnderwritingInfo);
        }
        if (isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet)) {
            if (!inUnderwritingInfoCeded.isEmpty()) {
                cededAggregateUnderwritingInfo = UnderwritingInfoUtilities.aggregate(inUnderwritingInfoCeded);
                outUnderwritingInfoCeded.addAll(inUnderwritingInfoCeded);
            }
        }
        if ((!inUnderwritingInfoGross.isEmpty() || !inUnderwritingInfoCeded.isEmpty()) && isSenderWired(outUnderwritingInfoNet)) {
            outUnderwritingInfoNet.add(UnderwritingInfoUtilities.calculateNet(grossAggregateUnderwritingInfo, cededAggregateUnderwritingInfo));
        }
    }


    public PacketList<UnderwritingInfo> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<UnderwritingInfo> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoGross() {
        return inUnderwritingInfoGross;
    }

    public void setInUnderwritingInfoGross(PacketList<UnderwritingInfo> inUnderwritingInfoGross) {
        this.inUnderwritingInfoGross = inUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<UnderwritingInfo> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoGross() {
        return outUnderwritingInfoGross;
    }

    public void setOutUnderwritingInfoGross(PacketList<UnderwritingInfo> outUnderwritingInfoGross) {
        this.outUnderwritingInfoGross = outUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoNet() {
        return outUnderwritingInfoNet;
    }

    public void setOutUnderwritingInfoNet(PacketList<UnderwritingInfo> outUnderwritingInfoNet) {
        this.outUnderwritingInfoNet = outUnderwritingInfoNet;
    }
}