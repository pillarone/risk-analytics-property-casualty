package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;


import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContractType extends AbstractParameterObjectClassifier {


    public static final ReinsuranceContractType QUOTASHARE = new ReinsuranceContractType("quota share", "QUOTASHARE",
        ["quotaShare": 0d, "commission": 0d])
    public static final ReinsuranceContractType WXL = new ReinsuranceContractType("wxl", "WXL", ["attachmentPoint": 0d, "limit": 0d,
        "annualLimit": 0d, "termLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
        "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType CXL = new ReinsuranceContractType("cxl", "CXL", ["attachmentPoint": 0d, "limit": 0d,
        "annualLimit": 0d, "termLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
        "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType STOPLOSS = new ReinsuranceContractType("stop loss", "STOPLOSS",
            ["attachmentPoint": 0d, "limit": 0d, "termLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d])
    public static final ReinsuranceContractType TRIVIAL = new ReinsuranceContractType("trivial", "TRIVIAL", [:])
    
    public static final all = [CXL, QUOTASHARE, STOPLOSS, TRIVIAL, WXL]

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
        return getContractStrategy(this, parameters)
    }

    public static IReinsuranceContractStrategy getTrivial() {
        return new TrivialContractStrategy()
    }

    static IReinsuranceContractStrategy getContractStrategy(ReinsuranceContractType type, Map parameters) {
        switch (type) {
            case ReinsuranceContractType.TRIVIAL:
                return new TrivialContractStrategy()
            case ReinsuranceContractType.QUOTASHARE:
                return new QuotaShareContractStrategy(quotaShare: parameters["quotaShare"], commission: parameters["commission"])
            case ReinsuranceContractType.WXL:
                return new WXLContractStrategy(attachmentPoint: parameters["attachmentPoint"], limit: parameters["limit"],
                        annualLimit: parameters["annualLimit"], termLimit: parameters["termLimit"],
                        premiumBase: parameters["premiumBase"], premium: parameters["premium"],
                        reinstatementPremiums: parameters["reinstatementPremiums"])
            case ReinsuranceContractType.CXL:
                return new CXLContractStrategy(attachmentPoint: parameters["attachmentPoint"], limit: parameters["limit"],
                        annualLimit: parameters["annualLimit"], termLimit: parameters["termLimit"],
                        premiumBase: parameters["premiumBase"], premium: parameters["premium"],
                        reinstatementPremiums: parameters["reinstatementPremiums"])
            case ReinsuranceContractType.STOPLOSS:
                return new StopLossContractStrategy(attachmentPoint: parameters["attachmentPoint"], limit: parameters["limit"],
                        termLimit: parameters["termLimit"], premiumBase: parameters["premiumBase"], premium: parameters["premium"])
        }
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
        "org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.ReinsuranceContractType.getContractStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}