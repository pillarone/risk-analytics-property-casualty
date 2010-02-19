package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints
import org.pillarone.riskanalytics.core.parameterization.*

/**
 * @author shartmann (at) munichre (dot) com
 */
public class CommissionStrategyType extends AbstractParameterObjectClassifier {

    public static final CommissionStrategyType FIXEDCOMMISSION = new CommissionStrategyType("fixed commission", "FIXEDCOMMISSION", ['commission':0d])
    public static final CommissionStrategyType SLIDINGCOMMISSION = new CommissionStrategyType("sliding commission", "SLIDINGCOMMISSION",
           ['bandCommission': new ConstrainedMultiDimensionalParameter(
                        [[0d], [0d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
    public static final CommissionStrategyType PROFITCOMMISSION = new CommissionStrategyType("profit commission", "PROFITCOMMISSION",
            ['profitCommissionRatio':0d, 'costRatio':0d, 'lossCarriedForwardEnabled':true, 'initialLossCarriedForward':0d])

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
            case CommissionStrategyType.PROFITCOMMISSION:
                commissionStrategy = new ProfitCommissionStrategy(
                        profitCommissionRatio : (Double) parameters['profitCommissionRatio'],
                        costRatio : (Double) parameters['costRatio'],
                        lossCarriedForwardEnabled : (Boolean) parameters['lossCarriedForwardEnabled'],
                        initialLossCarriedForward : (Double) parameters['initialLossCarriedForward'])
                break;
            case CommissionStrategyType.SLIDINGCOMMISSION:
                commissionStrategy = new SlidingCommissionStrategy(commissionBands: (ConstrainedMultiDimensionalParameter) parameters['commissionBands'])
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