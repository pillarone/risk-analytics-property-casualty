package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.domain.pc.constants.Reinsurer;
import org.pillarone.riskanalytics.domain.pc.creditrisk.DefaultProbabilities;
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurersDefault;
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory;
import umontreal.iro.lecuyer.probdist.BinomialDist;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): add flexibility in the number and naming of reinsurers
public class ReinsurersDefaultProbability extends Component {
    private Rating parmMercuryRe = Rating.AAA;
    private Rating parmVenusRe = Rating.AAA;
    private Rating parmEarthRe = Rating.AAA;
    private Rating parmMarsRe = Rating.AAA;
    private Rating parmJupiterRe = Rating.AAA;
    private Rating parmSaturnRe = Rating.AAA;
    private Rating parmUranusRe = Rating.AAA;
    private Rating parmNeptuneRe = Rating.AAA;

    private PacketList<DefaultProbabilities> inDefaultProbability = new PacketList<DefaultProbabilities>(DefaultProbabilities.class);
    private PacketList<ReinsurersDefault> outReinsurersDefault = new PacketList<ReinsurersDefault>(ReinsurersDefault.class);

    private IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getBinomialGenerator();

    protected void doCalculation() {
        Map<Rating, Double> defaultProbabilities = inDefaultProbability.get(0).defaultProbability;
        ReinsurersDefault reinsurersDefault = new ReinsurersDefault();
        reinsurersDefault.defaultOccured.put(Reinsurer.MERCURY_RE, defaultOfReinsurer(defaultProbabilities.get(parmMercuryRe)));
        reinsurersDefault.defaultOccured.put(Reinsurer.VENUS_RE, defaultOfReinsurer(defaultProbabilities.get(parmVenusRe)));
        reinsurersDefault.defaultOccured.put(Reinsurer.EARTH_RE, defaultOfReinsurer(defaultProbabilities.get(parmEarthRe)));
        reinsurersDefault.defaultOccured.put(Reinsurer.MARS_RE, defaultOfReinsurer(defaultProbabilities.get(parmMarsRe)));
        reinsurersDefault.defaultOccured.put(Reinsurer.JUPITER_RE, defaultOfReinsurer(defaultProbabilities.get(parmJupiterRe)));
        reinsurersDefault.defaultOccured.put(Reinsurer.SATURN_RE, defaultOfReinsurer(defaultProbabilities.get(parmSaturnRe)));
        reinsurersDefault.defaultOccured.put(Reinsurer.URANUS_RE, defaultOfReinsurer(defaultProbabilities.get(parmUranusRe)));
        reinsurersDefault.defaultOccured.put(Reinsurer.NEPTUN_RE, defaultOfReinsurer(defaultProbabilities.get(parmNeptuneRe)));

        outReinsurersDefault.add(reinsurersDefault);
    }

    private Integer defaultOfReinsurer(double probability) {
        ((BinomialDist) generator.getDistribution()).setParams(1, probability);
        return (Integer) generator.nextValue();
    }

    public PacketList<DefaultProbabilities> getInDefaultProbability() {
        return inDefaultProbability;
    }

    public void setInDefaultProbability(PacketList<DefaultProbabilities> inDefaultProbability) {
        this.inDefaultProbability = inDefaultProbability;
    }

    public PacketList<ReinsurersDefault> getOutReinsurersDefault() {
        return outReinsurersDefault;
    }

    public void setOutReinsurersDefault(PacketList<ReinsurersDefault> outReinsurersDefault) {
        this.outReinsurersDefault = outReinsurersDefault;
    }

    public Rating getParmMercuryRe() {
        return parmMercuryRe;
    }

    public void setParmMercuryRe(Rating parmMercuryRe) {
        this.parmMercuryRe = parmMercuryRe;
    }

    public Rating getParmVenusRe() {
        return parmVenusRe;
    }

    public void setParmVenusRe(Rating parmVenusRe) {
        this.parmVenusRe = parmVenusRe;
    }

    public Rating getParmEarthRe() {
        return parmEarthRe;
    }

    public void setParmEarthRe(Rating parmEarthRe) {
        this.parmEarthRe = parmEarthRe;
    }

    public Rating getParmMarsRe() {
        return parmMarsRe;
    }

    public void setParmMarsRe(Rating parmMarsRe) {
        this.parmMarsRe = parmMarsRe;
    }

    public Rating getParmJupiterRe() {
        return parmJupiterRe;
    }

    public void setParmJupiterRe(Rating parmJupiterRe) {
        this.parmJupiterRe = parmJupiterRe;
    }

    public Rating getParmSaturnRe() {
        return parmSaturnRe;
    }

    public void setParmSaturnRe(Rating parmSaturnRe) {
        this.parmSaturnRe = parmSaturnRe;
    }

    public Rating getParmUranusRe() {
        return parmUranusRe;
    }

    public void setParmUranusRe(Rating parmUranusRe) {
        this.parmUranusRe = parmUranusRe;
    }

    public Rating getParmNeptuneRe() {
        return parmNeptuneRe;
    }

    public void setParmNeptuneRe(Rating parmNeptuneRe) {
        this.parmNeptuneRe = parmNeptuneRe;
    }
}
