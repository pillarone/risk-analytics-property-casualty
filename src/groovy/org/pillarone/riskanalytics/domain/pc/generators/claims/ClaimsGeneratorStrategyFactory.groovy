package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.DistributionModified

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGeneratorStrategyFactory {

    static IClaimsGeneratorStrategy getStrategy(ClaimsGeneratorType type, Map parameters) {
        IClaimsGeneratorStrategy claimsGenerator;
        switch (type) {
            case ClaimsGeneratorType.NONE:
                claimsGenerator = new NoneClaimsGeneratorStrategy()
                break;
            case ClaimsGeneratorType.ATTRITIONAL:
                claimsGenerator = new AttritionalClaimsGeneratorStrategy(
                        claimsSizeBase: (Exposure) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"))
                break;
            case ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL:
                claimsGenerator = new FrequencyAverageAttritionalClaimsGeneratorStrategy(
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (Exposure) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"))
                break;
            case ClaimsGeneratorType.FREQUENCY_SEVERITY:
                claimsGenerator = new FrequencySeverityClaimsGeneratorStrategy(
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (Exposure) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
                break;
            case ClaimsGeneratorType.EXTERNAL_SEVERITY:
                claimsGenerator = new ExternalSeverityClaimsGeneratorStrategy(
                        claimsSizeBase: (Exposure) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
        }
        return claimsGenerator;
    }
}