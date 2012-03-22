package models.podra

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.AlmResultAggregator
import org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.DynamicAssetLiabilityMismatchGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicDevelopedClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicDependencies
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DynamicMultipleDependencies
import org.pillarone.riskanalytics.domain.pc.lob.DynamicConfigurableLobsWithReserves
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceWithBouquetCommissionProgram
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.DynamicReservesGeneratorLean
import org.pillarone.riskanalytics.domain.pc.underwriting.DynamicUnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.filter.DynamicSegmentFilters

/**
 * @author shartmann (at) munichre (dot) com
 */
class PodraModel extends StochasticModel {

    DynamicUnderwritingSegments underwritingSegments
    DynamicDevelopedClaimsGenerators claimsGenerators
    DynamicReservesGeneratorLean reserveGenerators
    DynamicDependencies dependencies
    DynamicMultipleDependencies eventGenerators
    DynamicConfigurableLobsWithReserves linesOfBusiness
    ReinsuranceWithBouquetCommissionProgram reinsurance
    DynamicAssetLiabilityMismatchGenerator almGenerators
    AlmResultAggregator aggregateFinancials
    DynamicSegmentFilters structures


    void initComponents() {
        underwritingSegments = new DynamicUnderwritingSegments()
        claimsGenerators = new DynamicDevelopedClaimsGenerators()
        reserveGenerators = new DynamicReservesGeneratorLean()
        dependencies = new DynamicDependencies()
        eventGenerators = new DynamicMultipleDependencies()
        linesOfBusiness = new DynamicConfigurableLobsWithReserves()
        reinsurance = new ReinsuranceWithBouquetCommissionProgram()
        almGenerators = new DynamicAssetLiabilityMismatchGenerator()
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

    @Override
    Closure createResultNavigatorMapping() {
        return {
            lob {
                or {
                    enclosedBy(prefix: ['linesOfBusiness:sub'], suffix: [':'])
                    conditionedOn(value: 'Aggregate') {
                        matching(toMatch: ["linesOfBusiness:(?!sub)"])
                    }
                }
            }
            peril {
                enclosedBy(prefix: ["claimsGenerators:sub"], suffix: [":"])
            }
            reinsuranceContractType {
                enclosedBy(prefix: ["subContracts:sub", "reinsuranceContracts:sub"], suffix: [":"])
            }
            accountBasis {
                matching(toMatch: ["Gross", "Ceded", "Net"])
            }
            keyfigure {
                synonymousTo(category: "Field")
            }
        }
    }


}
