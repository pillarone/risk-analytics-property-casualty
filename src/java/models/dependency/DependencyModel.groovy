package models.dependency

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsAggregator
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopula
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.lob.DependentLob
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.severities.AttachEventToSeverity

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DependencyModel extends StochasticModel {

    FrequencyGenerator frequencyGenerator
    LobCopula copula
    AttachEventToSeverity eventSeverities
    DependentLob fire
    DependentLob hull
    ReinsuranceContract cxl
    ClaimsAggregator claimsAggregator

    void initComponents() {
        frequencyGenerator = new FrequencyGenerator()
        copula = new LobCopula()
        eventSeverities = new AttachEventToSeverity()
        fire = new DependentLob()
        hull = new DependentLob()
        cxl = new ReinsuranceContract()
        claimsAggregator = new ClaimsAggregator()

        addStartComponent frequencyGenerator
    }

    void wireComponents() {
        copula.inNumber = frequencyGenerator.outFrequency
        eventSeverities.inProbabilities = copula.outProbabilities
        fire.inEventSeverities = eventSeverities.outEventSeverities
        hull.inEventSeverities = eventSeverities.outEventSeverities
        cxl.inClaims = fire.outClaims
        cxl.inClaims = hull.outClaims
        claimsAggregator.inClaimsGross = fire.outClaims
        claimsAggregator.inClaimsGross = hull.outClaims
        claimsAggregator.inClaimsCeded = cxl.outCoveredClaims
    }
}