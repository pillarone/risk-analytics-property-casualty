package models.profiling

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SimpleProfilingModel extends StochasticModel {

    FrequencyGenerator frequencyGenerator
    SingleClaimsGenerator claimsGenerator

    void initComponents() {
        frequencyGenerator = new FrequencyGenerator()
        claimsGenerator = new SingleClaimsGenerator()

        allComponents << frequencyGenerator
        allComponents << claimsGenerator

        addStartComponent frequencyGenerator
    }

    void wireComponents() {
        claimsGenerator.inClaimCount = frequencyGenerator.outFrequency
    }
}