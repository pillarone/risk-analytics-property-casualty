package models.podra


import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobs
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.MultiLineDynamicReinsuranceProgram

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PodraModel extends StochasticModel {

// Anlegen der Komponenten des Podra Modells
// als "Dynamische Komponenten" (Elemente können im UI hinzugefügt und gelöscht werden
// Underwriting Kennzahlen: Risikobänder
    DynamicUnderwritingSegments underwritingSegments
// Schadengeneratoren: Typen Attritional Loss, Frequency Severity
    DynamicClaimsGenerators claimsGenerators
// Abhängigkeitsstrukturen: Copulae auf Attritional Loss Verteilungen
    DynamicDependencies dependencies
// Ereignisgeneratoren für Frequency Severity Generatoren
    DynamicMultipleDependencies eventGenerators
// Zusammenfassung der Schadengeneratoren zu Branchen
    DynamicConfigurableLobs linesOfBusiness
// Rückversicherung auf Branchen oder SG-Level
    MultiLineDynamicReinsuranceProgram reinsurance

    void initComponents() {
// Komponenten anlegen
        underwritingSegments = new DynamicUnderwritingSegments()
        claimsGenerators = new DynamicClaimsGenerators()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        linesOfBusiness = new DynamicConfigurableLobs()
        reinsurance = new MultiLineDynamicReinsuranceProgram()
// Aufbau des Modells als Baumstruktur: Wurzeln festlegen
        addStartComponent underwritingSegments
        addStartComponent dependencies
        addStartComponent eventGenerators
    }

    void wireComponents() {
// Zusammenbinden der Komponenten: Übergabe von Ausgabegrößen an Eingabekanäle
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