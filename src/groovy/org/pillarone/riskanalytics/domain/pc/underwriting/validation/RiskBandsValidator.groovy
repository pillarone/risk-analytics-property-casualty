package org.pillarone.riskanalytics.domain.pc.underwriting.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBands
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.SurplusContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReverseSurplusContractStrategy
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class RiskBandsValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(RiskBandsValidator)

    private AbstractParameterValidationService validationService

    public RiskBandsValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        /** key: path  */
        Map<String, IReinsuranceContractStrategy> surplusContracts = [:]
        /** key: path  */
        Map<String, TableMultiDimensionalParameter> underwritingInfos = [:]

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof MultiDimensionalParameterHolder) {
                AbstractMultiDimensionalParameter value = parameter.getBusinessObject()  // parameter.value ??
                if (value instanceof TableMultiDimensionalParameter) {
                    List<String> titles = value.getColumnNames()  // value.titles ?
                    if (titles.contains(RiskBands.MAXIMUM_SUM_INSURED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug "validating ${parameter.path}"
                        }
                        underwritingInfos[parameter.path] = value

                        def currentErrors = validationService.validate(value, value.getColumnByName(RiskBands.MAXIMUM_SUM_INSURED))
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)
                    }
                }
            }
            else if (parameter instanceof ParameterObjectParameterHolder) {
                try {
                    IParameterObject parameterHolder = parameter.getBusinessObject()
                    if (parameterHolder instanceof SurplusContractStrategy
                        || parameterHolder instanceof ReverseSurplusContractStrategy) {
                        surplusContracts[parameter.path] = parameterHolder
                    }
                }
                catch (IllegalArgumentException ex) {
                    // https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1542
                    LOG.debug("call parameter.getBusinessObject() failed " + ex.toString())
                }
            }
        }
        if (!surplusContracts.isEmpty()) {
            for (Map.Entry<String, TableMultiDimensionalParameter> item: underwritingInfos.entrySet()) {
                String path = item.key
                List<Double> numberOfPolicies = item.value.getColumnByName(RiskBands.NUMBER_OF_POLICIES)
                for (int row = 0; row < numberOfPolicies.size(); row++) {
                    if (numberOfPolicies[row] > 0) continue
                    ParameterValidationImpl error = new ParameterValidationImpl(
                        'surplus.ri.needs.non.trivial.number.of.policies', [numberOfPolicies[row], row+1]
                    )
                    error.path = path
                    errors << error
                }
            }
        }
        return errors
    }

    private void registerConstraints() {

        validationService.register(TableMultiDimensionalParameter) {List type ->
            Set<Double> set = new HashSet(type)
            if (set.size() != type.size()) {
                return [ValidationType.ERROR, "underwriting.info.value.of.max.sum.insured.not.unique"]
            }
            return true
        }

        validationService.register(TableMultiDimensionalParameter) {List type ->
            if (type.any {it < 0}) {
                return [ValidationType.ERROR, "underwriting.info.value.of.max.sum.insured.negative"]
            }
            return true
        }
    }
}