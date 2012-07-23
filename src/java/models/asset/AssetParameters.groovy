package models.asset

import org.pillarone.riskanalytics.domain.assets.TermStructureType
import org.pillarone.riskanalytics.domain.assets.constants.BondType
import org.pillarone.riskanalytics.domain.assets.constants.Rating
import org.pillarone.riskanalytics.domain.assets.constants.Seniority

model = models.asset.AssetModel
periodCount = 1
displayName = 'Test'
components {
    bonds {
        subSwiss {
            parmBondType[0] = BondType.CORPORATE_BOND
            parmQuantity[0] = 2000.0
            parmRating[0] = Rating.AAA
            parmBookValue[0] = 300.0
            parmInstallmentPeriod[0] = 'YEARLY'
            parmMaturityDate[0] = '2011-12-31'
            parmFaceValue[0] = 1000.0
            parmSeniority[0] = Seniority.SENIOR_SECURED
            parmPurchasePrice[0] = 200.0
            parmCoupon[0] = 10.0
        }
        subFrance {
            parmPurchasePrice[0] = 100.0
            parmSeniority[0] = Seniority.SENIOR_SECURED
            parmBookValue[0] = 100.0
            parmInstallmentPeriod[0] = 'YEARLY'
            parmQuantity[0] = 1000.0
            parmMaturityDate[0] = '2011-12-31'
            parmFaceValue[0] = 1000.0
            parmBondType[0] = BondType.CORPORATE_BOND
            parmCoupon[0] = 10.0
            parmRating[0] = Rating.AAA
        }
    }
    fees {
        parmBondDepositFeesPercentageOfMarketValue[0] = 0.0005
        parmBondTransactionCostsRate[0] = 0.0005
    }
    treasury {
        parmMaximumCashLevel[0] = 100000.0
        parmMinimumCashLevel[0] = 1000.0
        parmInitialCash[0] = 10000.0
    }
    yieldCurve {
        parmModellingStrategy[0] = TermStructureType.getStrategy(TermStructureType.CIR, ["meanReversionParameter": 0.01, "riskAversionParameter": 0.0, "longRunMean": 0.08, "volatility": 0.05, "initialInterestRate": 0.05,])
    }
}
