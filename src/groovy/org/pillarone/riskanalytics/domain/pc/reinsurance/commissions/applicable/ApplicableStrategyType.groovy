package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class ApplicableStrategyType extends AbstractParameterObjectClassifier {

    public static final ApplicableStrategyType ALL = new ApplicableStrategyType("all", "ALL", [:])
    public static final ApplicableStrategyType NONE = new ApplicableStrategyType("none", "NONE", [:])
    public static final ApplicableStrategyType CONTRACT = new ApplicableStrategyType('contract', "CONTRACT",
            ['applicableContracts': new ComboBoxTableMultiDimensionalParameter([], ['Applicable Contracts'], IReinsuranceContractMarker.class)])

    public static final all = [ALL, NONE, CONTRACT];
    
    protected static Map types = [:]
    static {
        ApplicableStrategyType.all.each {
            ApplicableStrategyType.types[it.toString()] = it
        }
    }

    private ApplicableStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ApplicableStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static IApplicableStrategy getStrategy(ApplicableStrategyType type, Map parameters) {
        IApplicableStrategy commissionStrategy ;
        switch (type) {
            case ApplicableStrategyType.ALL:
                commissionStrategy = new AllApplicableStrategy()
                break
            case ApplicableStrategyType.NONE:
                commissionStrategy = new NoneApplicableStrategy()
                break
            case ApplicableStrategyType.CONTRACT:
                commissionStrategy = new ContractApplicableStrategy(applicableContracts: (ComboBoxTableMultiDimensionalParameter) parameters['applicableContracts'])
                break
            default:
                throw new InvalidParameterException("ApplicableStrategyType $type not implemented")
        }
        return commissionStrategy;
    }
}
