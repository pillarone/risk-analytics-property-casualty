package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.LPTPremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.ILimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase

class ReinsuranceContractType extends AbstractParameterObjectClassifier {


    public static final ReinsuranceContractType QUOTASHARE = new ReinsuranceContractType("quota share", "QUOTASHARE",
            ["quotaShare": 0d, "limit": LimitStrategyType.noLimit, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType SURPLUS = new ReinsuranceContractType("surplus", "SURPLUS", ["retention": 0d,
            "lines": 0d, "defaultCededLossShare": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType SURPLUSCOMPLEMENTARY = new ReinsuranceContractType("surplus complementary", "SURPLUSCOMPLEMENTARY", ["retention": 0d,
            "lines": 0d, "defaultCededLossShare": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType SURPLUS2 = new ReinsuranceContractType("surplus 2", "SURPLUS2",
            ["retention": 0d, "lines": 0, "commission": 0d, "alpha": 0d, "beta": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType WXL = new ReinsuranceContractType("wxl", "WXL", ["attachmentPoint": 0d, "limit": 0d,
            "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]), "coveredByReinsurer": 1d,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType CXL = new ReinsuranceContractType("cxl", "CXL", ["attachmentPoint": 0d, "limit": 0d,
            "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]), "coveredByReinsurer": 1d,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType WCXL = new ReinsuranceContractType("wcxl", "WCXL", ["attachmentPoint": 0d, "limit": 0d,
            "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]), "coveredByReinsurer": 1d,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType STOPLOSS = new ReinsuranceContractType("stop loss", "STOPLOSS",
            ["stopLossContractBase": StopLossContractBase.ABSOLUTE, "attachmentPoint": 0d, "limit": 0d, "premium": 0d,
                    "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]), "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType TRIVIAL = new ReinsuranceContractType("trivial", "TRIVIAL", [:])
    public static final ReinsuranceContractType AGGREGATEXL = new ReinsuranceContractType("aggregate xl", "AggregateXL",
            ["attachmentPoint": 0d, "limit": 0d, "premiumBase": PremiumBase.ABSOLUTE,
                    "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]), "premium": 0d, "coveredByReinsurer": 1d, "claimClass": ClaimType.AGGREGATED_EVENT])
    public static final ReinsuranceContractType LOSSPORTFOLIOTRANSFER = new ReinsuranceContractType("loss portfolio transfer", "LOSSPORTFOLIOTRANSFER",
            ["quotaShare": 0d, "premiumBase": LPTPremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType ADVERSEDEVELOPMENTCOVER = new ReinsuranceContractType("adverse development cover", "ADVERSEDEVELOPMENTCOVER",
            ["attachmentPoint": 0d, "limit": 0d, "stopLossContractBase": StopLossContractBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType GOLDORAK = new ReinsuranceContractType("goldorak", "GOLDORAK", ["cxlAttachmentPoint": 0d, "cxlLimit": 0d,
            "cxlAggregateDeductible": 0d, "cxlAggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "coveredByReinsurer": 1d, "slAttachmentPoint": 0d, "slLimit": 0d, "goldorakSlThreshold": 0d,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])

    public static final all = [QUOTASHARE, SURPLUS, SURPLUSCOMPLEMENTARY, WXL, CXL,
            WCXL, STOPLOSS, TRIVIAL, LOSSPORTFOLIOTRANSFER, ADVERSEDEVELOPMENTCOVER, GOLDORAK]

    protected static Map types = [:]
    static {
        ReinsuranceContractType.all.each {
            ReinsuranceContractType.types[it.toString()] = it
        }
    }

    private ReinsuranceContractType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ReinsuranceContractType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    private static IReinsuranceContractStrategy getQuotaShare(double quotaShare, ILimitStrategy limit, double coveredByReinsurer) {
        return new QuotaShareContractStrategy(quotaShare: quotaShare, limit: limit, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getStopLoss(StopLossContractBase stopLossContractBase, double attachmentPoint, double limit,
                                                            double premium, IPremiumAllocationStrategy premiumAllocation,
                                                            double coveredByReinsurer) {
        return new StopLossContractStrategy(stopLossContractBase: stopLossContractBase, attachmentPoint: attachmentPoint, limit: limit,
                premium: premium, premiumAllocation: premiumAllocation, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getSurplus(double retention, double lines,
                                                           double defaultCededLossShare, double coveredByReinsurer) {
        return new SurplusContractStrategy(retention: retention, lines: lines,
                defaultCededLossShare: defaultCededLossShare, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getComplementarySurplus(double retention, double lines,
                                                           double defaultCededLossShare, double coveredByReinsurer) {
        return new ComplementarySurplusContractStrategy(retention: retention, lines: lines,
                defaultCededLossShare: defaultCededLossShare, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getWXL(double attachmentPoint, double limit, double aggregateLimit,
                                                       double aggregateDeductible, PremiumBase premiumBase, double premium,
                                                       IPremiumAllocationStrategy premiumAllocation,
                                                       AbstractMultiDimensionalParameter reinstatementPremiums,
                                                       double coveredByReinsurer) {
        return new WXLContractStrategy(attachmentPoint: attachmentPoint, limit: limit, aggregateLimit: aggregateLimit,
                aggregateDeductible: aggregateDeductible, premiumBase: premiumBase, premium: premium, premiumAllocation: premiumAllocation,
                reinstatementPremiums: reinstatementPremiums, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getCXL(double attachmentPoint, double limit, double aggregateLimit,
                                                       double aggregateDeductible, PremiumBase premiumBase, double premium,
                                                       IPremiumAllocationStrategy premiumAllocation,
                                                       AbstractMultiDimensionalParameter reinstatementPremiums,
                                                       double coveredByReinsurer) {
        return new CXLContractStrategy(attachmentPoint: attachmentPoint, limit: limit, aggregateLimit: aggregateLimit,
                aggregateDeductible: aggregateDeductible, premiumBase: premiumBase, premium: premium,
                premiumAllocation: premiumAllocation, reinstatementPremiums: reinstatementPremiums, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getWCXL(double attachmentPoint, double limit, double aggregateLimit,
                                                        double aggregateDeductible, PremiumBase premiumBase, double premium,
                                                        IPremiumAllocationStrategy premiumAllocation,
                                                        AbstractMultiDimensionalParameter reinstatementPremiums,
                                                        double coveredByReinsurer) {
        return new WCXLContractStrategy(attachmentPoint: attachmentPoint, limit: limit, aggregateLimit: aggregateLimit,
                aggregateDeductible: aggregateDeductible, premiumBase: premiumBase, premium: premium, premiumAllocation: premiumAllocation,
                reinstatementPremiums: reinstatementPremiums, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getLossPortfolioTransferContractStrategy(double quotaShare, LPTPremiumBase premiumBase,
                                                                                         double premium, double coveredByReinsurer) {
        return new LossPortfolioTransferContractStrategy(quotaShare: quotaShare, premiumBase: premiumBase, premium: premium, coveredByReinsurer: coveredByReinsurer)
    }

    private static IReinsuranceContractStrategy getAdverseDevelopmentCover(StopLossContractBase stopLossContractBase, double attachmentPoint,
                                                                           double limit, double premium, double coveredByReinsurer) {
        return new AdverseDevelopmentCoverContractStrategy(stopLossContractBase: stopLossContractBase, attachmentPoint: attachmentPoint,
                limit: limit, premium: premium, coveredByReinsurer: coveredByReinsurer)
    }

    public static IReinsuranceContractStrategy getTrivial() {
        return new TrivialContractStrategy()
    }

    private static IReinsuranceContractStrategy getGoldorak(double attachmentPoint, double limit, double cxlAggregateDeductible,
                                                            double cxlAggregateLimit, PremiumBase premiumBase, double premium,
                                                            AbstractMultiDimensionalParameter reinstatementPremiums,
                                                            double coveredByReinsurer, double slAttachmentPoint,
                                                            double slLimit, double goldorakSlThreshold) {
        return new GoldorakContractStrategy(cxlAttachmentPoint: attachmentPoint, cxlLimit: limit, cxlAggregateDeductible:
        cxlAggregateDeductible, cxlAggregateLimit: cxlAggregateLimit,
                premiumBase: premiumBase, premium: premium, reinstatementPremiums: reinstatementPremiums,
                coveredByReinsurer: coveredByReinsurer, slAttachmentPoint: slAttachmentPoint, slLimit: slLimit,
                goldorakSlThreshold: goldorakSlThreshold)
    }

    static IReinsuranceContractStrategy getStrategy(ReinsuranceContractType type, Map parameters) {
        IReinsuranceContractStrategy contract
        switch (type) {
            case ReinsuranceContractType.QUOTASHARE:
                if (parameters.containsKey("limit")) {
                    contract = getQuotaShare((double) parameters["quotaShare"], (ILimitStrategy) parameters["limit"], (double) parameters["coveredByReinsurer"])
                }
                else {
                    contract = getQuotaShare((double) parameters["quotaShare"], (ILimitStrategy) LimitStrategyType.noLimit, (double) parameters["coveredByReinsurer"])
                }
//                contract = getQuotaShare(
//                        (double) parameters["quotaShare"],
//                        (ILimitStrategy) (parameters.containsKey("limit") ? parameters["limit"] : LimitStrategyType.noLimit),
//                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.WXL:
                contract = getWXL(
                        (double) parameters["attachmentPoint"],
                        (double) parameters["limit"],
                        (double) parameters["aggregateLimit"],
                        (double) parameters["aggregateDeductible"] == null ? 0 : (double) parameters["aggregateDeductible"],
                        (PremiumBase) parameters["premiumBase"],
                        (double) parameters["premium"],
                        (IPremiumAllocationStrategy) parameters["premiumAllocation"],
                        (AbstractMultiDimensionalParameter) parameters["reinstatementPremiums"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.CXL:
                contract = getCXL(
                        (double) parameters["attachmentPoint"],
                        (double) parameters["limit"],
                        (double) parameters["aggregateLimit"],
                        (double) parameters["aggregateDeductible"] == null ? 0 : (double) parameters["aggregateDeductible"],
                        (PremiumBase) parameters["premiumBase"],
                        (double) parameters["premium"],
                        (IPremiumAllocationStrategy) parameters["premiumAllocation"],
                        (AbstractMultiDimensionalParameter) parameters["reinstatementPremiums"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.WCXL:
                contract = getWCXL(
                        (double) parameters["attachmentPoint"],
                        (double) parameters["limit"],
                        (double) parameters["aggregateLimit"],
                        (double) parameters["aggregateDeductible"] == null ? 0 : (double) parameters["aggregateDeductible"],
                        (PremiumBase) parameters["premiumBase"],
                        (double) parameters["premium"],
                        (IPremiumAllocationStrategy) parameters["premiumAllocation"],
                        (AbstractMultiDimensionalParameter) parameters["reinstatementPremiums"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.STOPLOSS:
                contract = getStopLoss(
                        (StopLossContractBase) parameters["stopLossContractBase"],
                        (double) parameters["attachmentPoint"],
                        (double) parameters["limit"],
                        (double) parameters["premium"],
                        (IPremiumAllocationStrategy) parameters["premiumAllocation"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.SURPLUS:
                contract = getSurplus(
                        (double) parameters["retention"],
                        (double) parameters["lines"],
                        (double) parameters["defaultCededLossShare"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.SURPLUSCOMPLEMENTARY:
                contract = getComplementarySurplus(
                        (double) parameters["retention"],
                        (double) parameters["lines"],
                        (double) parameters["defaultCededLossShare"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.TRIVIAL:
                contract = getTrivial()
                break
            case ReinsuranceContractType.LOSSPORTFOLIOTRANSFER:
                contract = getLossPortfolioTransferContractStrategy(
                        (double) parameters["quotaShare"],
                        (LPTPremiumBase) parameters["premiumBase"],
                        (double) parameters["premium"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.ADVERSEDEVELOPMENTCOVER:
                contract = getAdverseDevelopmentCover(
                        (StopLossContractBase) parameters["stopLossContractBase"],
                        (double) parameters["attachmentPoint"],
                        (double) parameters["limit"],
                        (double) parameters["premium"],
                        (double) parameters["coveredByReinsurer"])
                break
            case ReinsuranceContractType.GOLDORAK:
                contract = getGoldorak(
                        (double) parameters["cxlAttachmentPoint"],
                        (double) parameters["cxlLimit"],
                        (double) parameters["cxlAggregateDeductible"] == null ? 0 : (double) parameters["cxlAggregateDeductible"],
                        (double) parameters["cxlAggregateLimit"],
                        (PremiumBase) parameters["premiumBase"],
                        (double) parameters["premium"],
                        (AbstractMultiDimensionalParameter) parameters["reinstatementPremiums"],
                        (double) parameters["coveredByReinsurer"],
                        (double) parameters["slAttachmentPoint"],
                        (double) parameters["slLimit"],
                        (double) parameters["goldorakSlThreshold"])
                break
        }
        return contract
    }
}