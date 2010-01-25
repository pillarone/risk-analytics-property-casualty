package org.pillarone.riskanalytics.domain.pc.generators.frequency;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

/**
 * @author michael-noe (at) web (dot) de, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencyExtractor extends Component {

    private PacketList<Frequency> inFrequency = new PacketList<Frequency>(Frequency.class);
    private PacketList<Frequency> outFrequency = new PacketList<Frequency>(Frequency.class);

    private int extractElement = 0;

    public FrequencyExtractor(int extractElement) {
        this.extractElement = extractElement;
    }

    protected void doCalculation() {
        getOutFrequency().add(getInFrequency().get(extractElement));
    }

    public PacketList<Frequency> getInFrequency() {
        return inFrequency;
    }

    public void setInFrequency(PacketList<Frequency> inFrequency) {
        this.inFrequency = inFrequency;
    }

    public PacketList<Frequency> getOutFrequency() {
        return outFrequency;
    }

    public void setOutFrequency(PacketList<Frequency> outFrequency) {
        this.outFrequency = outFrequency;
    }
}
