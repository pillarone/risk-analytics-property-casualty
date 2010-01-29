package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.domain.utils.RandomDistribution;


/**
 * ben (dot) ginsberg (at) intuitive-collaboration (dot) com
 */
public interface IOccurrenceClaimsGeneratorStrategy extends IClaimsGeneratorStrategy {
    RandomDistribution getOccurrenceDistribution();
}