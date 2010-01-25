package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.domain.pc.creditrisk.DefaultProbabilities;
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault;
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory;
import umontreal.iro.lecuyer.probdist.BinomialDist;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsurerDefaultProbability extends Component implements IReinsurerMarker {
    private Rating parmRating = Rating.BBB;

    private PacketList<DefaultProbabilities> inDefaultProbability = new PacketList<DefaultProbabilities>(DefaultProbabilities.class);
    private PacketList<ReinsurerDefault> outReinsurersDefault = new PacketList<ReinsurerDefault>(ReinsurerDefault.class);

    private IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getBinomialGenerator();

    protected void doCalculation() {
        Map<Rating, Double> defaultProbabilities = inDefaultProbability.get(0).defaultProbability;
        boolean isReinsurerDefault = defaultOfReinsurer(defaultProbabilities.get(parmRating));
        ReinsurerDefault reinsurerDefault = new ReinsurerDefault(getName(), isReinsurerDefault);
        outReinsurersDefault.add(reinsurerDefault);
    }

    private boolean defaultOfReinsurer(double probability) {
        ((BinomialDist) generator.getDistribution()).setParams(1, probability);
        return ((Integer) generator.nextValue()) == 1;
    }

    public Rating getParmRating() {
        return parmRating;
    }

    public void setParmRating(Rating parmRating) {
        this.parmRating = parmRating;
    }

    public PacketList<DefaultProbabilities> getInDefaultProbability() {
        return inDefaultProbability;
    }

    public void setInDefaultProbability(PacketList<DefaultProbabilities> inDefaultProbability) {
        this.inDefaultProbability = inDefaultProbability;
    }

    public PacketList<ReinsurerDefault> getOutReinsurersDefault() {
        return outReinsurersDefault;
    }

    public void setOutReinsurersDefault(PacketList<ReinsurerDefault> outReinsurersDefault) {
        this.outReinsurersDefault = outReinsurersDefault;
    }

    public IRandomNumberGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(IRandomNumberGenerator generator) {
        this.generator = generator;
    }
}