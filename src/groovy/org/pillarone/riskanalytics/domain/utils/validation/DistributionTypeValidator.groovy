package org.pillarone.riskanalytics.domain.utils.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter

class DistributionTypeValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(DistributionTypeValidator)
    private static final double EPSILON = 1E-6 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public DistributionTypeValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof DistributionType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }

        return errors
    }

    private void registerConstraints() {
        validationService.register(DistributionType.POISSON) {Map type ->
            if (type.lambda >= 0) return true
            ["distribution.type.error.poisson.lambda.negative", type.lambda]
        }
        validationService.register(DistributionType.EXPONENTIAL) {Map type ->
            type.lambda <= 0 ? ["distribution.type.error.exponential.lambda.nonpositive", type.lambda] :
                true
        }
        validationService.register(DistributionType.BETA) {Map type ->
            type.alpha <= 0 ? ["distribution.type.error.beta.alpha.nonpositive", type.alpha] :
                type.beta <= 0 ? ["distribution.type.error.beta.beta.nonpositive", type.beta] :
                    true
        }

        validationService.register(DistributionType.NEGATIVEBINOMIAL) {Map type ->
            if (type.gamma > 0) return true
            ["distribution.tpye.error.negativebinomial.gamma.nonpositive", type.gamma]
        }
        validationService.register(DistributionType.NEGATIVEBINOMIAL) {Map type ->
            if ((0.0..1.0).containsWithinBounds(type.p)) return true
            ["distribution.tpye.error.negativebinomial.p.out.of.range", type.p]
        }
        validationService.register(DistributionType.DISCRETEEMPIRICAL) {Map type ->
            double[] values = new double[type.discreteEmpiricalValues.getRowCount()-1];
            int index = type.discreteEmpiricalValues.getColumnIndex('observations')
            for (int i = 1; i < type.discreteEmpiricalValues.getRowCount(); i++) {
                values[i - 1] = InputFormatConverter.getDouble(type.discreteEmpiricalValues.getValueAt(i, index))
            }
            if (!values) {
                return ["distribution.type.error.discreteempirical.observations.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.discreteempirical.observations.nonincreasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(DistributionType.DISCRETEEMPIRICAL) {Map type ->
            double[] values = new double[type.discreteEmpiricalValues.getRowCount()-1];
            int index = type.discreteEmpiricalValues.getColumnIndex('probabilities')
            for (int i = 1; i < type.discreteEmpiricalValues.getRowCount(); i++) {
                values[i - 1] = InputFormatConverter.getDouble(type.discreteEmpiricalValues.getValueAt(i, index))
            }
            if (!values) {
                return ["distribution.type.error.discreteempirical.probabilities.empty"]
            }
           /* double sum = values.inject(0) {temp, it -> temp + it }
            if (isCloseEnough(sum, 1d)) return true
            ["distribution.type.error.discreteempirical.probabilities.sum.not.one", sum, values]  */
        }
        validationService.register(DistributionType.DISCRETEEMPIRICALCUMULATIVE) {Map type ->
            double[] values = new double[type.discreteEmpiricalCumulativeValues.getRowCount()-1];
            int index = type.discreteEmpiricalCumulativeValues.getColumnIndex('observations')
            for (int i = 1; i < type.discreteEmpiricalCumulativeValues.getRowCount(); i++) {
                values[i - 1] = InputFormatConverter.getDouble(type.discreteEmpiricalCumulativeValues.getValueAt(i, index))
            }
            if (!values) {
                return ["distribution.type.error.discreteempirical.cumulative.observations.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.discreteempirical.cumulative.observations.nonincreasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(DistributionType.DISCRETEEMPIRICALCUMULATIVE) {Map type ->
            double[] values = new double[type.discreteEmpiricalCumulativeValues.getRowCount()-1];
            int index = type.discreteEmpiricalCumulativeValues.getColumnIndex('cumulative probabilities')
            for (int i = 1; i < type.discreteEmpiricalCumulativeValues.getRowCount(); i++) {
                values[i - 1] = InputFormatConverter.getDouble(type.discreteEmpiricalCumulativeValues.getValueAt(i, index))
            }
            if (!values) {
                return ["distribution.type.error.discreteempirical.cumulative.probabilities.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.discreteempirical.cumulative.probabilities.nonincreasing", i, values[i - 1], values[i]]
                }
            }
          /*  if (!isCloseEnough(values[-1], 1d)) {
                return ["distribution.type.error.discreteempirical.cumulative.probability.last.value.not.1", values[values.length - 1]]
            }*/
            return true
        }
        validationService.register(DistributionType.NORMAL) {Map type ->
            if (type.stDev > 0) return true
            ["distribution.type.error.normal.sigma.nonpositive", type.stDev]
        }
        validationService.register(DistributionType.LOGNORMAL) {Map type ->
            if (type.stDev > 0) return true
            ["distribution.type.error.lognormal.sigma.nonpositive", type.stDev]
        }
        // todo(sku): check for further restrictions
        validationService.register(DistributionType.LOGNORMAL_MU_SIGMA) {Map type ->
            if (type.sigma > 0) return true
            ["distribution.type.error.lognormal_mu_sigma.sigma.nonpositive", type.sigma]
        }
        validationService.register(DistributionType.PARETO) {Map type ->
            if (type.alpha > 0) return true
            ["distribution.type.error.pareto.alpha.nonpositive", type.alpha]
        }
        validationService.register(DistributionType.PARETO) {Map type ->
            if (type.beta > 0) return true
            ["distribution.type.error.pareto.beta.nonpositive", type.beta]
        }
        validationService.register(DistributionType.UNIFORM) {Map type ->
            if (type.a < type.b) return true
            ["distribution.type.error.uniform.limits.nonincreasing", type.a, type.b]
        }
        validationService.register(DistributionType.PIECEWISELINEAR) {Map type ->
            double[] values = new double[type.supportPoints.getRowCount()-1];
            int index = type.supportPoints.getColumnIndex('values')
            for (int i = 1; i < type.supportPoints.getRowCount(); i++) {
                values[i - 1] = InputFormatConverter.getDouble(type.supportPoints.getValueAt(i, index))
            }
            if (!values) {
                return ["distribution.type.error.piecewiselinear.values.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.piecewiselinear.values.nonincreasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(DistributionType.PIECEWISELINEAR) {Map type ->
            double[] cdf = new double[type.supportPoints.getRowCount()-1];
            int index = type.supportPoints.getColumnIndex('cumulative probabilities')
            for (int i = 1; i < type.supportPoints.getRowCount(); i++) {
                cdf[i - 1] = InputFormatConverter.getDouble(type.supportPoints.getValueAt(i, index))
            }
            if (!cdf) {
                return ["distribution.type.error.piecewiselinear.cdf.probabilities.empty"]
            }
            for (int i = 1; i < cdf.length; i++) {
                if (cdf[i - 1] > cdf[i]) {
                    return ["distribution.type.error.piecewiselinear.cdf.probabilities.nonincreasing", i, cdf[i - 1], cdf[i]]
                }
            }
            if (!isCloseEnough(cdf[0], 0d)) {
                return ["distribution.type.error.piecewiselinear.cdf.first.value.not.0", cdf[0]]
            }
            if (!isCloseEnough(cdf[-1], 1d)) {
                return ["distribution.type.error.piecewiselinear.cdf.last.value.not.1", cdf[cdf.length - 1]]
            }
            return true
        }
        validationService.register(DistributionType.PIECEWISELINEAREMPIRICAL) {Map type ->
            double[] values = new double[type.observations.getRowCount()-1];
            int index = type.observations.getColumnIndex('observations')
            for (int i = 1; i < type.observations.getRowCount(); i++) {
                values[i - 1] = InputFormatConverter.getDouble(type.observations.getValueAt(i, index))
            }
            if (!values) {
                return ["distribution.type.error.piecewiselinear.empirical.observations.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.piecewiselinear.empirical.observations.nonincreasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(DistributionType.TRIANGULARDIST) {Map type ->
            if (type.a <= type.m && type.m <= type.b) return true
            ["distribution.type.error.triangular.abscissa.nonincreasing", type.a, type.b, type.m]
        }
        validationService.register(DistributionType.CHISQUAREDIST) {Map type ->
            if (type.n > 0) return true
            ["distribution.type.error.chisquare.n.nonpositive", type.n]
        }
        validationService.register(DistributionType.STUDENTDIST) {Map type ->
            if (type.n > 0) return true
            ["distribution.type.error.student.n.nonpositive", type.n]
        }
        validationService.register(DistributionType.BINOMIALDIST) {Map type ->
            if ((0.0..1.0).containsWithinBounds(type.p)) return true
            ["distribution.tpye.error.binomial.p.out.of.range", type.p]
        }
        validationService.register(DistributionType.BINOMIALDIST) {Map type ->
            if (type.n > 0) return true
            ["distribution.tpye.error.binomial.n.nonpositive", type.n]
        }
        validationService.register(DistributionType.INVERSEGAUSSIANDIST) {Map type ->
            if (type.mu > 0) return true
            ["distribution.type.error.inversegaussian.mu.nonpositive", type.mu]
        }
        validationService.register(DistributionType.INVERSEGAUSSIANDIST) {Map type ->
            if (type.lambda > 0) return true
            ["distribution.type.error.inversegaussian.lambda.nonpositive", type.lambda]
        }
        validationService.register(DistributionType.CONSTANTS) {Map type ->
            double[] values = new double[type.constants.getRowCount()-1];
            int index = type.constants.getColumnIndex('constants')
            for (int i = 1; i < type.constants.getRowCount(); i++) {
                values[i - 1] = InputFormatConverter.getDouble(type.constants.getValueAt(i, index))
            }
            if (values && values.size() > 0) return true
            ["distribution.type.error.constants.empty", values]
        }
        validationService.register(DistributionType.GAMMA) {Map type ->
            return (type.alpha <= 0) ? ["distribution.type.error.gamma.alpha.nonpositive", type.alpha] : true
        }
        validationService.register(DistributionType.GAMMA) {Map type ->
            return (type.lambda <= 0) ? ["distribution.type.error.gamma.lambda.nonpositive", type.lambda] : true
        }
        validationService.register(DistributionType.LOGLOGISTIC) {Map type ->
            return (type.alpha <= 0) ? ["distribution.type.error.loglogistic.alpha.nonpositive", type.alpha] : true
        }
        validationService.register(DistributionType.LOGLOGISTIC) {Map type ->
            return (type.beta <= 0) ? ["distribution.type.error.loglogistic.beta.nonpositive", type.beta] : true
        }
        validationService.register(DistributionType.GUMBEL) {Map type ->
            return (type.beta == 0) ? ["distribution.type.error.gumbel.beta.nonpositive", type.beta] : true
        }
    }

    private boolean isCloseEnough(double candidate, double compareAgainst) {
        (candidate - compareAgainst).abs() < EPSILON
    }
}