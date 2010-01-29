package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IFrequencySingleClaimsGeneratorStrategy extends IFrequencyClaimsGeneratorStrategy {
    FrequencySeverityClaimType getProduceClaim();
}