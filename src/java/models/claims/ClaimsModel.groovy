package models.claims

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicClaimsGenerators claimsGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators

    void initComponents() {
        globalParameters = new GlobalParameters()
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