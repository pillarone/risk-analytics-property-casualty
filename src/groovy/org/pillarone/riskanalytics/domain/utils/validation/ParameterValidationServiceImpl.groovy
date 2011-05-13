package org.pillarone.riskanalytics.domain.utils.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType


class ParameterValidationServiceImpl extends AbstractParameterValidationService {

    ParameterValidation createErrorObject(ValidationType validationType, String msg, List args) {
        return new ParameterValidationImpl(validationType, msg, args)
    }


}
