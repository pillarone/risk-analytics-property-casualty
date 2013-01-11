package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.packets.Packet;

import java.util.List;

/**
 *  Each severity in the list severities corresponds belongs to the marginals
 *  with the same list index.
 *
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class DependenceStream extends Packet {
    // todo (sku): convert to a map; new name for marginals: target
    public DependenceStream(){

    }

    public DependenceStream(List<Number> probabilities, List<String> marginals) {
        this.probabilities = probabilities;
        this.marginals = marginals;
    }

    public List<Number> probabilities;
    public List<String> marginals;

    public List<Number> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<Number> probabilities) {
        this.probabilities = probabilities;
    }

    public List<String> getMarginals() {
        return marginals;
    }

    public void setMarginals(List<String> marginals) {
        this.marginals = marginals;
    }
}