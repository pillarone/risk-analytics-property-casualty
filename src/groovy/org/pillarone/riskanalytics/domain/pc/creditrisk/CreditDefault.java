package org.pillarone.riskanalytics.domain.pc.creditrisk;

import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CreditDefault extends Component {

    private double parmDefaultAAA = 0.001;
    private double parmDefaultAA = 0.002;
    private double parmDefaultA = 0.004;
    private double parmDefaultBBB = 0.1;
    private double parmDefaultBB = 0.3;
    private double parmDefaultB = 0.5;
    private double parmDefaultCCC = 0.7;
    private double parmDefaultCC = 0.8;
    private double parmDefaultC = 0.9;

    private PacketList<DefaultProbabilities> outDefaultProbability = new PacketList<DefaultProbabilities>(DefaultProbabilities.class);

    protected void doCalculation() {
        DefaultProbabilities probabilities = new DefaultProbabilities();
        probabilities.defaultProbability.put(Rating.AAA, parmDefaultAAA);
        probabilities.defaultProbability.put(Rating.AA, parmDefaultAAA);
        probabilities.defaultProbability.put(Rating.A, parmDefaultAAA);
        probabilities.defaultProbability.put(Rating.BBB, parmDefaultBBB);
        probabilities.defaultProbability.put(Rating.BB, parmDefaultBB);
        probabilities.defaultProbability.put(Rating.B, parmDefaultB);
        probabilities.defaultProbability.put(Rating.CCC, parmDefaultCCC);
        probabilities.defaultProbability.put(Rating.CC, parmDefaultCC);
        probabilities.defaultProbability.put(Rating.C, parmDefaultC);
        outDefaultProbability.add(probabilities);
    }

    public PacketList<DefaultProbabilities> getOutDefaultProbability() {
        return outDefaultProbability;
    }

    public void setOutDefaultProbability(PacketList<DefaultProbabilities> outDefaultProbability) {
        this.outDefaultProbability = outDefaultProbability;
    }

    public double getParmDefaultAAA() {
        return parmDefaultAAA;
    }

    public void setParmDefaultAAA(double parmDefaultAAA) {
        this.parmDefaultAAA = parmDefaultAAA;
    }

    public double getParmDefaultAA() {
        return parmDefaultAA;
    }

    public void setParmDefaultAA(double parmDefaultAA) {
        this.parmDefaultAA = parmDefaultAA;
    }

    public double getParmDefaultA() {
        return parmDefaultA;
    }

    public void setParmDefaultA(double parmDefaultA) {
        this.parmDefaultA = parmDefaultA;
    }

    public double getParmDefaultBBB() {
        return parmDefaultBBB;
    }

    public void setParmDefaultBBB(double parmDefaultBBB) {
        this.parmDefaultBBB = parmDefaultBBB;
    }

    public double getParmDefaultBB() {
        return parmDefaultBB;
    }

    public void setParmDefaultBB(double parmDefaultBB) {
        this.parmDefaultBB = parmDefaultBB;
    }

    public double getParmDefaultB() {
        return parmDefaultB;
    }

    public void setParmDefaultB(double parmDefaultB) {
        this.parmDefaultB = parmDefaultB;
    }

    public double getParmDefaultCCC() {
        return parmDefaultCCC;
    }

    public void setParmDefaultCCC(double parmDefaultCCC) {
        this.parmDefaultCCC = parmDefaultCCC;
    }

    public double getParmDefaultCC() {
        return parmDefaultCC;
    }

    public void setParmDefaultCC(double parmDefaultCC) {
        this.parmDefaultCC = parmDefaultCC;
    }

    public double getParmDefaultC() {
        return parmDefaultC;
    }

    public void setParmDefaultC(double parmDefaultC) {
        this.parmDefaultC = parmDefaultC;
    }
}