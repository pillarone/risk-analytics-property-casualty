package org.pillarone.riskanalytics.domain.utils.validation;

import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ParameterValidationErrorImpl extends ParameterValidationError {

    public ParameterValidationErrorImpl(String message, Collection arguments) {
        super(message, arguments);
    }

    // todo(sku): remove again once a new core plugin is available
    @Override
    public String getLocalizedMessage(Locale locale) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
