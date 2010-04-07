package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ParameterValidationError
import org.pillarone.riskanalytics.core.parameterization.ParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

class DistributionType extends AbstractParameterObjectClassifier implements Serializable {

    static final double EPSILON = 1E-6 // guard for "close-enough" checks instead of == for doubles

    public static ParameterValidationService validationService = new ParameterValidationService()

    public static final DistributionType POISSON = new DistributionType(
            "poisson", "POISSON", ["lambda": 0d])
    public static final DistributionType NEGATIVEBINOMIAL = new DistributionType(
            "negative binomial", "NEGATIVEBINOMIAL", ["gamma": 1d, "p": 1d])
    public static final DistributionType DISCRETEEMPIRICAL = new DistributionType(
            "discrete empirical", "DISCRETEEMPIRICAL", ["discreteEmpiricalValues": new TableMultiDimensionalParameter([[0.0], [1.0]], ['observations', 'probabilities'])])
    public static final DistributionType DISCRETEEMPIRICALCUMULATIVE = new DistributionType(
            "discrete empirical cumulative", "DISCRETEEMPIRICALCUMULATIVE", ["discreteEmpiricalCumulativeValues": new TableMultiDimensionalParameter([[0.0], [1.0]], ['observations', 'cumulative probabilities'])])
    public static final DistributionType NORMAL = new DistributionType(
            "normal", "NORMAL", ["mean": 0d, "stDev": 1d])
    public static final DistributionType LOGNORMAL = new DistributionType(
            "log normal (mean, stdev)", "LOGNORMAL", ["mean": 1d, "stDev": 1d])
    public static final DistributionType LOGNORMAL_MU_SIGMA = new DistributionType(
            "log normal (mu, sigma)", "LOGNORMAL_MU_SIGMA", ["mu": 1d, "sigma": 1d])
    public static final DistributionType PARETO = new DistributionType(
            "pareto", "PARETO", ["alpha": 1d, "beta": 1d])
    public static final DistributionType UNIFORM = new DistributionType(
            "uniform", "UNIFORM", ["a": 0d, "b": 1d])
    public static final DistributionType CONSTANT = new DistributionType(
            "constant", "CONSTANT", ["constant": 0d])
    public static final DistributionType PIECEWISELINEAREMPIRICAL = new DistributionType(
            "piecewise linear empirical", "PIECEWISELINEAREMPIRICAL", ["observations": new TableMultiDimensionalParameter([0d, 1d], ['observations'])])
    public static final DistributionType PIECEWISELINEAR = new DistributionType(
            "piecewise linear", "PIECEWISELINEAR", ["supportPoints": new TableMultiDimensionalParameter([[0d, 1d], [0d, 1d]], ['values', 'cummulative probabilities'])])
    public static final DistributionType TRIANGULARDIST = new DistributionType(
            "triangular dist", "TRIANGULARDIST", ["a": 0d, "b": 1d, "m": 0.01])
    public static final DistributionType CHISQUAREDIST = new DistributionType(
            "chi square dist", "CHISQUAREDIST", ["n": 1])
    public static final DistributionType STUDENTDIST = new DistributionType(
            "student dist", "STUDENTDIST", ["n": 1])
    public static final DistributionType BINOMIALDIST = new DistributionType(
            "binomial dist", "BINOMIALDIST", ["n": 1, "p": 0d])
    public static final DistributionType INVERSEGAUSSIANDIST = new DistributionType(
            "inverse gaussian dist", "INVERSEGAUSSIANDIST", ["mu": 1d, "lambda": 1d])
    public static final DistributionType CONSTANTS = new DistributionType(
            "constant values", "CONSTANTS", ["constants": new TableMultiDimensionalParameter([0d, 1d], ['constants'])])

    public static final all = [
            CHISQUAREDIST,
            CONSTANT,
//            CONSTANTS,        // todo(bgi): reactivate once everything is running (see comments in PMO-787)
            BINOMIALDIST,
            DISCRETEEMPIRICAL,
            DISCRETEEMPIRICALCUMULATIVE,
            INVERSEGAUSSIANDIST,
            LOGNORMAL,
            LOGNORMAL_MU_SIGMA,
            NEGATIVEBINOMIAL,
            NORMAL,
            PARETO,
            POISSON,
            PIECEWISELINEAR,
            PIECEWISELINEAREMPIRICAL,
            STUDENTDIST,
            TRIANGULARDIST,
            UNIFORM
    ]

    protected static Map types = [:]

    static {
        DistributionType.all.each {
            DistributionType.types[it.toString()] = it
        }
        validationService.register(POISSON) {Map type ->
            if (type.lambda >= 0) return true
            ["distribution.type.error.poisson.negative.lambda", type.lambda]
        }

        validationService.register(NEGATIVEBINOMIAL) {Map type ->
            if (type.gamma > 0) return true
            ["distribution.tpye.error.negativebinomial.gamma.negative.or.zero", type.gamma]
        }
        validationService.register(NEGATIVEBINOMIAL) {Map type ->
            if ((0..1).containsWithinBounds(type.p)) return true
            ["distribution.tpye.error.negativebinomial.p.out.of.range", type.p]
        }
        validationService.register(DISCRETEEMPIRICAL) {Map type ->
            double[] values = type.discreteEmpiricalValues.getColumnByName('observations')
            if (!values) {
                return ["distribution.type.error.discreteempirical.observations.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.discreteempirical.observations.not.increasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(DISCRETEEMPIRICAL) {Map type ->
            double[] values = type.discreteEmpiricalValues.getColumnByName('probabilities')
            if (!values) {
                return ["distribution.type.error.discreteempirical.probabilities.empty"]
            }
            double sum = values.inject(0) {temp, it -> temp + it }
            if (isCloseEnough(sum, 1d)) return true
            ["distribution.type.error.discreteempirical.probabilities.sum.not.one", sum, values]
        }
        validationService.register(DISCRETEEMPIRICALCUMULATIVE) {Map type ->
            double[] values = type.discreteEmpiricalCumulativeValues.getColumnByName('observations')
            if (!values) {
                return ["distribution.type.error.discreteempiricalcumulative.observations.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.discreteempiricalcumulative.observations.not.increasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(DISCRETEEMPIRICALCUMULATIVE) {Map type ->
            double[] values = type.discreteEmpiricalCumulativeValues.getColumnByName('cumulative probabilities')
            if (!values) {
                return ["distribution.type.error.discreteempiricalcumulative.probabilities.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.discreteempiricalcumulative.probabilities.not.increasing", i, values[i - 1], values[i]]
                }
            }
            if (!isCloseEnough(values[-1], 1d)) {
                return ["distribution.type.error.discreteempiricalcumulative.probability.last.value.not.1", values[values.length - 1]]
            }
            return true
        }
        validationService.register(NORMAL) {Map type ->
            if (type.stDev > 0) return true
            ["distribution.type.error.normal.sigma.negative.or.zero", type.stDev]
        }
        validationService.register(LOGNORMAL) {Map type ->
            if (type.stDev > 0) return true
            ["distribution.type.error.lognormal.sigma.negative.or.zero", type.stDev]
        }
        // todo(sku): check for further restrictions
        validationService.register(LOGNORMAL_MU_SIGMA) {Map type ->
            if (type.sigma > 0) return true
            ["distribution.type.error.lognormal_mu_sigma.sigma.negative.or.zero", type.sigma]
        }
        validationService.register(PARETO) {Map type ->
            if (type.alpha > 0) return true
            ["distribution.type.error.pareto.alpha.negative.or.zero", type.alpha]
        }
        validationService.register(PARETO) {Map type ->
            if (type.beta > 0) return true
            ["distribution.type.error.pareto.beta.negative.or.zero", type.beta]
        }
        validationService.register(UNIFORM) {Map type ->
            if (type.a < type.b) return true
            ["distribution.type.error.uniform", type.a, type.b]
        }
        validationService.register(PIECEWISELINEAR) {Map type ->
            double[] values = type.supportPoints.getColumnByName('values')
            if (!values) {
                return ["distribution.type.error.piecewiselinear.values.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.piecewiselinear.values.not.increasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(PIECEWISELINEAR) {Map type ->
            double[] cdf = type.supportPoints.getColumnByName('cummulative probabilities')
            if (!cdf) {
                return ["distribution.type.error.piecewiselinear.cummulative.probabilities.empty"]
            }
            for (int i = 1; i < cdf.length; i++) {
                if (cdf[i - 1] > cdf[i]) {
                    return ["distribution.type.error.piecewiselinear.cummulative.probabilities.not.increasing", i, cdf[i - 1], cdf[i]]
                }
            }
            if (!isCloseEnough(cdf[0], 0d)) {
                return ["distribution.type.error.piecewiselinear.cdf.first.value.not.null", cdf[0]]
            }
            if (!isCloseEnough(cdf[-1], 1d)) {
                return ["distribution.type.error.piecewiselinear.cdf.last.value.not.1", cdf[cdf.length - 1]]
            }
            return true
        }
        validationService.register(PIECEWISELINEAREMPIRICAL) {Map type ->
            double[] values = type.observations.getColumnByName('observations')
            if (!values) {
                return ["distribution.type.error.piecewiselinearempirical.observations.empty"]
            }
            for (int i = 1; i < values.length; i++) {
                if (values[i - 1] > values[i]) {
                    return ["distribution.type.error.piecewiselinear.observations.not.increasing", i, values[i - 1], values[i]]
                }
            }
            return true
        }
        validationService.register(TRIANGULARDIST) {Map type ->
            if (type.a <= type.m && type.m <= type.b) return true
            ["distribution.type.error.triangular", type.a, type.b, type.m]
        }
        validationService.register(CHISQUAREDIST) {Map type ->
            if (type.n > 0) return true
            ["distribution.type.error.chisquare.n.negative.or.zero", type.n]
        }
        validationService.register(STUDENTDIST) {Map type ->
            if (type.n > 0) return true
            ["distribution.type.error.student.n.negative.or.zero", type.n]
        }
        validationService.register(BINOMIALDIST) {Map type ->
            if ((0..1).containsWithinBounds(type.p)) return true
            ["distribution.tpye.error.binomial.p.out.of.range", type.p]
        }
        validationService.register(BINOMIALDIST) {Map type ->
            if (type.n > 0) return true
            ["distribution.tpye.error.binomial.n.negative.or.zero", type.n]
        }
        validationService.register(INVERSEGAUSSIANDIST) {Map type ->
            if (type.mu > 0) return true
            ["distribution.type.error.inversegaussian.mu.negative.or.zero", type.mu]
        }
        validationService.register(INVERSEGAUSSIANDIST) {Map type ->
            if (type.lambda > 0) return true
            ["distribution.type.error.inversegaussian.lambda.negative.or.zero", type.lambda]
        }
        validationService.register(CONSTANTS) {Map type ->
            if (type.constants.size() > 0) return true
            ["distribution.type.error.constants.empty", type.constants]
        }
    }

    static boolean isCloseEnough(double candidate, double compareAgainst) {
        (candidate - compareAgainst).abs() < EPSILON
    }

    protected DistributionType(String typeName, Map parameters) {
        this(typeName, typeName, parameters)
    }

    protected DistributionType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static DistributionType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return RandomDistributionFactory.getDistribution(this, parameters)
    }

    public String getConstructionString(Map parameters) {
        TreeMap sortedParameters = new TreeMap()
        sortedParameters.putAll(parameters)
        "org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory.getDistribution(${this.class.name}.${typeName.toUpperCase()}, $sortedParameters)"
    }

    List<ParameterValidationError> validate(Map parameters) {
        validationService.validate(this, parameters)
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        return types[displayName]
    }
}
