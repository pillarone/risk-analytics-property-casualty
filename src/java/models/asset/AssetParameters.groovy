package models.asset

import org.pillarone.riskanalytics.domain.assets.constants.Seniority
import org.pillarone.riskanalytics.domain.assets.constants.Rating
import org.pillarone.riskanalytics.domain.assets.constants.BondType
import org.pillarone.riskanalytics.domain.assets.TermStructureType
import org.pillarone.riskanalytics.domain.assets.ModellingStrategyFactory

model = models.asset.AssetModel
periodCount = 3
displayName = 'Test'
components {
    bonds {
        subSwiss {
            parmBondType[0] = BondType.CORPORATE_BOND
            parmBondType[2] = BondType.CORPORATE_BOND
            parmBondType[1] = BondType.CORPORATE_BOND
            parmQuantity[2] = 2000.0
            parmQuantity[1] = 0.0
            parmQuantity[0] = 2000.0
            parmRating[0] = Rating.AAA
            parmRating[1] = Rating.AAA
            parmRating[2] = Rating.AAA
            parmBookValue[1] = 0.0
            parmBookValue[2] = 300.0
            parmBookValue[0] = 300.0
            parmInstallmentPeriod[0] = 'YEARLY'
            parmInstallmentPeriod[2] = 'YEARLY'
            parmInstallmentPeriod[1] = 'YEARLY'
            parmMaturityDate[0] = '2011-12-31'
            parmMaturityDate[2] = '2014-12-31'
            parmMaturityDate[1] = '2011-12-31'
            parmFaceValue[2] = 1000.0
            parmFaceValue[1] = 0.0
            parmFaceValue[0] = 1000.0
            parmSeniority[1] = Seniority.SENIOR_SECURED
            parmSeniority[0] = Seniority.SENIOR_SECURED
            parmSeniority[2] = Seniority.SENIOR_SECURED
            parmPurchasePrice[0] = 200.0
            parmPurchasePrice[1] = 20.0
            parmPurchasePrice[2] = 200.0
            parmCoupon[0] = 10.0
            parmCoupon[1] = 10.0
            parmCoupon[2] = 10.0
        }
        subFrance {
            parmPurchasePrice[1] = 20.0
            parmPurchasePrice[2] = 20.0
            parmPurchasePrice[0] = 100.0
            parmSeniority[2] = Seniority.SENIOR_SECURED
            parmSeniority[1] = Seniority.SENIOR_SECURED
            parmSeniority[0] = Seniority.SENIOR_SECURED
            parmBookValue[1] = 0.0
            parmBookValue[2] = 0.0
            parmBookValue[0] = 100.0
            parmInstallmentPeriod[1] = 'YEARLY'
            parmInstallmentPeriod[2] = 'YEARLY'
            parmInstallmentPeriod[0] = 'YEARLY'
            parmQuantity[0] = 1000.0
            parmQuantity[2] = 0.0
            parmQuantity[1] = 0.0
            parmMaturityDate[2] = '2014-12-31'
            parmMaturityDate[0] = '2011-12-31'
            parmMaturityDate[1] = '2011-12-31'
            parmFaceValue[0] = 1000.0
            parmFaceValue[2] = 0.0
            parmFaceValue[1] = 0.0
            parmBondType[1] = BondType.CORPORATE_BOND
            parmBondType[0] = BondType.CORPORATE_BOND
            parmBondType[2] = BondType.CORPORATE_BOND
            parmCoupon[2] = 10.0
            parmCoupon[1] = 10.0
            parmCoupon[0] = 10.0
            parmRating[1] = Rating.AAA
            parmRating[2] = Rating.AAA
            parmRating[0] = Rating.AAA
        }
    }
    fees {
        parmBondDepositFeesPercentageOfMarketValue[1] = 0.0005
        parmBondDepositFeesPercentageOfMarketValue[0] = 0.0005
        parmBondDepositFeesPercentageOfMarketValue[2] = 0.0005
        parmBondTransactionCostsRate[2] = 0.0005
        parmBondTransactionCostsRate[1] = 0.0005
        parmBondTransactionCostsRate[0] = 0.0005
    }
    treasury {
        parmMaximumCashLevel[0] = 100000.0
        parmMaximumCashLevel[1] = 100000.0
        parmMaximumCashLevel[2] = 100000.0
        parmMinimumCashLevel[2] = 1000.0
        parmMinimumCashLevel[0] = 1000.0
        parmMinimumCashLevel[1] = 1000.0
        parmInitialCash[0] = 10000.0
        parmInitialCash[1] = 10000.0
        parmInitialCash[2] = 10000.0
    }
    yieldCurve {
        parmModellingStrategy[2] = ModellingStrategyFactory.getModellingStrategy(TermStructureType.CIR, ["meanReversionParameter": 0.01, "riskAversionParameter": 0.0, "longRunMean": 0.08, "volatility": 0.05, "initialInterestRate": 0.05,])
        parmModellingStrategy[1] = ModellingStrategyFactory.getModellingStrategy(TermStructureType.CIR, ["meanReversionParameter": 0.01, "riskAversionParameter": 0.0, "longRunMean": 0.08, "volatility": 0.05, "initialInterestRate": 0.05,])
        parmModellingStrategy[0] = ModellingStrategyFactory.getModellingStrategy(TermStructureType.CIR, ["meanReversionParameter": 0.01, "riskAversionParameter": 0.0, "longRunMean": 0.08, "volatility": 0.05, "initialInterestRate": 0.05,])
    }
}
