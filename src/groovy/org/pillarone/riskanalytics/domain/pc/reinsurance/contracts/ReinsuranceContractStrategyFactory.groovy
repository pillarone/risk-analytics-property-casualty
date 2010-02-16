package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContractStrategyFactory {

    private static IReinsuranceContractStrategy getQuotaShare(double quotaShare, double commission, double coveredByReinsurer) {
        return new QuotaShareContractStrategy(quotaShare: quotaShare, commission: commission, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getQuotaShareProfitCommission(double quotaShare, double commission, double coveredByReinsurer,
                                                                              double expensesOfReinsurer, double profitCommission) {
           return new QuotaShareProfitCommissionContractStrategy(quotaShare: quotaShare, commission: commission, coveredByReinsurer: coveredByReinsurer,
                                                                 expensesOfReinsurer: expensesOfReinsurer, profitCommission: profitCommission)
       }

    private static IReinsuranceContractStrategy getQuotaShareSlidingScale(double quotaShare, double commission, double coveredByReinsurer,
                                                                          double claimLevel1, double claimLevel2, double rateLevel1, double rateLevel2, double rateLevel3) {
        return new QuotaShareSlidingScaleContractStrategy(quotaShare: quotaShare, commission: commission, coveredByReinsurer: coveredByReinsurer,
            claimLevel1: claimLevel1, claimLevel2: claimLevel2, rateLevel1: rateLevel1, rateLevel2: rateLevel2, rateLevel3: rateLevel3)
    }

    private static IReinsuranceContractStrategy getQuotaShareMultiSlidingScale(double quotaShare, double commission, double coveredByReinsurer,
                                                                               TableMultiDimensionalParameter levels) {
        return new QuotaShareMultiSlidingScaleContractStrategy(quotaShare: quotaShare, commission: commission, coveredByReinsurer: coveredByReinsurer,
            levels: levels)
    }

    private static IReinsuranceContractStrategy getQuotaShareEventLimit(double quotaShare, double eventLimit, double commission,
                                                                        double coveredByReinsurer) {
        return new QuotaShareEventLimitContractStrategy(quotaShare: quotaShare, eventLimit: eventLimit, commission: commission,
            coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getQuotaShareEventLimitAAL(double quotaShare, double eventLimit, double annualAggregateLimit,
                                                                           double commission, double coveredByReinsurer) {
        return new QuotaShareEventLimitAALContractStrategy(quotaShare: quotaShare, eventLimit: eventLimit, annualAggregateLimit: annualAggregateLimit,
            commission: commission, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getQuotaShareAAL(double quotaShare, double annualAggregateLimit,
                                                                 double commission, double coveredByReinsurer) {
        return new QuotaShareAALContractStrategy(quotaShare: quotaShare, annualAggregateLimit: annualAggregateLimit,
            commission: commission, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getQuotaShareAAD(double quotaShare, double annualAggregateDeductible,
                                                                 double commission, double coveredByReinsurer) {
        return new QuotaShareAADContractStrategy(quotaShare: quotaShare, annualAggregateDeductible: annualAggregateDeductible,
            commission: commission, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getQuotaShareAADAAL(double quotaShare, double annualAggregateDeductible,
                                                                    double annualAggregateLimit, double commission,
                                                                    double coveredByReinsurer) {
        return new QuotaShareAADAALContractStrategy(quotaShare: quotaShare, annualAggregateDeductible: annualAggregateDeductible,
            annualAggregateLimit: annualAggregateLimit, commission: commission, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getStopLoss(double attachmentPoint, double limit, PremiumBase premiumBase,
                                                            double premium, double coveredByReinsurer) {
        return new StopLossContractStrategy(attachmentPoint: attachmentPoint, limit: limit, premiumBase: premiumBase,
            premium: premium, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getSurplus(double retention, double lines, double commission,
                                                           double defaultCededLossShare, double coveredByReinsurer) {
        return new SurplusContractStrategy(retention: retention, lines: lines, commission: commission,
            defaultCededLossShare: defaultCededLossShare, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getWXL(double attachmentPoint, double limit, double aggregateLimit,
                                                       PremiumBase premiumBase, double premium,
                                                       AbstractMultiDimensionalParameter reinstatementPremiums,
                                                       double coveredByReinsurer) {
        return new WXLContractStrategy(attachmentPoint: attachmentPoint, limit: limit, aggregateLimit: aggregateLimit,
            premiumBase: premiumBase, premium: premium, reinstatementPremiums: reinstatementPremiums,
            coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getCXL(double attachmentPoint, double limit, double aggregateLimit,
                                                       PremiumBase premiumBase, double premium,
                                                       AbstractMultiDimensionalParameter reinstatementPremiums,
                                                       double coveredByReinsurer) {
        return new CXLContractStrategy(attachmentPoint: attachmentPoint, limit: limit, aggregateLimit: aggregateLimit,
            premiumBase: premiumBase, premium: premium, reinstatementPremiums: reinstatementPremiums,
            coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getWCXL(double attachmentPoint, double limit, double aggregateLimit,
                                                        PremiumBase premiumBase, double premium,
                                                        AbstractMultiDimensionalParameter reinstatementPremiums,
                                                        double coveredByReinsurer) {
        return new WCXLContractStrategy(attachmentPoint: attachmentPoint, limit: limit, aggregateLimit: aggregateLimit,
            premiumBase: premiumBase, premium: premium, reinstatementPremiums: reinstatementPremiums,
            coveredByReinsurer: coveredByReinsurer)
    }
  private static IReinsuranceContractStrategy getLossPortfolioTransfer(double quotaShare, double commission, double coveredByReinsurer) {
      return new LossPortfolioTransferContractStrategy(quotaShare: quotaShare, commission: commission, coveredByReinsurer: coveredByReinsurer)
  }



    public static IReinsuranceContractStrategy getTrivial() {
        return new TrivialContractStrategy()
    }

    static IReinsuranceContractStrategy getContractStrategy(ReinsuranceContractType type, Map parameters) {
        IReinsuranceContractStrategy contract
        switch (type) {
            case ReinsuranceContractType.QUOTASHARE:
                contract = getQuotaShare(parameters["quotaShare"], parameters["commission"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.QUOTASHAREPROFITCOMMISSION:
                contract = getQuotaShareProfitCommission(parameters["quotaShare"], parameters["commission"], parameters["coveredByReinsurer"],
                    parameters["expensesOfReinsurer"], parameters["profitCommission"])
                break
            case ReinsuranceContractType.QUOTASHARESLIDINGSCALE:
                contract = getQuotaShareSlidingScale(parameters["quotaShare"], parameters["commission"], parameters["coveredByReinsurer"],
                    parameters["claimLevel1"], parameters["claimLevel2"], parameters["rateLevel1"], parameters["rateLevel2"], parameters["rateLevel3"])
                break
            case ReinsuranceContractType.QUOTASHAREMULTISLIDINGSCALE:
                contract = getQuotaShareMultiSlidingScale(parameters["quotaShare"], parameters["commission"], parameters["coveredByReinsurer"],
                    parameters["levels"])
                break
            case ReinsuranceContractType.QUOTASHAREEVENTLIMIT:
                contract = getQuotaShareEventLimit(parameters["quotaShare"], parameters["eventLimit"], parameters["commission"],
                    parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.QUOTASHAREEVENTLIMITAAL:
                contract = getQuotaShareEventLimitAAL(parameters["quotaShare"], parameters["eventLimit"], parameters["annualAggregateLimit"],
                    parameters["commission"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.QUOTASHAREAAL:
                contract = getQuotaShareAAL(parameters["quotaShare"], parameters["annualAggregateLimit"],
                    parameters["commission"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.QUOTASHAREAAD:
                contract = getQuotaShareAAD(parameters["quotaShare"], parameters["annualAggregateDeductible"],
                    parameters["commission"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.QUOTASHAREAADAAL:
                contract = getQuotaShareAADAAL(parameters["quotaShare"], parameters["annualAggregateDeductible"],
                    parameters["annualAggregateLimit"], parameters["commission"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.WXL:
                contract = getWXL(parameters["attachmentPoint"], parameters["limit"], parameters["aggregateLimit"],
                    parameters["premiumBase"], parameters["premium"], parameters["reinstatementPremiums"],
                    parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.CXL:
                contract = getCXL(parameters["attachmentPoint"], parameters["limit"], parameters["aggregateLimit"],
                    parameters["premiumBase"], parameters["premium"], parameters["reinstatementPremiums"],
                    parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.WCXL:
                contract = getWCXL(parameters["attachmentPoint"], parameters["limit"], parameters["aggregateLimit"],
                    parameters["premiumBase"], parameters["premium"], parameters["reinstatementPremiums"],
                    parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.STOPLOSS:
                contract = getStopLoss(parameters["attachmentPoint"], parameters["limit"], parameters["premiumBase"],
                    parameters["premium"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.SURPLUS:
                contract = getSurplus(parameters["retention"], parameters["lines"], parameters["commission"],
                    parameters["defaultCededLossShare"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.TRIVIAL:
                contract = getTrivial()
                break
           case ReinsuranceContractType.LOSSPORTFOLIOTRANSFER:
                contract = getLossPortfolioTransfer(parameters["quotaShare"], parameters["commission"], parameters["coveredByReinsurer"])
                break
        }
        return contract
    }
}