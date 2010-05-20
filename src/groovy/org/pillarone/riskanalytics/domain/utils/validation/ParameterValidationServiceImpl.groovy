package org.pillarone.riskanalytics.domain.utils.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError


class ParameterValidationServiceImpl extends AbstractParameterValidationService {

    ParameterValidationError createErrorObject(String msg, List args) {
        return new ParameterValidationErrorImpl(msg, args)
    }


}
