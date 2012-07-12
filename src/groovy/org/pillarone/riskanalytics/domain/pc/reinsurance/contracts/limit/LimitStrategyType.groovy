package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LimitStrategyType extends AbstractParameterObjectClassifier {

    public static final LimitStrategyType NONE = new LimitStrategyType("none", "NONE", [:])
    public static final LimitStrategyType AAL = new LimitStrategyType("AAL", "AAL", ['aal': 0d])
    public static final LimitStrategyType AAD = new LimitStrategyType("AAD", "AAD", ['aad': 0d])
    public static final LimitStrategyType AALAAD = new LimitStrategyType("AAL, AAD", "AALAAD", ['aal': 0d, 'aad': 0d])
    public static final LimitStrategyType EVENTLIMIT = new LimitStrategyType("event", "EVENTLIMIT", ['eventLimit': 0d])
    public static final LimitStrategyType EVENTLIMITAAL = new LimitStrategyType("event, AAL", "EVENTLIMITAAL", ['aal': 0d, 'eventLimit': 0d])

    public static final all = [NONE, AAL, AAD, AALAAD, EVENTLIMIT, EVENTLIMITAAL]

    protected static Map types = [:]
    static {
        LimitStrategyType.all.each {
            LimitStrategyType.types[it.toString()] = it
        }
    }

    private LimitStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static LimitStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        all
    }

    public IParameterObject getParameterObject(Map parameters) {
        getStrategy(this, parameters)
    }

    public static ILimitStrategy getNoLimit() {
        getStrategy(LimitStrategyType.NONE, [:])
    }

    public static ILimitStrategy getStrategy(LimitStrategyType type, Map parameters) {
        ILimitStrategy limitStrategy ;
        switch (type) {
            case LimitStrategyType.NONE:
                limitStrategy = new NoneLimitStrategy()
                break
            case LimitStrategyType.AAL:
                limitStrategy = new AalLimitStrategy(aal: (Double) parameters['aal'])
                break
            case LimitStrategyType.AAD:
                limitStrategy = new AadLimitStrategy(aad: (Double) parameters['aad'])
                break
            case LimitStrategyType.AALAAD:
                limitStrategy = new AalAadLimitStrategy(aal: (Double) parameters['aal'], aad: (Double) parameters['aad'])
                break
            case LimitStrategyType.EVENTLIMIT:
                limitStrategy = new EventLimitStrategy(eventLimit: (Double) parameters['eventLimit'])
                break
            case LimitStrategyType.EVENTLIMITAAL:
                limitStrategy = new EventAalLimitStrategy(eventLimit: (Double) parameters['eventLimit'], aal: (Double) parameters['aal'])
                break
            default:
                throw new InvalidParameterException("LimitStrategyType $type not implemented")
        }
        limitStrategy;
    }
}