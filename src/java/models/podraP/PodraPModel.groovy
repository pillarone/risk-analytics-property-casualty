package models.podraP

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobs
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceWithBouquetCommissionProgram

/**
 * A PodraPModel is a model for liabilities, including deterministic underwriting information, stochastic claims
 * generation, allocation to line of business, and a reinsurance program. Claims can be correlated by using copulas
 * for attritional claims, or by using event generators.<br>
 *
 * On the model level, each component (type) can have an arbitrary number of instances included in the model,
 * which are specified in the parametrization and can be managed from user interface.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PodraPModel extends StochasticModel {

    /** underwritingSegments is a collection of RiskBands which emit their properties on the outUnderwritingInfo channel */
    DynamicUnderwritingSegments underwritingSegments
    /** a list of independently specified claims generators */
    DynamicClaimsGenerators claimsGenerators
    /** dependency structures: copulas based on attritional loss distributions */
    DynamicDependencies dependencies
    // Ereignisgeneratoren für Frequency Severity Generatoren
    DynamicMultipleDependencies eventGenerators
    // mappings of claims Zusammenfassung der Schadengeneratoren zu Branchen
    DynamicConfigurableLobs linesOfBusiness
    // Rückversicherung auf Branchen oder SG-Level
    ReinsuranceWithBouquetCommissionProgram reinsurance

    void initComponents() {
        // set up components
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
// Zusammenbinden der Komponenten: Übergabe von Ausgabegrößen an Eingabekanäle
        /**
         * connect inputs to (preceding) outputs
         */
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inProbabilities = dependencies.outProbabilities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
        linesOfBusiness.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
        linesOfBusiness.inClaimsGross = claimsGenerators.outClaims
        reinsurance.inUnderwritingInfo = linesOfBusiness.outUnderwritingInfoGross
        reinsurance.inClaims = linesOfBusiness.outClaimsGross
        linesOfBusiness.inUnderwritingInfoCeded = reinsurance.outCoverUnderwritingInfo
        linesOfBusiness.inClaimsCeded = reinsurance.outClaimsCeded
    }
}