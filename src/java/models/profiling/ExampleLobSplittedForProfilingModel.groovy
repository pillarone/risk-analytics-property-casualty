package models.profiling

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsMerger
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ExampleLobSplittedForProfilingModel extends StochasticModel {

    UnderwritingSegment underwriting

    FrequencyGenerator frequencyGenerator
    SingleClaimsGenerator singleClaimsGenerator
    AttritionalClaimsGenerator attritionalClaimsGenerator

    ReinsuranceContract contract1 = new ReinsuranceContract(parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    ReinsuranceContract contract2 = new ReinsuranceContract(parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    ReinsuranceContract contract3 = new ReinsuranceContract(parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    ClaimsMerger reinsuranceClaimsAggregator = new ClaimsMerger()

    void initComponents() {
        underwriting = new UnderwritingSegment()
        frequencyGenerator = new FrequencyGenerator()
        singleClaimsGenerator = new SingleClaimsGenerator()
        attritionalClaimsGenerator = new AttritionalClaimsGenerator()

        allComponents << underwriting
        allComponents << frequencyGenerator
        allComponents << singleClaimsGenerator
        allComponents << attritionalClaimsGenerator
        allComponents << contract1
        allComponents << contract2
        allComponents << contract3
        allComponents << reinsuranceClaimsAggregator

        addStartComponent underwriting
    }

    void wireComponents() {
        attritionalClaimsGenerator.inUnderwritingInfo = underwriting.outUnderwritingInfo
        frequencyGenerator.inUnderwritingInfo = underwriting.outUnderwritingInfo
        singleClaimsGenerator.inUnderwritingInfo = underwriting.outUnderwritingInfo
        singleClaimsGenerator.inClaimCount = frequencyGenerator.outFrequency

        contract1.inUnderwritingInfo = underwriting.outUnderwritingInfo
        contract2.inUnderwritingInfo = contract1.outNetAfterCoverUnderwritingInfo
        contract3.inUnderwritingInfo = contract2.outNetAfterCoverUnderwritingInfo

        contract1.inClaims = singleClaimsGenerator.outClaims
        contract1.inClaims = attritionalClaimsGenerator.outClaims
        contract2.inClaims = contract1.outUncoveredClaims
        contract3.inClaims = contract2.outUncoveredClaims

        reinsuranceClaimsAggregator.inClaimsCeded = contract1.outCoveredClaims
        reinsuranceClaimsAggregator.inClaimsCeded = contract2.outCoveredClaims
        reinsuranceClaimsAggregator.inClaimsCeded = contract3.outCoveredClaims

        reinsuranceClaimsAggregator.inClaimsGross = singleClaimsGenerator.outClaims
        reinsuranceClaimsAggregator.inClaimsGross = attritionalClaimsGenerator.outClaims
    }
}