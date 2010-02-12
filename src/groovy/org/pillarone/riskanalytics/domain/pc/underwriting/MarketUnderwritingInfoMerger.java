package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MarketUnderwritingInfoMerger extends Component {

    private PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    public void doCalculation() {
        if (inUnderwritingInfoGross.isEmpty() && !inUnderwritingInfoCeded.isEmpty()) {
            throw new IllegalStateException("Only ceded underwriting info found!");
        }

        /* The map contains the gross claims as keys and the ceded as values */
        if (anyOutChannelWired()) {
            // By using a LinkedHashMap, we can be sure, that all outClaims* lists will be sorted according
            // to the inUnderwritingInfoGross list.
            Map<UnderwritingInfo, GrossCededUnderwritingInfoPair> grossMergedCededPairs = new LinkedHashMap<UnderwritingInfo, GrossCededUnderwritingInfoPair>(inUnderwritingInfoGross.size());
            for (UnderwritingInfo grossUnderwritingInfo : inUnderwritingInfoGross) {
                GrossCededUnderwritingInfoPair underwritingInfoPair = new GrossCededUnderwritingInfoPair(grossUnderwritingInfo);
                if (grossMergedCededPairs.containsKey(grossUnderwritingInfo.originalUnderwritingInfo)) {
                    throw new IllegalArgumentException("MarketUnderwritingInfoMerger.inUnderwritingInfoGross contains twice the same underwriting info!");
                }
                grossMergedCededPairs.put(grossUnderwritingInfo.originalUnderwritingInfo, underwritingInfoPair);
                outUnderwritingInfoGross.add(grossUnderwritingInfo);
            }
            if (isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet)) {
                for (UnderwritingInfo cededUnderwritingInfo : inUnderwritingInfoCeded) {
                    if (grossMergedCededPairs.containsKey(cededUnderwritingInfo.originalUnderwritingInfo)) {
                        GrossCededUnderwritingInfoPair underwritingInfoPair = grossMergedCededPairs.get(cededUnderwritingInfo.originalUnderwritingInfo);
                        UnderwritingInfo aggregateCededUnderwritingInfo = underwritingInfoPair.getUnderwritingInfoCeded();
                        if (aggregateCededUnderwritingInfo == null) {
                            underwritingInfoPair.setUnderwritingInfoCeded(UnderwritingInfoPacketFactory.copy(cededUnderwritingInfo));
                        }
                        else {
                            aggregateCededUnderwritingInfo = aggregateCededUnderwritingInfo.plus(cededUnderwritingInfo);
                            aggregateCededUnderwritingInfo.numberOfPolicies = cededUnderwritingInfo.numberOfPolicies;
                            aggregateCededUnderwritingInfo.origin = this;
                        }
                    }
                }

                for (Map.Entry<UnderwritingInfo, GrossCededUnderwritingInfoPair> entry : grossMergedCededPairs.entrySet()) {
                    UnderwritingInfo grossUnderwritingInfo = entry.getValue().getUnderwritingInfoGross();
                    UnderwritingInfo netUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
                    netUnderwritingInfo.origin = this;
                    netUnderwritingInfo.originalUnderwritingInfo = entry.getKey();

                    UnderwritingInfo cededUnderwritingInfo = entry.getValue().getUnderwritingInfoCeded();
                    if (cededUnderwritingInfo == null) {
                        cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(netUnderwritingInfo);
                        UnderwritingInfoUtilities.setZero(cededUnderwritingInfo);
                    }
                    else {
                        netUnderwritingInfo = netUnderwritingInfo.minus(cededUnderwritingInfo);
                        netUnderwritingInfo.commission = cededUnderwritingInfo.commission;
                        netUnderwritingInfo.numberOfPolicies = grossUnderwritingInfo.numberOfPolicies;
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