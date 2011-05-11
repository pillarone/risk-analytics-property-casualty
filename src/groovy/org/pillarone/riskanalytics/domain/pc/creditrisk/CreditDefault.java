package org.pillarone.riskanalytics.domain.pc.creditrisk;

import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CREDIT"})
public class CreditDefault extends Component {

    // preset values according to CEIOPS
    private double parmDefaultAAA = 0.00002;
    private double parmDefaultAA  = 0.0001;
    private double parmDefaultA   = 0.0005;
    private double parmDefaultBBB = 0.0024;
    private double parmDefaultBB  = 0.012;
    private double parmDefaultB   = 0.04175;
    private double parmDefaultCCC = 0.04175;
    private double parmDefaultCC  = 0.04175;
    private double parmDefaultC   = 0.04175;

    private PacketList<DefaultProbabilities> outDefaultProbability = new PacketList<DefaultProbabilities>(DefaultProbabilities.class);
    /** this packet is built during the first call of doCalculation() and sent out for each call */
    private DefaultProbabilities probabilities;

    protected void doCalculation() {
        if (probabilities == null) {
            probabilities = new DefaultProbabilities();
            probabilities.defaultProbability.put(Rating.AAA, parmDefaultAAA);
            probabilities.defaultProbability.put(Rating.AA, parmDefaultAAA);
            probabilities.defaultProbability.put(Rating.A, parmDefaultAAA);
            probabilities.defaultProbability.put(Rating.BBB, parmDefaultBBB);
            probabilities.defaultProbability.put(Rating.BB, parmDefaultBB);
            probabilities.defaultProbability.put(Rating.B, parmDefaultB);
            probabilities.defaultProbability.put(Rating.CCC, parmDefaultCCC);
            probabilities.defaultProbability.put(Rating.CC, parmDefaultCC);
            probabilities.defaultProbability.put(Rating.C, parmDefaultC);
            probabilities.defaultProbability.put(Rating.DEFAULT, 1d);
            probabilities.defaultProbability.put(Rating.NO_DEFAULT, 0d);
        }
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