package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoMerger extends Component {

    private PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    public void doCalculation() {
        if (inUnderwritingInfoGross.isEmpty() && !inUnderwritingInfoCeded.isEmpty()) {
            throw new IllegalStateException("Only ceded underwriting information found!");
        }

        /* The map contains the gross UwInfo as keys and the ceded as values */
        if (anyOutChannelWired()) {
            // Using a LinkedHashMap ensures that all outUwInfo* lists will be sorted according to the inUwInfoGross list.
            Map<UnderwritingInfo, UnderwritingInfo> grossMergedCededPairs = new LinkedHashMap<UnderwritingInfo, UnderwritingInfo>(inUnderwritingInfoGross.size());
            for (UnderwritingInfo grossUnderwritingInfo : inUnderwritingInfoGross) {
                if (grossMergedCededPairs.containsKey(grossUnderwritingInfo.getOriginalUnderwritingInfo())) {
                    throw new IllegalArgumentException("UnderwritingInfoMerger.inUnderwritingInfoGross contained two packets with the same origin!");
                }
                grossMergedCededPairs.put(grossUnderwritingInfo.getOriginalUnderwritingInfo(), null);
                outUnderwritingInfoGross.add(grossUnderwritingInfo);
            }
            if (isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet)) {
                for (UnderwritingInfo cededUnderwritingInfo : inUnderwritingInfoCeded) {
                    if (grossMergedCededPairs.containsKey(cededUnderwritingInfo.originalUnderwritingInfo)) {
                        UnderwritingInfo aggregateCededUnderwritingInfo = grossMergedCededPairs.get(cededUnderwritingInfo.originalUnderwritingInfo);
                        if (aggregateCededUnderwritingInfo == null) {
                            grossMergedCededPairs.put(cededUnderwritingInfo.originalUnderwritingInfo, UnderwritingInfoPacketFactory.copy(cededUnderwritingInfo));
                        }
                        else {
                            aggregateCededUnderwritingInfo.plus(cededUnderwritingInfo);
                            aggregateCededUnderwritingInfo.numberOfPolicies = cededUnderwritingInfo.numberOfPolicies;
                            aggregateCededUnderwritingInfo.origin = this;
                        }
                    }
                }

                for (Map.Entry<UnderwritingInfo, UnderwritingInfo> entry : grossMergedCededPairs.entrySet()) {
                    UnderwritingInfo grossUnderwritingInfo = entry.getKey();
                    UnderwritingInfo netUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
                    netUnderwritingInfo.origin = this;
                    netUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo;

                    UnderwritingInfo cededUnderwritingInfo = entry.getValue();
                    if (cededUnderwritingInfo == null) {
                        cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(netUnderwritingInfo);
                        UnderwritingInfoUtilities.setZero(cededUnderwritingInfo);
                    }
                    else {
                        netUnderwritingInfo.minus(cededUnderwritingInfo);
                        netUnderwritingInfo.commission = cededUnderwritingInfo.commission;
                    }
                    outUnderwritingInfoCeded.add(cededUnderwritingInfo);
                    outUnderwritingInfoNet.add(netUnderwritingInfo);
                }
            }
        }
    }

    private boolean anyOutChannelWired() {
        return isSenderWired(outUnderwritingInfoGross) || isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet);
    }

    public void setOutUnderwritingInfoNet(PacketList<UnderwritingInfo> outUnderwritingInfoNet) {
        this.outUnderwritingInfoNet = outUnderwritingInfoNet;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoNet() {
        return outUnderwritingInfoNet;
    }

    public void setOutUnderwritingInfoGross(PacketList<UnderwritingInfo> outUnderwritingInfoGross) {
        this.outUnderwritingInfoGross = outUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoGross() {
        return outUnderwritingInfoGross;
    }

    public void setOutUnderwritingInfoCeded(PacketList<UnderwritingInfo> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoGross(PacketList<UnderwritingInfo> inUnderwritingInfoGross) {
        this.inUnderwritingInfoGross = inUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoGross() {
        return inUnderwritingInfoGross;
    }

    public void setInUnderwritingInfoCeded(PacketList<UnderwritingInfo> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }
}
