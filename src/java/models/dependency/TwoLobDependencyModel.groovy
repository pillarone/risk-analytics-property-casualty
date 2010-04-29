package models.dependency

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsAggregator
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopula
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.lob.DependentLobAttrSingleEventClaims
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.severities.AttachEventToSeverity

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class TwoLobDependencyModel extends StochasticModel {

    FrequencyGenerator frequencyGeneratorEvent
    FrequencyGenerator frequencyGeneratorLarge
    FrequencyGenerator independentFrequencyGeneratorHull
    FrequencyGenerator independentFrequencyGeneratorFire
    LobCopula copulaEvent
    LobCopula copulaLarge
    LobCopula copulaAttritional

    AttachEventToSeverity eventSeverities
    DependentLobAttrSingleEventClaims fire
    DependentLobAttrSingleEventClaims hull
    ReinsuranceContract cxl

    ClaimsAggregator claimsAggregator

    void initComponents() {
        frequencyGeneratorEvent = new FrequencyGenerator(name: 'event number')
        frequencyGeneratorLarge = new FrequencyGenerator(name: 'number of large claims')
        independentFrequencyGeneratorFire = new FrequencyGenerator(name: 'independent fire')
        independentFrequencyGeneratorHull = new FrequencyGenerator(name: 'independent hull')
        copulaEvent = new LobCopula()
        copulaLarge = new LobCopula()
        copulaAttritional = new LobCopula()
        eventSeverities = new AttachEventToSeverity()
        fire = new DependentLobAttrSingleEventClaims()
        hull = new DependentLobAttrSingleEventClaims()
        cxl = new ReinsuranceContract()
        claimsAggregator = new ClaimsAggregator()

        addStartComponent frequencyGeneratorEvent
        addStartComponent frequencyGeneratorLarge
        addStartComponent copulaAttritional
        addStartComponent independentFrequencyGeneratorHull
        addStartComponent independentFrequencyGeneratorFire
    }

    void wireComponents() {
        copulaEvent.inNumber = frequencyGeneratorEvent.outFrequency
        eventSeverities.inProbabilities = copulaEvent.outProbabilities
        copulaLarge.inNumber = frequencyGeneratorLarge.outFrequency

        fire.inEventSeverities = eventSeverities.outEventSeverities
        fire.inSingleSeverities = copulaLarge.outProbabilities
        fire.inAttritionalSeverities = copulaAttritional.outProbabilities
        fire.inSingleClaimsCount = independentFrequencyGeneratorFire.outFrequency
        hull.inEventSeverities = eventSeverities.outEventSeverities
        hull.inSingleSeverities = copulaLarge.outProbabilities
        hull.inAttritionalSeverities = copulaAttritional.outProbabilities
        hull.inSingleClaimsCount = independentFrequencyGeneratorHull.outFrequency
        cxl.inClaims = fire.outClaims
        cxl.inClaims = hull.outClaims

        claimsAggregator.inClaimsGross = fire.outClaims
        claimsAggregator.inClaimsGross = hull.outClaims
        claimsAggregator.inClaimsCeded = cxl.outCoveredClaims
    }
}