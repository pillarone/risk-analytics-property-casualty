package org.pillarone.riskanalytics.domain.pc.allocators;

import org.pillarone.riskanalytics.domain.pc.constants.Exposure;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency;
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyPacketFactory;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory;

import java.util.Arrays;

/**
 * The frequency allocator receives one frequency packet and splits this frequency on the risk
 * bands <tt>inUnderwritingInfo</tt>. <tt>outFrequency</tt> contains as many frequency packets as
 * <tt>inUnderwritingInfo</tt> packets are received. Allocation is base on the property specified in
 * <tt>parmBase</tt>.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com, michael-noe (at) web (dot) de
 */
public class FrequencyAllocator extends Component {

    PacketList<Frequency> inFrequency = new PacketList<Frequency>(Frequency.class);
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    PacketList<Frequency> outFrequency = new PacketList<Frequency>(Frequency.class);

    public Exposure parmBase = Exposure.NUMBER_OF_POLICIES;

    IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getUniformGenerator();

    public void validateWiring() {
        if (wiredReceivers(inUnderwritingInfo) != 1) {
            throw new IllegalArgumentException("Wiring error: More than one underwriting information source is wired!");
        }
        if (wiredReceivers(inFrequency) != 1) {
            throw new IllegalArgumentException("Wiring error: More than one frequency source is wired!");
        }
        super.validateWiring();
    }

    protected void doCalculation() {
        if (inFrequency.size() != 1) {
            throw new IllegalArgumentException("More than one frequency packet received!");
        }
        int numberOfUnderwritingInfos = inUnderwritingInfo.size();
        double[] limits = new double[numberOfUnderwritingInfos];
        double total = 0d;
        for (int i = 0; i < numberOfUnderwritingInfos; i++) {
            total += inUnderwritingInfo.get(i).scaleValue(parmBase);
            outFrequency.add(FrequencyPacketFactory.createPacket());
        }
        for (int i = 0; i < numberOfUnderwritingInfos; i++) {
            if (i == 0) {
                limits[i] = inUnderwritingInfo.get(i).scaleValue(parmBase) / total;
            } else {
                limits[i] = limits[i - 1] + inUnderwritingInfo.get(i).scaleValue(parmBase) / total;
            }
        }

        for (int i = 0; i < inFrequency.get(0).value; i++) {
            double u = (Double) generator.nextValue();
            int interval = Arrays.binarySearch(limits, u);
            if (interval < 0) {
                interval = -interval - 1;
            }
            outFrequency.get(interval).value++;
        }
    }
}