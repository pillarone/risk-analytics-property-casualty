package models.onelobsurplus

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskBands
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocator
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorStrategyFactory

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
class OneLobSurplusModel extends StochasticModel {

    RiskBands underwriting

    FrequencyGenerator frequencyGenerator
    SingleClaimsGenerator singleClaimsGenerator
    AttritionalClaimsGenerator attritionalClaimsGenerator

    RiskAllocator claimsAllocator = new RiskAllocator(
        parmRiskAllocatorStrategy: RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.RISKTOBAND, [:]))

    ReinsuranceContract quotaShare
    ReinsuranceContract surplus
    ReinsuranceContract wxl

    void initComponents() {
        underwriting = new RiskBands()

        attritionalClaimsGenerator = new AttritionalClaimsGenerator()
        frequencyGenerator = new FrequencyGenerator()
        singleClaimsGenerator = new SingleClaimsGenerator()

        claimsAllocator = new RiskAllocator()
        quotaShare = new ReinsuranceContract()
        surplus = new ReinsuranceContract()
        wxl = new ReinsuranceContract()

        allComponents << underwriting
        allComponents << attritionalClaimsGenerator
        allComponents << frequencyGenerator
        allComponents << singleClaimsGenerator
        allComponents << claimsAllocator
        allComponents << quotaShare
        allComponents << surplus
        allComponents << wxl
        addStartComponent underwriting
    }

    void wireComponents() {
        // claims
        frequencyGenerator.inUnderwritingInfo = underwriting.outUnderwritingInfo
        singleClaimsGenerator.inUnderwritingInfo = underwriting.outUnderwritingInfo
        singleClaimsGenerator.inClaimCount = frequencyGenerator.outFrequency
        attritionalClaimsGenerator.inUnderwritingInfo = underwriting.outUnderwritingInfo

        // allocation of claims to risk bands
        claimsAllocator.inUnderwritingInfo = underwriting.outUnderwritingInfo
        claimsAllocator.inTargetDistribution = underwriting.outAttritionalTargetDistribution
        claimsAllocator.inClaims = attritionalClaimsGenerator.outClaims
        claimsAllocator.inClaims = singleClaimsGenerator.outClaims

        quotaShare.inUnderwritingInfo = underwriting.outUnderwritingInfo
        quotaShare.inClaims = claimsAllocator.outClaims

        // surplus
        surplus.inUnderwritingInfo = quotaShare.outNetAfterCoverUnderwritingInfo
        surplus.inClaims = quotaShare.outUncoveredClaims

        wxl.inUnderwritingInfo = surplus.outNetAfterCoverUnderwritingInfo
        wxl.inClaims = surplus.outUncoveredClaims
    }
}