package org.pillarone.riskanalytics.domain.pc.creditrisk;

import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.core.packets.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DefaultProbabilities extends Packet {

    public Map<Rating, Double> defaultProbability = new HashMap<Rating, Double>();

    public DefaultProbabilities() {
    }


    public Map<Rating, Double> getDefaultProbability() {
        return defaultProbability;
    }

    public void setDefaultProbability(Map<Rating, Double> defaultProbability) {
        this.defaultProbability = defaultProbability;
    }
}