package models.multiCompany

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.AlmResultAggregator
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicDevelopedClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies

import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.DynamicReservesGeneratorLean
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.lob.DynamicCompanyConfigurableLobsWithReserves
import org.pillarone.riskanalytics.domain.pc.company.DynamicCompany
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceMarketWithBouquetCommissionProgram
import org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.DynamicCompanyConfigurableAssetLiabilityMismatchGenerator
import org.pillarone.riskanalytics.domain.pc.filter.DynamicSegmentFilters

/**
 * @author shartmann (at) munichre (dot) com
 */
class MultiCompanyModel extends StochasticModel {

    DynamicUnderwritingSegments underwritingSegments
    DynamicDevelopedClaimsGenerators claimsGenerators
    DynamicReservesGeneratorLean reserveGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators
    DynamicCompanyConfigurableLobsWithReserves linesOfBusiness
    DynamicCompany companies
    ReinsuranceMarketWithBouquetCommissionProgram reinsuranceMarket
    DynamicCompanyConfigurableAssetLiabilityMismatchGenerator almGenerators
    AlmResultAggregator aggregateFinancials
    DynamicSegmentFilters structures


    void initComponents() {
        underwritingSegments = new DynamicUnderwritingSegments()
        claimsGenerators = new DynamicDevelopedClaimsGenerators()
        reserveGenerators = new DynamicReservesGeneratorLean()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        linesOfBusiness = new DynamicCompanyConfigurableLobsWithReserves()
        companies = new DynamicCompany()
        reinsuranceMarket = new ReinsuranceMarketWithBouquetCommissionProgram()
        almGenerators = new DynamicCompanyConfigurableAssetLiabilityMismatchGenerator()
        aggregateFinancials = new AlmResultAggregator()
        structures = new DynamicSegmentFilters()

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
        reinsuranceMarket.inUnderwritingInfo = linesOfBusiness.outUnderwritingInfoGross
        reinsuranceMarket.inClaims = linesOfBusiness.outClaimsGross
        linesOfBusiness.inUnderwritingInfoCeded = reinsuranceMarket.outCoverUnderwritingInfo
        linesOfBusiness.inClaimsCeded = reinsuranceMarket.outClaimsCeded
        companies.inClaimsGross = reinsuranceMarket.outClaimsGross
        companies.inClaimsCeded = reinsuranceMarket.outClaimsCeded
        companies.inUnderwritingInfoGross = reinsuranceMarket.outUnderwritingInfo
        companies.inUnderwritingInfoCeded = reinsuranceMarket.outCoverUnderwritingInfo
        companies.inFinancialResults= almGenerators.outAlmResult
        aggregateFinancials.inClaims = reinsuranceMarket.outClaimsNet
        aggregateFinancials.inUnderwritingInfo = reinsuranceMarket.outNetAfterCoverUnderwritingInfo
        aggregateFinancials.inAlm = almGenerators.outAlmResult
        if (structures.subComponentCount() > 0) {
            structures.inClaimsGross = reinsuranceMarket.outClaimsGross
            structures.inClaimsCeded = linesOfBusiness.outClaimsCeded
            structures.inClaimsNet = linesOfBusiness.outClaimsNet
            structures.inUnderwritingInfoGross = reinsuranceMarket.outUnderwritingInfo
            structures.inUnderwritingInfoCeded = linesOfBusiness.outUnderwritingInfoCeded
            structures.inUnderwritingInfoNet = linesOfBusiness.outUnderwritingInfoNet
        }
    }
}
