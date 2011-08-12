package org.pillarone.riskanalytics.domain.pc.filter

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SegmentFilterValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(SegmentFilterValidator)
    private AbstractParameterValidationService validationService

    public SegmentFilterValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof MultiDimensionalParameterHolder) {
                AbstractMultiDimensionalParameter value = parameter.getBusinessObject()
                if (value instanceof ConstrainedMultiDimensionalParameter) {
                    IMultiDimensionalConstraints constraints = value.constraints
                    if (constraints instanceof SegmentPortion) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug "validating ${parameter.path}"
                        }
                        def currentErrors = validationService.validate(constraints, value)
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)

                    }
                }
            }
        }
        return errors
    }

    private void registerConstraints() {
        validationService.register(SegmentPortion) {ConstrainedMultiDimensionalParameter type ->
            if (type.valueRowCount == 0) {
                return [ValidationType.ERROR, 'portions.contains.no.segment']
            }
            return
        }
    }
}
