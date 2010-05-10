package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * use the originalUnderwritingInfo property from each (gross in-) UI packet as unique identifier for merging
 *
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
            // By using a LinkedHashMap, we can be sure that all outClaims* lists will be sorted according
            // to the inUnderwritingInfoGross list.
            Map<UnderwritingInfo, GrossCededUnderwritingInfoPair> grossMergedCededPairs = new LinkedHashMap<UnderwritingInfo, GrossCededUnderwritingInfoPair>(inUnderwritingInfoGross.size());
            for (UnderwritingInfo grossUnderwritingInfo : inUnderwritingInfoGross) {
                // use the originalUnderwritingInfo property from each (gross in-) UI packet as unique identifier for merging
                if (grossMergedCededPairs.containsKey(grossUnderwritingInfo.originalUnderwritingInfo)) {
                    throw new IllegalArgumentException("MarketUnderwritingInfoMerger.inUnderwritingInfoGross contains twice the same underwriting info!");
                }
                grossMergedCededPairs.put(grossUnderwritingInfo.originalUnderwritingInfo, new GrossCededUnderwritingInfoPair(grossUnderwritingInfo));
                outUnderwritingInfoGross.add(grossUnderwritingInfo);
            }
            if (isSenderWired(outUnderwritingInfoCeded) || isSenderWired(outUnderwritingInfoNet)) {
                // now find all ceded in-UI packets with matching key (unique identifier = originalUnderwritingInfo property)
                // and aggregate them by summing their UnderwritingInfo.{premiumWritten,commission} and ExposureInfo properties
                for (UnderwritingInfo cededUnderwritingInfo : inUnderwritingInfoCeded) {
                    // ceded in-UI packets whose originalUI key don't match any gross in-UI packet's key will be ignored
                    if (grossMergedCededPairs.containsKey(cededUnderwritingInfo.originalUnderwritingInfo)) {
                        // get the working copy of the aggregate of all ceded uwinfo packets matched so far (if any)
                        GrossCededUnderwritingInfoPair underwritingInfoPair = grossMergedCededPairs.get(cededUnderwritingInfo.originalUnderwritingInfo);
                        UnderwritingInfo aggregateCededUnderwritingInfo = underwritingInfoPair.getUnderwritingInfoCeded();
                        if (aggregateCededUnderwritingInfo == null) {
                            // copy the first matched packet to the aggregate working copy
                            underwritingInfoPair.setUnderwritingInfoCeded(UnderwritingInfoPacketFactory.copy(cededUnderwritingInfo));
                        }
                        else {
                            // add subsequent matched packets to the aggregate working copy
                            aggregateCededUnderwritingInfo.plus(cededUnderwritingInfo);
                            // prevent ExposureInfo.plus from doubling the numberOfPolicies (since the packets come from just one source)!
                            aggregateCededUnderwritingInfo.numberOfPolicies = cededUnderwritingInfo.numberOfPolicies;
                            aggregateCededUnderwritingInfo.origin = this;
                        }
                    }
                }

                // calculate the net uwinfo for each gross in- uwinfo packet that was found
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
                        // net premium written = gross premium written - sum of matched ceded premium written
                        netUnderwritingInfo.minus(cededUnderwritingInfo);
                        // net commission = sum of matched ceded commission
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
