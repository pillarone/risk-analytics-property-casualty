package org.pillarone.riskanalytics.domain.pc.generators.claims.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.generators.claims.IClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import umontreal.iro.lecuyer.probdist.Distribution
import org.codehaus.groovy.runtime.InvokerInvocationException

/**
 * This validator focuses on valid combinations of distributions and modifications.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsGeneratorStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(ClaimsGeneratorStrategyValidator)
    private static final double EPSILON = 1E-6 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public ClaimsGeneratorStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof ClaimsGeneratorType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    try {
                        def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)
                    }
                    catch (IllegalArgumentException ex) {
                        // distribution parameters are invalid, however this is checked in the DistributionTypeValidator
                    }
                }
                // step down recursively
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }
        return errors
    }

    private void registerConstraints() {
        validationService.register(ClaimsGeneratorType.NONE) {Map type ->
            return checkArea(type)
        }
        validationService.register(ClaimsGeneratorType.ATTRITIONAL) {Map type ->
            return checkArea(type)
        }
        validationService.register(ClaimsGeneratorType.ATTRITIONAL_WITH_DATE) {Map type ->
            return checkArea(type)
        }
        validationService.register(ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL) {Map type ->
            return checkArea(type)
        }
        validationService.register(ClaimsGeneratorType.FREQUENCY_SEVERITY) {Map type ->
            return checkArea(type)
        }
        validationService.register(ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY) {Map type ->
            return checkArea(type)
        }
        validationService.register(ClaimsGeneratorType.EXTERNAL_SEVERITY) {Map type ->
            return checkArea(type)
        }
    }

    private def checkArea(Map type) {
        if (type.size() == 0) return    // ClaimsGeneratorType.NONE select, no checks necessary
        Distribution distribution = type.claimsSizeDistribution.distribution
        DistributionModified modification = type.claimsSizeModification

        if (!modification || modification.type == DistributionModifier.NONE ||
            modification.type == DistributionModifier.SHIFT) {
            return
        }

        double leftBoundary = (Double) modification.getParameters().get("min");
        double rightBoundary = (Double) modification.getParameters().get("max");
        // todo: if cdf is expensive, split truncated cases to use getArea (and censored..?)
        if (Math.abs(distribution.cdf(rightBoundary) - distribution.cdf(leftBoundary)) < 1E-8) {
            return ["claims.model.error.restricted.density.function.not.normalizable.for.claims.generator"]
        }
        return
    }
}
