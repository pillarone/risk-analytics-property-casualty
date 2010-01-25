package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency;

import java.util.List;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class Copula extends Component {

    private PacketList<Frequency> inNumber = new PacketList<Frequency>(Frequency.class);
    private PacketList<DependenceStream> outProbabilities = new PacketList<DependenceStream>(DependenceStream.class);

    public void doCalculation() {
        if (isReceiverWired(inNumber) || inNumber.size() > 0) {
            for (Frequency frequency: inNumber) {
                for (int i = 0; i < frequency.value; i++) {
                    outProbabilities.add(buildDependenceStream());
                }
            }
        }
        else {
            outProbabilities.add(buildDependenceStream());
        }
    }

    private DependenceStream buildDependenceStream() {
        return new DependenceStream(getRandomVector(), getTargetNames());
    }

    abstract protected List<Number> getRandomVector();

    abstract protected List<String> getTargetNames();

    public PacketList<Frequency> getInNumber() {
        return inNumber;
    }

    public void setInNumber(PacketList<Frequency> inNumber) {
        this.inNumber = inNumber;
    }

    public PacketList<DependenceStream> getOutProbabilities() {
        return outProbabilities;
    }

    public void setOutProbabilities(PacketList<DependenceStream> outProbabilities) {
        this.outProbabilities = outProbabilities;
    }
}