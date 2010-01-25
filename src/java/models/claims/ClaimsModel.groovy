package models.claims

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsModel extends StochasticModel {

    DynamicClaimsGenerators claimsGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators

    void initComponents() {
        claimsGenerators = new DynamicClaimsGenerators()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()

        addStartComponent dependencies
        addStartComponent eventGenerators
    }

    void wireComponents() {
        claimsGenerators.inProbabilities = dependencies.outProbabilities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
    }
}