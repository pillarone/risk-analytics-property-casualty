package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

class ReinsuranceContractType extends AbstractParameterObjectClassifier {


    public static final ReinsuranceContractType QUOTASHARE = new ReinsuranceContractType("quota share", "QUOTASHARE",
        ["quotaShare": 0d, "commission": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType QUOTASHAREPROFITCOMMISSION = new ReinsuranceContractType("quota share profit commission", "QUOTASHAREPROFITCOMMISSION",
        ["quotaShare": 0d, "commission": 0d, "coveredByReinsurer": 1d, "expensesOfReinsurer": 0d, "profitCommission": 0d])
    public static final ReinsuranceContractType QUOTASHARESLIDINGSCALE = new ReinsuranceContractType("quota share sliding scale", "QUOTASHARESLIDINGSCALE",
        ["quotaShare": 0d, "commission": 0d, "coveredByReinsurer": 1d, "claimLevel1": 0d, "claimLevel2": 0d,
            "rateLevel1": 0d, "rateLevel2": 0d, "rateLevel3": 0d])
    public static final ReinsuranceContractType QUOTASHAREMULTISLIDINGSCALE = new ReinsuranceContractType("quota share multi sliding scale", "QUOTASHAREMULTISLIDINGSCALE",
        ["quotaShare": 0d, "commission": 0d, "coveredByReinsurer": 1d, "levels": [[0d, 0d, 0d], [0d, 0d, 0d]]])
    public static final ReinsuranceContractType QUOTASHAREEVENTLIMIT = new ReinsuranceContractType("quota share event limit", "QUOTASHAREEVENTLIMIT",
        ["quotaShare": 0d, "eventLimit": 1E100, "commission": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType QUOTASHAREEVENTLIMITAAL = new ReinsuranceContractType("quota share event limit, AAL", "QUOTASHAREEVENTLIMIT",
        ["quotaShare": 0d, "eventLimit": 1E100, "annualAggregateLimit": 0d, "commission": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType QUOTASHAREAAL = new ReinsuranceContractType("quota share aal", "QUOTASHAREAAL", [
        "quotaShare": 0d, "annualAggregateLimit": 0d, "commission": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType QUOTASHAREAAD = new ReinsuranceContractType("quota share aad", "QUOTASHAREAAD", [
        "quotaShare": 0d, "annualAggregateDeductible": 0d, "commission": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType QUOTASHAREAADAAL = new ReinsuranceContractType("quota share aad aal", "QUOTASHAREAADAAL", [
        "quotaShare": 0d, "annualAggregateDeductible": 0d, "annualAggregateLimit": 0d, "commission": 0d, "coveredByReinsurer": 1d])
    public static final ReinsuranceContractType SURPLUS = new ReinsuranceContractType("surplus", "SURPLUS", ["retention": 0d,
        "lines": 0d, "commission": 0d, "defaultCededLossShare": 0d, "coveredByReinsurer": 1d])
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

    public static final all = [QUOTASHARE, QUOTASHAREPROFITCOMMISSION, QUOTASHARESLIDINGSCALE, QUOTASHAREMULTISLIDINGSCALE, QUOTASHAREEVENTLIMIT, QUOTASHAREEVENTLIMITAAL, QUOTASHAREAAL, QUOTASHAREAAD,
        QUOTASHAREAADAAL, SURPLUS, WXL, CXL, WCXL, STOPLOSS, TRIVIAL, AGGREGATEXL]

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