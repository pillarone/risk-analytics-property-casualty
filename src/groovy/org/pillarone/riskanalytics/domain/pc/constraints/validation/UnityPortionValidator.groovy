package org.pillarone.riskanalytics.domain.pc.constraints.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

import org.pillarone.riskanalytics.domain.utils.constraints.IUnityPortion
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class UnityPortionValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(UnityPortionValidator)
    private AbstractParameterValidationService validationService

    public UnityPortionValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof MultiDimensionalParameterHolder) {
                AbstractMultiDimensionalParameter value = parameter.getBusinessObject()
                if (value instanceof ConstrainedMultiDimensionalParameter) {
                    IMultiDimensionalConstraints constraints = value.constraints
                    if (constraints instanceof IUnityPortion) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug "validating ${parameter.path}"
                        }
                        def currentErrors = validationService.validate(constraints, value)
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)

                    }
                }
            }
            // probably not needed here
            else if (parameter instanceof ParameterObjectParameterHolder) {
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }  // end for
        return errors
    }

    private void registerConstraints() {

        validationService.register(IUnityPortion) {ConstrainedMultiDimensionalParameter type ->
            int portionColumnIndex = type.constraints.getPortionColumnIndex()
            if (type.valueRowCount == 0) return true
            double[] portions = type.getColumn(portionColumnIndex)

            int componentNameColumnIndex = type.constraints.getComponentNameColumnIndex()
            // list of portions empty and list of component references non-empty
            if (!type.getColumn(componentNameColumnIndex).isEmpty() && portions.length == 0) {
                return ["portion.unity.error.portions.empty"]
            }

            for (int i = 0; i < portions.length; i++) {
                if (portions[i] < 0 || portions[i] > 1) {
                    return ["portion.unity.error.portions.not.in.unity.interval", i, portions[i]]
                }
            }
            return true
        }


    }

}






