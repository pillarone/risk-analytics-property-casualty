package models.podraFac

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.AlmResultAggregator
import org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.DynamicAssetLiabilityMismatchGenerator
import org.pillarone.riskanalytics.domain.pc.filter.DynamicSegmentFilters
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicDevelopedClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.generators.fac.MultipleFacShareDistributions
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobsWithReserves
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceFACWithBouquetCommissionProgram
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.DynamicReservesGeneratorLean
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicDevelopedFACClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author shartmann (at) munichre (dot) com
 */
class PodraFacModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicUnderwritingSegments underwritingSegments
    MultipleFacShareDistributions treatyAllocationForFAC
    DynamicDevelopedFACClaimsGenerators claimsGenerators
    DynamicReservesGeneratorLean reserveGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators
    DynamicConfigurableLobsWithReserves linesOfBusiness
    ReinsuranceFACWithBouquetCommissionProgram reinsurance
    DynamicAssetLiabilityMismatchGenerator almGenerators
    AlmResultAggregator aggregateFinancials
    DynamicSegmentFilters structures


    void initComponents() {
        globalParameters = new GlobalParameters()
        underwritingSegments = new DynamicUnderwritingSegments()
        treatyAllocationForFAC = new MultipleFacShareDistributions()
        claimsGenerators = new DynamicDevelopedFACClaimsGenerators()
        reserveGenerators = new DynamicReservesGeneratorLean()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        linesOfBusiness = new DynamicConfigurableLobsWithReserves()
        reinsurance = new ReinsuranceFACWithBouquetCommissionProgram()
        almGenerators = new DynamicAssetLiabilityMismatchGenerator()
        aggregateFinancials = new AlmResultAggregator()
        structures = new DynamicSegmentFilters()

        addStartComponent underwritingSegments
        addStartComponent dependencies
        addStartComponent eventGenerators
        addStartComponent almGenerators
    }

    void wireComponents() {
        if (treatyAllocationForFAC.subComponentCount() > 0) {
            treatyAllocationForFAC.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
            claimsGenerators.inDistributionsByUwInfo = treatyAllocationForFAC.outDistributionsByUwInfo
        }
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inProbabilities = dependencies.outProbabilities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
        reserveGenerators.inClaims = claimsGenerators.outClaims
        aggregateFinancials.inClaims = reinsurance.outClaimsNet
        aggregateFinancials.inUnderwritingInfo = reinsurance.outNetAfterCoverUnderwritingInfo
        aggregateFinancials.inAlm = almGenerators.outAlmResult
        if (linesOfBusiness.subComponentCount() > 0) {
            linesOfBusiness.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
            linesOfBusiness.inClaimsGross = claimsGenerators.outClaims
            linesOfBusiness.inClaimsGross = reserveGenerators.outClaimsDevelopment
            linesOfBusiness.inInitialReserves = reserveGenerators.outInitialReserves
            reinsurance.inUnderwritingInfo = linesOfBusiness.outUnderwritingInfoGross
            reinsurance.inClaims = linesOfBusiness.outClaimsGross
            linesOfBusiness.inUnderwritingInfoCeded = reinsurance.outCoverUnderwritingInfo
            linesOfBusiness.inClaimsCeded = reinsurance.outClaimsCeded
            if (structures.subComponentCount() > 0) {
                structures.inClaimsGross = reinsurance.outClaimsGross
                structures.inClaimsCeded = linesOfBusiness.outClaimsCeded
                structures.inClaimsNet = linesOfBusiness.outClaimsNet
                structures.inUnderwritingInfoGross = reinsurance.outUnderwritingInfo
                structures.inUnderwritingInfoCeded = linesOfBusiness.outUnderwritingInfoCeded
                structures.inUnderwritingInfoNet = linesOfBusiness.outUnderwritingInfoNet
            }
        }
        else {
            reinsurance.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
            reinsurance.inClaims = claimsGenerators.outClaims
            reinsurance.inClaims = reserveGenerators.outClaimsDevelopment
        }
    }

    public String getDefaultResultConfiguration() {
        return "CapitalEagle Analysis"
    }
}
