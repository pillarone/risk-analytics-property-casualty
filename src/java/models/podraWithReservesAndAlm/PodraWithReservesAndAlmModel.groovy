package models.podraWithReservesAndAlm

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.AlmResultAggregator
import org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.DynamicAssetLiabilityMismatchGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicDevelopedClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobsWithReserves
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.MultiLineDynamicReinsuranceProgram
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.DynamicReservesGeneratorLean
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments

/**
 * @author shartmann (at) munichre (dot) com
 */
class PodraWithReservesAndAlmModel extends StochasticModel {

    DynamicUnderwritingSegments underwritingSegments
    DynamicDevelopedClaimsGenerators claimsGenerators
    DynamicReservesGeneratorLean reserveGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators
    DynamicConfigurableLobsWithReserves linesOfBusiness
    MultiLineDynamicReinsuranceProgram reinsurance
    DynamicAssetLiabilityMismatchGenerator almGenerators
    AlmResultAggregator almResult


    void initComponents() {
        underwritingSegments = new DynamicUnderwritingSegments()
        claimsGenerators = new DynamicDevelopedClaimsGenerators()
        reserveGenerators = new DynamicReservesGeneratorLean()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        linesOfBusiness = new DynamicConfigurableLobsWithReserves()
        reinsurance = new MultiLineDynamicReinsuranceProgram()
        almGenerators = new DynamicAssetLiabilityMismatchGenerator()
        almResult = new AlmResultAggregator()

        addStartComponent underwritingSegments
        addStartComponent dependencies
        addStartComponent eventGenerators
        addStartComponent almGenerators
    }

    void wireComponents() {
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inProbabilities = dependencies.outProbabilities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
        linesOfBusiness.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
        reserveGenerators.inClaims = claimsGenerators.outClaims
        linesOfBusiness.inClaimsGross = claimsGenerators.outClaims
        linesOfBusiness.inClaimsGross = reserveGenerators.outClaimsDevelopment
        reinsurance.inUnderwritingInfo = linesOfBusiness.outUnderwritingInfoGross
        reinsurance.inClaims = linesOfBusiness.outClaimsGross
        linesOfBusiness.inUnderwritingInfoCeded = reinsurance.outCoverUnderwritingInfo
        linesOfBusiness.inClaimsCeded = reinsurance.outClaimsCeded
        almResult.inClaims = reinsurance.outClaimsNet
        almResult.inUnderwritingInfo = reinsurance.outNetAfterCoverUnderwritingInfo
        almResult.inAlm = almGenerators.outAlmResult
    }
}
