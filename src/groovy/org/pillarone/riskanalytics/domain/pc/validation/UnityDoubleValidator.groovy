package org.pillarone.riskanalytics.domain.pc.validation


import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.DoubleParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class UnityDoubleValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(UnityDoubleValidator)
    private AbstractParameterValidationService validationService

    public UnityDoubleValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof DoubleParameterHolder) {
                String path = parameter.path
                if (path.contains('parmPeriodPaymentPortion') || path.contains('coveredByReinsurer') ||
                        path.contains('quotaShare')) {
                    Double value = parameter.getBusinessObject()
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    def currentErrors = validationService.validate(path, value)
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)

                }
            }

            else if (parameter instanceof ParameterObjectParameterHolder) {
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }  // end for
        return errors
    }

    private void registerConstraints() {

        validationService.register(String) {Double type ->

            if (type < 0 || type > 1) {
                return ["double.unity.error.double.not.in.unity.interval"]
            }
            return true
        }


    }

}
