package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.AbsoluteReservesGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.IReservesGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.InitialReservesGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.PriorPeriodReservesGeneratorStrategy
import org.pillarone.riskanalytics.core.parameter.MultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author shartmann (at) munichre (dot) com
 */
public class CommissionStrategyType extends AbstractParameterObjectClassifier {

    public static final CommissionStrategyType FIXEDCOMMISSION = new CommissionStrategyType("fixed commission", "FIXEDCOMMISSION", ['commission':0d])
    public static final CommissionStrategyType SLIDINGCOMMISSION = new CommissionStrategyType("sliding commission", "SLIDINGCOMMISSION",
            ['bandCommission':new TableMultiDimensionalParameter([[0d], [0d]],['claim level (from)', 'commission rate'])])
    public static final CommissionStrategyType PROFITCOMMISSION = new CommissionStrategyType("profit commission", "PROFITCOMMISSION",
            ['profitCommissionRatio':0d,
             'costRatio':0d,
             'cededIncurredClaims':0d,
             'lossCarriedForward':0d
            ])

    public static final all = [FIXEDCOMMISSION, SLIDINGCOMMISSION, PROFITCOMMISSION]

    protected static Map types = [:]
    static {
        CommissionStrategyType.all.each {
            CommissionStrategyType.types[it.toString()] = it
        }
    }

    private CommissionStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static CommissionStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static ICommissionStrategy getStrategy(CommissionStrategyType type, Map parameters) {
        ICommissionStrategy commissionStrategy ;
        switch (type) {
            case CommissionStrategyType.FIXEDCOMMISSION:
                commissionStrategy = new FixedCommissionStrategy(commission : (Double) parameters['commission'])
                break;
        }
        return commissionStrategy;
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
        return "org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType.getStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}