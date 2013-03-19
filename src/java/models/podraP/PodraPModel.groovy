package models.podraP

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobs
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceWithBouquetCommissionProgram
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * A PodraPModel is a model for liabilities, including deterministic underwriting information, stochastic claims
 * generation, allocation to line of business, and a reinsurance program. Claims can be correlated by using copulas
 * for attritional claims, or by using event generators.<br>
 *
 * On the model level, each component (type) can have an arbitrary number of instances included in the model,
 * which are specified in the parametrization and can be managed from the user interface.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PodraPModel extends StochasticModel {

    GlobalParameters globalParameters
    /** underwritingSegments is a collection of RiskBands which emit their properties on the outUnderwritingInfo channel */
    DynamicUnderwritingSegments underwritingSegments
    /** a list of independently specified claims generators */
    DynamicClaimsGenerators claimsGenerators
    /** dependency structures: copulas based on attritional loss distributions */
    DynamicDependencies dependencies
    // Ereignisgeneratoren f�r Frequency Severity Generatoren
    DynamicMultipleDependencies eventGenerators
    // mappings of claims Zusammenfassung der Schadengeneratoren zu Branchen
    DynamicConfigurableLobs linesOfBusiness
    // R�ckversicherung auf Branchen oder SG-Level
    ReinsuranceWithBouquetCommissionProgram reinsurance

    void initComponents() {
        // set up components
        globalParameters = new GlobalParameters()
        underwritingSegments = new DynamicUnderwritingSegments()
        claimsGenerators = new DynamicClaimsGenerators()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        linesOfBusiness = new DynamicConfigurableLobs()
        reinsurance = new ReinsuranceWithBouquetCommissionProgram()
        // build up the model as a tree structure: define the roots
        addStartComponent underwritingSegments
        addStartComponent dependencies
        addStartComponent eventGenerators
    }

    void wireComponents() {
        /**
         * connect inputs to (preceding) outputs
         */
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inProbabilities = dependencies.outProbabilities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
        if (linesOfBusiness.subComponentCount() > 0) {
            linesOfBusiness.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
            linesOfBusiness.inClaimsGross = claimsGenerators.outClaims
            reinsurance.inUnderwritingInfo = linesOfBusiness.outUnderwritingInfoGross
            reinsurance.inClaims = linesOfBusiness.outClaimsGross
            linesOfBusiness.inUnderwritingInfoCeded = reinsurance.outCoverUnderwritingInfo
            linesOfBusiness.inClaimsCeded = reinsurance.outClaimsCeded
        }
        else {
            reinsurance.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
            reinsurance.inClaims = claimsGenerators.outClaims
        }
    }
}