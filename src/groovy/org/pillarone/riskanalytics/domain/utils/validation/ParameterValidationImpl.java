package org.pillarone.riskanalytics.domain.utils.validation;

import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation;
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType;
import java.util.Collection;

public class ParameterValidationImpl extends ParameterValidation {

    public ParameterValidationImpl(ValidationType validationType, String message, Collection arguments) {
        super(validationType, message, arguments);
    }
}
