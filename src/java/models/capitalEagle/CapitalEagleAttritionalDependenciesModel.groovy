package models.capitalEagle

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsAggregator
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopula
import org.pillarone.riskanalytics.domain.pc.lob.ExampleLobAttritionalDependencies
import org.pillarone.riskanalytics.domain.pc.lob.PropertyLobAttritionalDependencies

class CapitalEagleAttritionalDependenciesModel extends StochasticModel {
    LobCopula copulaAttritional
    ExampleLobAttritionalDependencies mtpl
    ExampleLobAttritionalDependencies motorHull
    ExampleLobAttritionalDependencies personalAccident
    PropertyLobAttritionalDependencies property
    ClaimsAggregator claimsAggregator


    public void initComponents() {
        copulaAttritional = new LobCopula()
        mtpl = new ExampleLobAttritionalDependencies()
        motorHull = new ExampleLobAttritionalDependencies()
        personalAccident = new ExampleLobAttritionalDependencies()
        property = new PropertyLobAttritionalDependencies()
        claimsAggregator = new ClaimsAggregator()

        addStartComponent copulaAttritional
    }

    public void wireComponents() {
        mtpl.inProbabilities = copulaAttritional.outProbabilities
        motorHull.inProbabilities = copulaAttritional.outProbabilities
        personalAccident.inProbabilities = copulaAttritional.outProbabilities
        property.inProbabilities = copulaAttritional.outProbabilities
        claimsAggregator.inClaimsGross = mtpl.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsGross = motorHull.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsGross = personalAccident.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsGross = property.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsCeded = mtpl.subRiProgram.outClaimsCeded
        claimsAggregator.inClaimsCeded = motorHull.subRiProgram.outClaimsCeded
        claimsAggregator.inClaimsCeded = personalAccident.subRiProgram.outClaimsCeded
        claimsAggregator.inClaimsCeded = property.subRiProgram.outClaimsCeded
    }
}