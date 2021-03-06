package models.podraPC

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicDevelopedClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobsWithReserves
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceWithBouquetCommissionProgram
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.DynamicReservesGeneratorLean
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PodraPCModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicUnderwritingSegments underwritingSegments
    DynamicDevelopedClaimsGenerators claimsGenerators
    DynamicReservesGeneratorLean reserveGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators
    DynamicConfigurableLobsWithReserves linesOfBusiness
    ReinsuranceWithBouquetCommissionProgram reinsurance

    void initComponents() {
        globalParameters = new GlobalParameters()
        underwritingSegments = new DynamicUnderwritingSegments()
        claimsGenerators = new DynamicDevelopedClaimsGenerators()
        reserveGenerators = new DynamicReservesGeneratorLean()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        linesOfBusiness = new DynamicConfigurableLobsWithReserves()
        reinsurance = new ReinsuranceWithBouquetCommissionProgram()

        addStartComponent underwritingSegments
        addStartComponent dependencies
        addStartComponent eventGenerators
    }

    void wireComponents() {
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inProbabilities = dependencies.outProbabilities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
        reserveGenerators.inClaims = claimsGenerators.outClaims
        if (linesOfBusiness.subComponentCount() > 0) {
            linesOfBusiness.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
            linesOfBusiness.inClaimsGross = claimsGenerators.outClaims
            linesOfBusiness.inClaimsGross = reserveGenerators.outClaimsDevelopment
            reinsurance.inUnderwritingInfo = linesOfBusiness.outUnderwritingInfoGross
            reinsurance.inClaims = linesOfBusiness.outClaimsGross
            linesOfBusiness.inUnderwritingInfoCeded = reinsurance.outCoverUnderwritingInfo
            linesOfBusiness.inClaimsCeded = reinsurance.outClaimsCeded
        }
        else {
            reinsurance.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
            reinsurance.inClaims = claimsGenerators.outClaims
            reinsurance.inClaims = reserveGenerators.outClaimsDevelopment
        }
    }
}
