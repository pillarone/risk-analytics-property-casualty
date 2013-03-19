package models.dynamicCapitalEagle

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.creditrisk.CreditDefault
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobs
import org.pillarone.riskanalytics.domain.pc.reinsurance.DynamicReinsurersDefaultProbabilities
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.MultiLineDynamicReinsuranceProgramWithDefault
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicCapitalEagleModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicUnderwritingSegments underwritingSegments
    DynamicClaimsGenerators claimsGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators
    DynamicConfigurableLobs linesOfBusiness
    MultiLineDynamicReinsuranceProgramWithDefault reinsurance
    DynamicReinsurersDefaultProbabilities reinsurersRating
    CreditDefault defaultProbabilities

    void initComponents() {
        globalParameters = new GlobalParameters()
        underwritingSegments = new DynamicUnderwritingSegments()
        claimsGenerators = new DynamicClaimsGenerators()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        reinsurersRating = new DynamicReinsurersDefaultProbabilities()
        defaultProbabilities = new CreditDefault()
        linesOfBusiness = new DynamicConfigurableLobs()
        reinsurance = new MultiLineDynamicReinsuranceProgramWithDefault()

        addStartComponent underwritingSegments
        addStartComponent dependencies
        addStartComponent eventGenerators
        addStartComponent defaultProbabilities
    }

    void wireComponents() {
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inProbabilities = dependencies.outProbabilities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
        linesOfBusiness.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
        linesOfBusiness.inClaimsGross = claimsGenerators.outClaims
        reinsurance.inUnderwritingInfo = linesOfBusiness.outUnderwritingInfoGross
        reinsurance.inClaims = linesOfBusiness.outClaimsGross
        reinsurersRating.inDefaultProbability = defaultProbabilities.outDefaultProbability
        reinsurance.inReinsurersDefault = reinsurersRating.outReinsurersDefault
        linesOfBusiness.inUnderwritingInfoCeded = reinsurance.outCoverUnderwritingInfo
        linesOfBusiness.inClaimsCeded = reinsurance.outClaimsCeded
    }
}