package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.underwriting.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The underwriting info merger calculates merged ceded underwriting info using the property original
 * underwriting info. All ceded underwriting info having the same reference for originalUnderwritingInfo will
 * be merged in one underwritingInfo object by aggregating the values and setting the
 * origin to the ClaimsMerger (<code>this</code>).<br/>
 * If the outUnderwritingInfoNet channel is connected  net underwritingInfo object are constructed
 * too. For every gross underwritingInfo a net underwritingInfo is constructed subtracting
 * the ultimate of the merged ceded underwritingInfo with the same originalUnderwritingInfo.<br/>
 * <p/>
 * <b>Usage:</b> If a gross underwritingInfo has different ways through the network of a
 * model and is ceded in different places (parallel reinsurance contracts).
 * <p/>
 * <b>Cave:</b> The component will throw an IllegalArgumentException if it
 * receives a gross claim twice.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoMerger extends Component {

    private PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCededInGrossPackets = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    public void doCalculation() {
        if (inUnderwritingInfoGross.isEmpty() && !inUnderwritingInfoCeded.isEmpty()) {
            throw new IllegalStateException("UnderwritingInfoMerger.onlyCededUnderwritingInfo");
        }

        /* The map contains the gross UwInfo as keys and the ceded as values */
        if (anyOutChannelWired()) {
            // Using a LinkedHashMap ensures that all outUwInfo* lists will be sorted according to the inUwInfoGross list.
            Map<UnderwritingInfo, CededUnderwritingInfo> grossMergedCededPairs = new LinkedHashMap<UnderwritingInfo, CededUnderwritingInfo>(inUnderwritingInfoGross.size());
            for (UnderwritingInfo grossUnderwritingInfo : inUnderwritingInfoGross) {
                if (grossMergedCededPairs.containsKey(grossUnderwritingInfo.getOriginalUnderwritingInfo())) {
                    throw new IllegalArgumentException("UnderwritingInfoMerger.doubleInformation");
                }
                grossMergedCededPairs.put(grossUnderwritingInfo.getOriginalUnderwritingInfo(), null);
                outUnderwritingInfoGross.add(grossUnderwritingInfo);
            }
            if (isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet)) {
                for (CededUnderwritingInfo cededUnderwritingInfo : inUnderwritingInfoCeded) {
                    if (grossMergedCededPairs.containsKey(cededUnderwritingInfo.getOriginalUnderwritingInfo())) {
                        CededUnderwritingInfo aggregateCededUnderwritingInfo = grossMergedCededPairs.get(cededUnderwritingInfo.getOriginalUnderwritingInfo());
                        if (aggregateCededUnderwritingInfo == null) {
                            grossMergedCededPairs.put(cededUnderwritingInfo.getOriginalUnderwritingInfo(), CededUnderwritingInfoPacketFactory.copy(cededUnderwritingInfo));
                        }
                        else {
                            aggregateCededUnderwritingInfo.plus(cededUnderwritingInfo);
                            aggregateCededUnderwritingInfo.setNumberOfPolicies(cededUnderwritingInfo.getNumberOfPolicies());
                            aggregateCededUnderwritingInfo.origin = this;
                        }
                    }
                }

                for (Map.Entry<UnderwritingInfo, CededUnderwritingInfo> entry : grossMergedCededPairs.entrySet()) {
                    UnderwritingInfo grossUnderwritingInfo = entry.getKey();
                    UnderwritingInfo netUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
                    netUnderwritingInfo.origin = this;
                    netUnderwritingInfo.setOriginalUnderwritingInfo(grossUnderwritingInfo);

                    CededUnderwritingInfo cededUnderwritingInfo = entry.getValue();
                    if (cededUnderwritingInfo == null) {
                        cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(netUnderwritingInfo);
                        CededUnderwritingInfoUtilities.setZero(cededUnderwritingInfo);
                    }
                    else {
                        netUnderwritingInfo = UnderwritingInfoUtilities.calculateNet(grossUnderwritingInfo, cededUnderwritingInfo);
                    }
                    outUnderwritingInfoCeded.add(cededUnderwritingInfo);
                    outUnderwritingInfoNet.add(netUnderwritingInfo);
                }
            }
            if (isSenderWired(outUnderwritingInfoCededInGrossPackets) && outUnderwritingInfoCeded.size() > 0) {
                for (CededUnderwritingInfo cededUnderwritingInfo : outUnderwritingInfoCeded) {
                    outUnderwritingInfoCededInGrossPackets.add(UnderwritingInfoPacketFactory.copy(cededUnderwritingInfo));
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

    public void setOutUnderwritingInfoCeded(PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public PacketList<CededUnderwritingInfo> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoGross(PacketList<UnderwritingInfo> inUnderwritingInfoGross) {
        this.inUnderwritingInfoGross = inUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoGross() {
        return inUnderwritingInfoGross;
    }

    public void setInUnderwritingInfoCeded(PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<CededUnderwritingInfo> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoCededInGrossPackets() {
        return outUnderwritingInfoCededInGrossPackets;
    }

    public void setOutUnderwritingInfoCededInGrossPackets(PacketList<UnderwritingInfo> outUnderwritingInfoCededInGrossPackets) {
        this.outUnderwritingInfoCededInGrossPackets = outUnderwritingInfoCededInGrossPackets;
    }
}
