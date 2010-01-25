package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase;
import org.pillarone.riskanalytics.domain.utils.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.RandomDistribution;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IFrequencyClaimsGeneratorStrategy extends IClaimsGeneratorStrategy {
    RandomDistribution getFrequencyDistribution();
    DistributionModified getFrequencyModification();
    FrequencyBase getFrequencyBase();
}