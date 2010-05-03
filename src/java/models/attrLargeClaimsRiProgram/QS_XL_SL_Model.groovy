package models.attrLargeClaimsRiProgram

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsMerger
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.StopLossContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QS_XL_SL_Model extends StochasticModel {

    FrequencyGenerator frequencyGenerator
    SingleClaimsGenerator claimsGenerator
    AttritionalClaimsGenerator attritionalClaimsGenerator
    ReinsuranceContract quotaShare
    ReinsuranceContract wxl
    ReinsuranceContract stopLoss
    ClaimsMerger aggregator = new ClaimsMerger()

    void initComponents() {
        frequencyGenerator = new FrequencyGenerator() //parmGenerator: RandomNumberGeneratorFactory.getGenerator(DiscreteRandomDistributionType.POISSON, ["lambda": 10]))
        claimsGenerator = new SingleClaimsGenerator() //parmGenerator: RandomNumberGeneratorFactory.getGenerator(ContinuousRandomDistributionType.LOGNORMAL, ["mean": 5, "stDev": 10]))
        attritionalClaimsGenerator = new AttritionalClaimsGenerator()
        //parmGenerator: RandomNumberGeneratorFactory.getGenerator(ContinuousRandomDistributionType.LOGNORMAL, ["mean": 50, "stDev": 100]))
        quotaShare = new ReinsuranceContract(parmContractStrategy: new QuotaShareContractStrategy(
            "quotaShare": 0.2))
        wxl = new ReinsuranceContract(parmContractStrategy: new WXLContractStrategy(
            "attachmentPoint": 100,
            "limit": 10,
            "aggregateLimit": 100,
            "premiumBase": PremiumBase.ABSOLUTE,
            "premium": 70,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium'])))
        stopLoss = new ReinsuranceContract(parmContractStrategy: new StopLossContractStrategy(
            "stopLossContractBase": StopLossContractBase.ABSOLUTE,
            "attachmentPoint": 1000,
            "limit": 1000,
            "premium": 800))

        allComponents << frequencyGenerator
        allComponents << claimsGenerator
        allComponents << attritionalClaimsGenerator
        allComponents << quotaShare
        allComponents << wxl
        allComponents << aggregator
        allComponents << stopLoss
        addStartComponent frequencyGenerator
        addStartComponent attritionalClaimsGenerator
    }

    void wireComponents() {
        claimsGenerator.inClaimCount = frequencyGenerator.outFrequency
        quotaShare.inClaims = claimsGenerator.outClaims
        quotaShare.inClaims = attritionalClaimsGenerator.outClaims
        wxl.inClaims = quotaShare.outUncoveredClaims
        aggregator.inClaimsGross = claimsGenerator.outClaims
        aggregator.inClaimsGross = attritionalClaimsGenerator.outClaims
        aggregator.inClaimsCeded = quotaShare.outCoveredClaims
        aggregator.inClaimsCeded = wxl.outCoveredClaims
        stopLoss.inClaims = aggregator.outClaimsNet
        stopLoss.inClaims = quotaShare.outCoveredClaims
        stopLoss.inClaims = wxl.outCoveredClaims
    }
}