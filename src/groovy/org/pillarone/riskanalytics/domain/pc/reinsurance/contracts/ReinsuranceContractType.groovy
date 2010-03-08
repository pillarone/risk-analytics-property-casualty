package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.constants.LPTPremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType

class ReinsuranceContractType extends AbstractParameterObjectClassifier {


    public static final ReinsuranceContractType QUOTASHARE = new ReinsuranceContractType("quota share", "QUOTASHARE",
        ["quotaShare": 0d, "limit": LimitStrategyType.noLimit, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType SURPLUS = new ReinsuranceContractType("surplus", "SURPLUS", ["retention": 0d,
        "lines": 0d, "defaultCededLossShare": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType SURPLUS2 = new ReinsuranceContractType("surplus 2", "SURPLUS2",
        ["retention": 0d, "lines": 0, "commission": 0d, "alpha": 0d, "beta": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType WXL = new ReinsuranceContractType("wxl", "WXL", ["attachmentPoint": 0d, "limit": 0d,
        "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d,
        "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType CXL = new ReinsuranceContractType("cxl", "CXL", ["attachmentPoint": 0d, "limit": 0d,
        "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d,
        "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType WCXL = new ReinsuranceContractType("wcxl", "WCXL", ["attachmentPoint": 0d, "limit": 0d,
        "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d,
        "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType STOPLOSS = new ReinsuranceContractType("stop loss", "STOPLOSS",
        ["attachmentPoint": 0d, "limit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType TRIVIAL = new ReinsuranceContractType("trivial", "TRIVIAL", [:])

    public static final ReinsuranceContractType AGGREGATEXL = new ReinsuranceContractType("aggregate xl", "AggregateXL",
        ["attachmentPoint": 0d, "limit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d, "claimClass": ClaimType.AGGREGATED_EVENT])

    public static final ReinsuranceContractType LOSSPORTFOLIOTRANSFER = new ReinsuranceContractType("loss portfolio transfer", "LOSSPORTFOLIOTRANSFER",
        ["quotaShare": 0d, "premiumBase": LPTPremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType ADVERSEDEVELOPMENTCOVER = new ReinsuranceContractType("adverse development cover", "ADVERSEDEVELOPMENTCOVER",
        ["attachmentPoint": 0d, "limit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d])

    public static final all = [QUOTASHARE, SURPLUS, WXL, CXL,
        WCXL, STOPLOSS, TRIVIAL, AGGREGATEXL, LOSSPORTFOLIOTRANSFER, ADVERSEDEVELOPMENTCOVER]

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
        return ReinsuranceContractStrategyFactory.getContractStrategy(this, parameters)
    }

    public String getConstructionString(Map parameters) {
        StringBuffer parameterString = new StringBuffer('[')
        parameters.each {k, v ->
            if (v.class.isEnum()) {
                parameterString << "\"$k\":${v.class.name}.$v,"
            } else if (v instanceof IParameterObject) {
                parameterString << "\"$k\":${v.type.getConstructionString(v.parameters)},"
            } else {
                parameterString << "\"$k\":$v,"
            }
        }
        if (parameterString.size() == 1) {
            parameterString << ':'
        }
        parameterString << ']'
        "org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory.getContractStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}