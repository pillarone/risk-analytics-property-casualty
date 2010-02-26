package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.ILimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.constants.LPTPremiumBase

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContractStrategyFactory {

    private static IReinsuranceContractStrategy getQuotaShare(double quotaShare, ILimitStrategy limit, double coveredByReinsurer) {
        return new QuotaShareContractStrategy(quotaShare: quotaShare, limit: limit, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getStopLoss(double attachmentPoint, double limit, PremiumBase premiumBase,
                                                            double premium, double coveredByReinsurer) {
        return new StopLossContractStrategy(attachmentPoint: attachmentPoint, limit: limit, premiumBase: premiumBase,
                premium: premium, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getSurplus(double retention, double lines, 
                                                           double defaultCededLossShare, double coveredByReinsurer) {
        return new SurplusContractStrategy(retention: retention, lines: lines,
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

    private static IReinsuranceContractStrategy getLossPortfolioTransferContractStrategy(double quotaShare, LPTPremiumBase premiumBase,
                        double premium, double coveredByReinsurer) {
        return new LossPortfolioTransferContractStrategy(quotaShare: quotaShare, premiumBase: premiumBase, premium: premium, coveredByReinsurer: coveredByReinsurer)
    }

    public static IReinsuranceContractStrategy getTrivial() {
        return new TrivialContractStrategy()
    }

    static IReinsuranceContractStrategy getContractStrategy(ReinsuranceContractType type, Map parameters) {
        IReinsuranceContractStrategy contract
        switch (type) {
            case ReinsuranceContractType.QUOTASHARE:
                if (parameters["limit"]) {
                    contract = getQuotaShare(parameters["quotaShare"], parameters["limit"], parameters["coveredByReinsurer"])
                }
                else {
                    contract = getQuotaShare(parameters["quotaShare"], LimitStrategyType.noLimit, parameters["coveredByReinsurer"])
                }
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
                contract = getSurplus(parameters["retention"], parameters["lines"],
                        parameters["defaultCededLossShare"], parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.TRIVIAL:
                contract = getTrivial()
                break
            case ReinsuranceContractType.LOSSPORTFOLIOTRANSFER:
                contract = getLossPortfolioTransferContractStrategy(parameters["quotaShare"], parameters["premiumBase"],
                        parameters["premium"], parameters["coveredByReinsurer"])
                break
        }
        return contract
    }
}