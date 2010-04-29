package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ParameterValidationError
import org.pillarone.riskanalytics.core.parameterization.ParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import umontreal.iro.lecuyer.probdist.*

import static org.pillarone.riskanalytics.core.util.GroovyUtils.asDouble

class DistributionType extends AbstractParameterObjectClassifier implements Serializable {

    static final double EPSILON = 1E-6 // guard for "close-enough" checks instead of == for doubles

    public static ParameterValidationService validationService = new ParameterValidationService()

    public static final DistributionType POISSON = new DistributionType(
            "poisson", "POISSON", ["lambda": 0d])
    public static final DistributionType EXPONENTIAL = new DistributionType(
            "exponential", "EXPONENTIAL", ["lambda": 0d])
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
    public static final DistributionType BETA = new DistributionType(
            "beta", "BETA", ["alpha": 1d, "beta": 1d])
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
            BETA,
            CHISQUAREDIST,
            CONSTANT,
            CONSTANTS,
            BINOMIALDIST,
            DISCRETEEMPIRICAL,
            DISCRETEEMPIRICALCUMULATIVE,
            EXPONENTIAL,
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
        validationService.register(EXPONENTIAL) {Map type ->
            type.lambda < 0 ? ["distribution.type.error.exponential.negative.lambda", type.lambda] :
            true
        }
        validationService.register(BETA) {Map type ->
            type.alpha < 0 ? ["distribution.type.error.exponential.negative.alpha", type.alpha] :
            type.beta < 0 ? ["distribution.type.error.exponential.negative.beta", type.beta] :
            true
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
            double[] values = type.constants.getColumnByName('constants')
            if (values && values.size() > 0) return true
            ["distribution.type.error.constants.empty", values]
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
        return DistributionType.getStrategy(this, parameters)
    }

    public String getConstructionString(Map parameters) {
        TreeMap sortedParameters = new TreeMap()
        sortedParameters.putAll(parameters)
        "org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(${this.class.name}.${typeName.toUpperCase()}, $sortedParameters)"
    }

    List<ParameterValidationError> validate(Map parameters) {
        validationService.validate(this, parameters)
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        return types[displayName]
    }

    private static Distribution getLognormalDistribution(double mean, double stDev) {
        double variance = stDev * stDev
        double meanSquare = mean * mean
        double t = Math.log(1 + (variance / meanSquare))
        double sigma = Math.sqrt(t)
        double mu = Math.log(mean) - 0.5 * t
        if (mu == Double.NaN || sigma == Double.NaN) {
            throw new IllegalArgumentException("A lognormal distribution for mean: $mean and stDev: $stDev does not exist")
        }
        return new LognormalDist(mu, sigma)
    }

    private static Distribution getDiscreteEmpiricalDistribution(double[] obs, double[] prob) {
        double probSum = 0
        assert obs.length == prob.length, "Discrete empirical distributions require the same number of observations and probabilities, but got $obs.length and $prob.length "
        for (double value : prob) {probSum += value}
        assert 1.0 - probSum < 1e-6, "DiscreteEmpiricalGenerator requires the sum of probabilities to be 1.0"
        return new DiscreteDistribution(obs, prob, obs.length)
    }

    private static Distribution getDiscreteEmpiricalCumulativeDistribution(double[] obs, double[] cumprob) {
        assert obs.length == cumprob.length, "DiscreteEmpiricalCumulativeGenerator requires lists of same length but got $obs.length and $cumprob.length"
        assert cumprob[-1] <= 1.0 && cumprob[-1] > 1.0 - 1e-6, "DiscreteEmpiricalCumulativeGenerator requires the last probability to be close to 1.0"
        double lastcell = 0, ret = 0
        def prob = cumprob.collect {cell -> ret = cell - lastcell; lastcell = cell; ret }
        return getDiscreteEmpiricalDistribution(obs, prob as double[])
    }

    static RandomDistribution getUniformDistribution() {
        return getStrategy(DistributionType.UNIFORM, ['a': 0, 'b': 1])
    }

    /**
     * @return new RandomDistribution with distribution==null if parameters are invalid. Use ValidationService for better error messages.
     *
     * */
    static RandomDistribution getStrategy(DistributionType type, Map parameters) {
        RandomDistribution distribution = new RandomDistribution(type: type, parameters: parameters)
        //TODO msp move initialization to RD.getDistribution()
        switch (type) {
            case DistributionType.NORMAL:
                distribution.distribution = new NormalDist(
                        (double) (parameters.containsKey("mean") ? parameters["mean"] : 0),
                        (double) (parameters.containsKey("stDev") ? parameters["stDev"] : 1)
                )
                break
            case DistributionType.LOGNORMAL:
                distribution.distribution = getLognormalDistribution((double) parameters["mean"], (double) parameters["stDev"])
                break
            case DistributionType.LOGNORMAL_MU_SIGMA:
                distribution.distribution = new LognormalDist((double) parameters["mu"], (double) parameters["sigma"])
                break
            case DistributionType.POISSON:
                distribution.distribution = new PoissonDist((double) (parameters.containsKey("lambda") ? parameters["lambda"] : 0))
                break
            case DistributionType.EXPONENTIAL:
                distribution.distribution = new ExponentialDist((double) (parameters.containsKey("lambda") ? parameters["lambda"] : 0))
                break
            case DistributionType.NEGATIVEBINOMIAL:
                distribution.distribution = new NegativeBinomialDist((double) parameters["gamma"], (double) parameters["p"])
                break
            case DistributionType.PARETO:
                distribution.distribution = new ParetoDist((double) parameters["alpha"], (double) parameters["beta"])
                break
            case DistributionType.BETA:
                distribution.distribution = new BetaDist((double) parameters["alpha"], (double) parameters["beta"])
                break
            case DistributionType.UNIFORM:
                distribution.distribution = new UniformDist((double) parameters["a"], (double) parameters["b"])
                break
            case DistributionType.CONSTANT:
                distribution.distribution = new ConstantDistribution((double) parameters["constant"])
                break
            case DistributionType.DISCRETEEMPIRICAL:
                distribution.distribution = getDiscreteEmpiricalDistribution(asDouble(parameters["discreteEmpiricalValues"].getColumnByName("observations")), asDouble(parameters["discreteEmpiricalValues"].getColumnByName("probabilities")))
                break
            case DistributionType.DISCRETEEMPIRICALCUMULATIVE:
                distribution.distribution = getDiscreteEmpiricalCumulativeDistribution(asDouble(parameters["discreteEmpiricalCumulativeValues"].getColumnByName("observations")), asDouble(parameters["discreteEmpiricalCumulativeValues"].getColumnByName("cumulative probabilities")))
                break
            case DistributionType.PIECEWISELINEAREMPIRICAL:
                distribution.distribution = new PiecewiseLinearEmpiricalDist((double[]) asDouble(parameters["observations"].getColumnByName("observations")))
                break
            case DistributionType.PIECEWISELINEAR:
                distribution.distribution = new PiecewiseLinearDistribution(asDouble(parameters["supportPoints"].getColumnByName("values")), asDouble(parameters["supportPoints"].getColumnByName("cummulative probabilities")))
                break
            case DistributionType.TRIANGULARDIST:
                distribution.distribution = new TriangularDist((double) parameters["a"], (double) parameters["b"], (double) parameters["m"])
                break
            case DistributionType.CHISQUAREDIST:
                distribution.distribution = new ChiSquareDist((int) parameters["n"])
                break
            case DistributionType.STUDENTDIST:
                distribution.distribution = new StudentDist((int) parameters["n"])
                break
            case DistributionType.BINOMIALDIST:
                distribution.distribution = new BinomialDist((int) parameters["n"], (double) parameters["p"])
                break
            case DistributionType.INVERSEGAUSSIANDIST:
                distribution.distribution = new InverseGaussianDist((double) parameters["mu"], (double) parameters["lambda"])
                break
            case DistributionType.CONSTANTS:
                distribution.distribution = new ConstantsDistribution(asDouble(parameters["constants"].getColumnByName("constants")))
                break
        }

        return distribution
    }
}
