package models.oneLobQSXL

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class OneLobQSXLModel extends StochasticModel {

    FrequencyGenerator frequencyGenerator
    SingleClaimsGenerator claimsGenerator
    ReinsuranceContract quotaShare
    ReinsuranceContract wxl

    void initComponents() {
        frequencyGenerator = new FrequencyGenerator()
        //parmGenerator: RandomNumberGeneratorFactory.getGenerator(ContinuousRandomDistributionType.CONSTANT, ["constant": 0]),
        //parmBase: FrequencyBase.ABSOLUTE)
        claimsGenerator = new SingleClaimsGenerator()
        //parmGenerator: RandomNumberGeneratorFactory.getGenerator(ContinuousRandomDistributionType.CONSTANT, ["constant": 0]),
        //parmBase: Exposure.ABSOLUTE)
        quotaShare = new ReinsuranceContract(parmContractStrategy: new QuotaShareContractStrategy("quotaShare": 0.2))
        wxl = new ReinsuranceContract(parmContractStrategy: new WXLContractStrategy(
            "attachmentPoint": 100,
            "limit": 10,
            "aggregateLimit": 100,
            "premiumBase": PremiumBase.ABSOLUTE,
            "premium": 70,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium'])))

        allComponents << frequencyGenerator
        allComponents << claimsGenerator
        allComponents << quotaShare
        allComponents << wxl
        addStartComponent frequencyGenerator
    }

    void wireComponents() {
        claimsGenerator.inClaimCount = frequencyGenerator.outFrequency
        quotaShare.inClaims = claimsGenerator.outClaims
        wxl.inClaims = quotaShare.outUncoveredClaims
    }
}