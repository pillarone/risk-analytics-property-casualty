package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModified

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExternalSeverityClaimsGeneratorStrategy implements IParameterObject, IClaimsGeneratorStrategy {

    Exposure claimsSizeBase = Exposure.ABSOLUTE
    RandomDistribution claimsSizeDistribution = RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ['constant': 0d])
    FrequencySeverityClaimType produceClaim = FrequencySeverityClaimType.AGGREGATED_EVENT

    public Object getType() {
        return ClaimsGeneratorType.EXTERNAL_SEVERITY
    }

    public Map getParameters() {
        ['claimsSizeBase': claimsSizeBase,
                'claimsSizeDistribution': claimsSizeDistribution,
                'produceClaim': produceClaim]
    }

    public DistributionModified getClaimsSizeModification() {
        return null
    }

    public FrequencySeverityClaimType getProduceClaim() {
        produceClaim
    }
}
