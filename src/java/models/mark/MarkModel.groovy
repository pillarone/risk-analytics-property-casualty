package models.mark

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class MarkModel extends StochasticModel {

    FrequencyGenerator frequencyGenerator
    SingleClaimsGenerator claimsGeneratorFire
    SingleClaimsGenerator claimsGeneratorMotor

    void initComponents() {
        frequencyGenerator = new FrequencyGenerator() //parmDistribution: RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 10]))
        claimsGeneratorFire = new SingleClaimsGenerator() //parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        claimsGeneratorMotor = new SingleClaimsGenerator() //parmDistribution: RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))

        allComponents << frequencyGenerator
        allComponents << claimsGeneratorFire
        allComponents << claimsGeneratorMotor

        addStartComponent frequencyGenerator
    }

    void wireComponents() {
        claimsGeneratorFire.inClaimCount = frequencyGenerator.outFrequency
        claimsGeneratorMotor.inClaimCount = frequencyGenerator.outFrequency
    }
}