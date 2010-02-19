package models.onelobsurplus

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.SurplusContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategy
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType

model = OneLobSurplusModel
periodCount = 2
allPeriods = 0..<periodCount

components {
    underwriting {
        parmUnderwritingInformation[allPeriods] = new TableMultiDimensionalParameter(
                [[1000d, 10000d, 100000d], [800d, 8000d, 80000d], [50000d, 50000d, 50000d], [1000, 100, 10], [''], ['']],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks',
                        'custom allocation number of single claims', 'custom allocation attritional claims'],
                )
        parmAllocationBaseAttritionalClaims[allPeriods] = RiskBandAllocationBase.PREMIUM
        parmAllocationBaseSingleClaims[allPeriods] = RiskBandAllocationBase.NUMBER_OF_POLICIES
    }
    frequencyGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(FrequencyDistributionType.POISSON, ["lambda": 1d])
        parmBase[allPeriods] = FrequencyBase.ABSOLUTE
    }
    singleClaimsGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.PARETO, ['alpha': 1.2d, 'beta': 5000d])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
    attritionalClaimsGenerator {
        parmDistribution[allPeriods] = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.NORMAL, ['mean': 100000d, 'stDev': 5000d])
        parmBase[allPeriods] = Exposure.ABSOLUTE
    }
    claimsAllocator {
        parmRiskAllocatorStrategy[allPeriods] = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.RISKTOBAND, [:])
    }
    quotaShare {
		parmContractStrategy[allPeriods]= new QuotaShareContractStrategy("quotaShare":0.2, "coveredByReinsurer": 1d)
        parmCommissionStrategy[allPeriods] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0d])
	}
    surplus {
        parmContractStrategy[allPeriods] = new SurplusContractStrategy(
                'retention': 2000d,
                'lines': 4,
                'defaultCededLossShare':0.0,
                'coveredByReinsurer': 1d)
    }
    wxl {
        parmContractStrategy[allPeriods] = new WXLContractStrategy(
                'attachmentPoint': 2000d,
                'limit': 20000d,
                'aggregateLimit': 100000d,
                'premiumBase': PremiumBase.ABSOLUTE,
                'premium': 1000d,
                'reinstatementPremiums': new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']),
                'coveredByReinsurer': 1d)
    }
}
