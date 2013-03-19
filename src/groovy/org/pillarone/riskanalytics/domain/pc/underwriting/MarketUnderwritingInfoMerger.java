package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * use the originalUnderwritingInfo property from each (gross in-) UI packet as unique identifier for merging
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"UNDERWRITING","MERGER"})
public class MarketUnderwritingInfoMerger extends Component {

    private PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCededInGrossPackets = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    public void doCalculation() {
        if (inUnderwritingInfoGross.isEmpty() && !inUnderwritingInfoCeded.isEmpty()) {
            throw new IllegalStateException("MarketUnderwritingInfoMerger.onlyCededUnderwritingInfo");
        }

        /* The map contains the gross UwInfo as keys and the ceded as values */
        if (anyOutChannelWired()) {
            // Using a LinkedHashMap ensures that all outUwInfo* lists will be sorted according to the inUwInfoGross list.
            Map<UnderwritingInfo, GrossCededUnderwritingInfoPair> grossMergedCededPairs = new LinkedHashMap<UnderwritingInfo, GrossCededUnderwritingInfoPair>(inUnderwritingInfoGross.size());
            for (UnderwritingInfo grossUnderwritingInfo : inUnderwritingInfoGross) {
                // use the originalUnderwritingInfo property from each (gross in-) UI packet as unique identifier for merging
                if (grossMergedCededPairs.containsKey(grossUnderwritingInfo.getOriginalUnderwritingInfo())) {
                    throw new IllegalArgumentException("MarketUnderwritingInfoMerger.doubleInformation");
                }
                grossMergedCededPairs.put(grossUnderwritingInfo.getOriginalUnderwritingInfo(), new GrossCededUnderwritingInfoPair(grossUnderwritingInfo));
                outUnderwritingInfoGross.add(grossUnderwritingInfo);
            }
            if (isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet)) {
                // now find all ceded in-UI packets with matching key (unique identifier = originalUnderwritingInfo property)
                // and aggregate them by summing their UnderwritingInfo.{premium,commission} and ExposureInfo properties
                for (CededUnderwritingInfo cededUnderwritingInfo : inUnderwritingInfoCeded) {
                    // ceded in-UI packets whose originalUI key don't match any gross in-UI packet's key will be ignored
                    if (grossMergedCededPairs.containsKey(cededUnderwritingInfo.getOriginalUnderwritingInfo())) {
                        // get the working copy of the aggregate of all ceded uwinfo packets matched so far (if any)
                        GrossCededUnderwritingInfoPair underwritingInfoPair = grossMergedCededPairs.get(cededUnderwritingInfo.getOriginalUnderwritingInfo());
                        CededUnderwritingInfo aggregateCededUnderwritingInfo = underwritingInfoPair.getUnderwritingInfoCeded();
                        if (aggregateCededUnderwritingInfo == null) {
                            // copy the first matched packet to the aggregate working copy
                            underwritingInfoPair.setUnderwritingInfoCeded(CededUnderwritingInfoPacketFactory.copy(cededUnderwritingInfo));
                        }
                        else {
                            // add subsequent matched packets to the aggregate working copy
                            aggregateCededUnderwritingInfo.plus(cededUnderwritingInfo);
                            // prevent ExposureInfo.plus from doubling the numberOfPolicies (since the packets come from just one source)!
                            aggregateCededUnderwritingInfo.setNumberOfPolicies(cededUnderwritingInfo.getNumberOfPolicies());
                            aggregateCededUnderwritingInfo.origin = this;
                        }
                    }
                }

                // calculate the net uwinfo for each gross in- uwinfo packet that was found
                for (Map.Entry<UnderwritingInfo, GrossCededUnderwritingInfoPair> entry : grossMergedCededPairs.entrySet()) {
                    UnderwritingInfo grossUnderwritingInfo = entry.getValue().getUnderwritingInfoGross();
                    UnderwritingInfo netUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
                    netUnderwritingInfo.origin = this;
                    netUnderwritingInfo.setOriginalUnderwritingInfo(entry.getKey());
                    CededUnderwritingInfo cededUnderwritingInfo = entry.getValue().getUnderwritingInfoCeded();
                    if (cededUnderwritingInfo == null) {
                        cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(netUnderwritingInfo);
                        CededUnderwritingInfoUtilities.setZero(cededUnderwritingInfo);
                    }
                    else {
                        // net premium written = gross premium written - sum of matched ceded premium written
                        netUnderwritingInfo = UnderwritingInfoUtilities.calculateNet(grossUnderwritingInfo, cededUnderwritingInfo);
                    }

                    outUnderwritingInfoCeded.add(cededUnderwritingInfo);
                    outUnderwritingInfoNet.add(netUnderwritingInfo);
                }
            }
            if (isSenderWired(getOutUnderwritingInfoCededInGrossPackets()) && outUnderwritingInfoCeded.size() > 0) {
                for (CededUnderwritingInfo cededUnderwritingInfo : outUnderwritingInfoCeded) {
                    getOutUnderwritingInfoCededInGrossPackets().add(UnderwritingInfoPacketFactory.copy(cededUnderwritingInfo));
                }
            }
        }
    }

    private boolean anyOutChannelWired() {
        return isSenderWired(outUnderwritingInfoGross) || isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet);
    }

    public PacketList<CededUnderwritingInfo> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoGross() {
        return inUnderwritingInfoGross;
    }

    public void setInUnderwritingInfoGross(PacketList<UnderwritingInfo> inUnderwritingInfoGross) {
        this.inUnderwritingInfoGross = inUnderwritingInfoGross;
    }

    public PacketList<CededUnderwritingInfo> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded) {
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

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoCededInGrossPackets() {
        return outUnderwritingInfoCededInGrossPackets;
    }

    public void setOutUnderwritingInfoCededInGrossPackets(PacketList<UnderwritingInfo> outUnderwritingInfoCededInGrossPackets) {
        this.outUnderwritingInfoCededInGrossPackets = outUnderwritingInfoCededInGrossPackets;
    }
}
