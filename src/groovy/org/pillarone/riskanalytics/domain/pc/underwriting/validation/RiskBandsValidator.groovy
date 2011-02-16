package org.pillarone.riskanalytics.domain.pc.underwriting.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBands
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator

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

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof MultiDimensionalParameterHolder) {
                AbstractMultiDimensionalParameter value = parameter.getBusinessObject()  // parameter.value ??
                if (value instanceof TableMultiDimensionalParameter) {
                    List<String> titles = value.getColumnNames()  // value.titles ?
                    if (titles.contains(RiskBands.MAXIMUM_SUM_INSURED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug "validating ${parameter.path}"
                        }

                        def currentErrors = validationService.validate(value, value.getColumnByName(RiskBands.MAXIMUM_SUM_INSURED))
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)
                    }
                }
            }
        }
        return errors
    }

    private void registerConstraints() {

        validationService.register(TableMultiDimensionalParameter) {List type ->
            Set<Double> set = new HashSet(type)
            if (set.size() != type.size()) {
                return ["underwriting.info.value.of.max.sum.insured.not.unique"]
            }
            return true
        }

        validationService.register(TableMultiDimensionalParameter) {List type ->
            if (type.any {it <0}) {
                return ["underwriting.info.value.of.max.sum.insured.negative"]
            }
            return true
        }
    }
}