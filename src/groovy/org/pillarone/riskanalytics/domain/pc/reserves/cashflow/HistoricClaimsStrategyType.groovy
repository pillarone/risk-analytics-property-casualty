package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;


import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class HistoricClaimsStrategyType extends AbstractParameterObjectClassifier {

    public static final HistoricClaimsStrategyType NONE = new HistoricClaimsStrategyType('none', 'NONE', [:])
    public static final HistoricClaimsStrategyType LAST_PAID = new HistoricClaimsStrategyType("last paids", "LAST_PAID", [
            paidByDevelopmentPeriod : new TableMultiDimensionalParameter([[0d], [1]], ['Paids','Development Periods'])
    ])
    public static final HistoricClaimsStrategyType LAST_REPORTED = new HistoricClaimsStrategyType("last reported", "LAST_REPORTED", [
            reportedByDevelopmentPeriod : new TableMultiDimensionalParameter([[0d], [1]], ['Reported','Development Periods'])
    ])

    public static final all = [NONE, LAST_PAID, LAST_REPORTED]

    protected static Map types = [:]
    static {
        HistoricClaimsStrategyType.all.each {
            HistoricClaimsStrategyType.types[it.toString()] = it
        }
    }

    private HistoricClaimsStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static HistoricClaimsStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IHistoricClaimsStrategy getStrategy(HistoricClaimsStrategyType type, Map parameters) {
        IHistoricClaimsStrategy historicClaimsStrategy;
        switch (type) {
            case HistoricClaimsStrategyType.NONE:
                historicClaimsStrategy = new NoHistoricClaimsStrategy()
                break;
            case HistoricClaimsStrategyType.LAST_PAID:
                historicClaimsStrategy = new HistoricLastPaidClaimsStrategy(paidByDevelopmentPeriod : parameters['paidByDevelopmentPeriod'])
                break;
            case HistoricClaimsStrategyType.LAST_REPORTED:
                historicClaimsStrategy = new HistoricLastReportedClaimsStrategy(reportedByDevelopmentPeriod : parameters['reportedByDevelopmentPeriod'])
                break;
        }
        return historicClaimsStrategy;
    }

    public String getConstructionString(Map parameters) {
        StringBuffer parameterString = new StringBuffer('[')
        parameters.each {k, v ->
            if (v.class.isEnum()) {
                parameterString << "\"$k\":${v.class.name}.$v,"
            }
            else if (v instanceof IParameterObject) {
                parameterString << "\"$k\":${v.type.getConstructionString(v.parameters)},"
            }
            else {
                parameterString << "\"$k\":$v,"
            }
        }
        if (parameterString.size() == 1) {
            parameterString << ':'
        }
        parameterString << ']'
        return "org.pillarone.riskanalytics.domain.pc.reserves.cashflow.HistoricClaimsStrategyType.getStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}