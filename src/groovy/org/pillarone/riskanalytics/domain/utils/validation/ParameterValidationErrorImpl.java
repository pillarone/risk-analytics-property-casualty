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

    @Override
    public String getLocalizedMessage(Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("org.pillarone.riskanalytics.domain.pc.validation.distributionType", locale);
            return MessageFormat.format(bundle.getString(msg),args);
        } catch (MissingResourceException e) {
            return msg;
        }
    }
}
