package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.simulation.InvalidParameterException;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import umontreal.iro.lecuyer.probdist.*
import static org.pillarone.riskanalytics.core.util.GroovyUtils.asDouble
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter


class DistributionType extends AbstractParameterObjectClassifier implements Serializable {

    public static final DistributionType POISSON = new DistributionType(
            "poisson", "POISSON", ["lambda": 0d])
    public static final DistributionType EXPONENTIAL = new DistributionType(
            "exponential", "EXPONENTIAL", ["lambda": 1d])
    public static final DistributionType NEGATIVEBINOMIAL = new DistributionType(
            "negative binomial", "NEGATIVEBINOMIAL", ["gamma": 1d, "p": 1d])
    public static final DistributionType DISCRETEEMPIRICAL = new DistributionType(
            "discrete empirical", "DISCRETEEMPIRICAL", ["discreteEmpiricalValues": new ConstrainedMultiDimensionalParameter([[0.0], [1.0]],
                    ['observations', 'probabilities'], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
    public static final DistributionType DISCRETEEMPIRICALCUMULATIVE = new DistributionType(
            "discrete empirical cumulative", "DISCRETEEMPIRICALCUMULATIVE", ["discreteEmpiricalCumulativeValues":
                    new ConstrainedMultiDimensionalParameter([[0.0], [1.0]], ['observations', 'cumulative probabilities'],
                            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
    public static final DistributionType NORMAL = new DistributionType(
            "normal", "NORMAL", ["mean": 0d, "stDev": 1d])
    public static final DistributionType LOGNORMAL = new DistributionType(
            "log normal (mean, stdev)", "LOGNORMAL", ["mean": 1d, "stDev": 1d])
    public static final DistributionType LOGNORMAL_MU_SIGMA = new DistributionType(
            "log normal (mu, sigma)", "LOGNORMAL_MU_SIGMA", ["mu": 1d, "sigma": 1d])
    public static final DistributionType LOGNORMAL_MEAN_CV = new DistributionType(
            "log normal (mean, cv)", "LOGNORMAL_MEAN_CV", ["mean": 1d, "CV": 1d])
    public static final DistributionType BETA = new DistributionType(
            "beta", "BETA", ["alpha": 1d, "beta": 1d])
    public static final DistributionType PARETO = new DistributionType(
            "pareto", "PARETO", ["alpha": 1d, "beta": 1d])
    public static final DistributionType UNIFORM = new DistributionType(
            "uniform", "UNIFORM", ["a": 0d, "b": 1d])
    public static final DistributionType CONSTANT = new DistributionType(
            "constant", "CONSTANT", ["constant": 0d])
    public static final DistributionType PIECEWISELINEAREMPIRICAL = new DistributionType(
            "piecewise linear empirical", "PIECEWISELINEAREMPIRICAL", ["observations": new ConstrainedMultiDimensionalParameter([0d, 1d],
                    ['observations'], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
    public static final DistributionType PIECEWISELINEAR = new DistributionType(
            "piecewise linear", "PIECEWISELINEAR", ["supportPoints": new ConstrainedMultiDimensionalParameter([[0d, 1d], [0d, 1d]],
                    ['values', 'cumulative probabilities'], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
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
            "constant values", "CONSTANTS", ["constants": new ConstrainedMultiDimensionalParameter([0d, 1d],
                    ['constants'], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
    public static final DistributionType GAMMA = new DistributionType(
            "gamma", "GAMMA", ["alpha": 2d, "lambda": 2d])
    public static final DistributionType GUMBEL = new DistributionType(
            "gumbel", "GUMBEL", ["beta": 1d, "delta": 0d])
    public static final DistributionType LOGLOGISTIC = new DistributionType(
            "log logistic", "LOGLOGISTIC", ["alpha": 2d, "beta": 1d])
    public static final DistributionType GPD = new DistributionType(
            "generalized pareto", "GPD", ["xi": 1 / 2d, "beta": 1d, "tau": 1d])
    public static final DistributionType SHIFTEDPARETOII = new DistributionType(
            "shifted pareto II", "SHIFTEDPARETOII", ["alpha": 2d, "beta": 1d, "lambda": 0d])
    public static final DistributionType PARETOII = new DistributionType(
            "pareto II", "PARETOII", ["alpha": 2d, "lambda": 0d])
    public static final DistributionType LOGNORMALPARETO = new DistributionType(
            "lognormal pareto", "LOGNORMALPARETO", ["sigma": 1d, "alpha": 2d, "beta": 1d, "mu": -2d])
    public static final DistributionType LOGNORMALTYPEIIPARETO = new DistributionType(
            "lognormal type II pareto", "LOGNORMALTYPEIIPARETO", ["sigma": 1d, "alpha": 2d, "beta": 1d, "lambda": 0d, "mu": -2d])
    public static final DistributionType LOGNORMALPARETO_SMOOTH = new DistributionType(
            "lognormal pareto smooth", "LOGNORMALPARETO_SMOOTH", ["sigma": 1d, "alpha": 2d, "beta": 1d])
    public static final DistributionType LOGNORMALTYPEIIPARETO_SMOOTH = new DistributionType(
            "lognormal type II pareto smooth", "LOGNORMALTYPEIIPARETO_SMOOTH", ["sigma": 1d, "alpha": 2d, "beta": 1d, "lambda": 0d])

    public static final all = [
            BETA,
            CHISQUAREDIST,
            CONSTANT,
            CONSTANTS,
            BINOMIALDIST,
            DISCRETEEMPIRICAL,
            DISCRETEEMPIRICALCUMULATIVE,
            EXPONENTIAL,
            GAMMA,
            GPD,
            GUMBEL,
            INVERSEGAUSSIANDIST,
            LOGLOGISTIC,
            LOGNORMAL,
            LOGNORMAL_MEAN_CV,
            LOGNORMAL_MU_SIGMA,
            LOGNORMALPARETO,
            LOGNORMALTYPEIIPARETO,
            LOGNORMALPARETO_SMOOTH,
            LOGNORMALTYPEIIPARETO_SMOOTH,
            NEGATIVEBINOMIAL,
            NORMAL,
            PARETO,
            POISSON,
            PIECEWISELINEAR,
            PIECEWISELINEAREMPIRICAL,
            SHIFTEDPARETOII,
            STUDENTDIST,
            TRIANGULARDIST,
            PARETOII,
            UNIFORM
    ]

    protected static Map types = [:]

    static {
        DistributionType.all.each {
            DistributionType.types[it.toString()] = it
        }
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
            throw new IllegalArgumentException("['DistributionType.NaNParameter','"
                    + mean + "','" + stDev + "']")
        }
        return new LognormalDist(mu, sigma)
    }

    private static Distribution getLognormalCVDistribution(double mean, double cv) {
        double stdev = mean * cv
        double variance = stdev * stdev
        double meanSquare = mean * mean
        double t = Math.log(1 + (variance / meanSquare))
        double sigma = Math.sqrt(t)
        double mu = Math.log(mean) - 0.5 * t
        if (mu == Double.NaN || mu == 0) {
            throw new IllegalArgumentException("['DistributionType.NaNParameter','" + mu + "']")
        }
        return new LognormalDist(mu, sigma)
    }

    private static Distribution getDiscreteEmpiricalDistribution(double[] obs, double[] prob) {
        double probSum = 0
        for (double value : prob) {probSum += value}
        for (int i = 0; i < prob.size(); i++) {
            prob[i] = prob[i] / probSum
        }
        return new DiscreteDistribution(obs, prob, obs.length)
    }

    private static Distribution getDiscreteEmpiricalCumulativeDistribution(double[] obs, double[] cumprob) {
        double lastcell = 0, ret = 0
        def prob = cumprob.collect {cell -> ret = cell - lastcell; lastcell = cell; ret }
        return getDiscreteEmpiricalDistribution(obs, prob as double[])
    }

    static RandomDistribution getUniformDistribution() {
        return getStrategy(DistributionType.UNIFORM, ['a': 0, 'b': 1])
    }

    /**
     * @return new RandomDistribution with distribution==null if parameters are invalid. Use ValidationService for better error messages.
     */
    static RandomDistribution getStrategy(DistributionType type, Map parameters) {
        RandomDistribution distribution = new RandomDistribution(type: type, parameters: parameters)
        try {
            switch (type) {
                case DistributionType.NORMAL:
                    distribution.distribution = new NormalDist(
                            (double) (parameters.containsKey("mean") ? parameters["mean"] : 0),
                            (double) (parameters.containsKey("stDev") ? parameters["stDev"] : 1))
                    break
                case DistributionType.LOGNORMAL:
                    distribution.distribution = getLognormalDistribution((double) parameters["mean"], (double) parameters["stDev"])
                    break
                case DistributionType.LOGNORMAL_MEAN_CV:
                    distribution.distribution = getLognormalCVDistribution((double) parameters["mean"], (double) parameters["CV"])
                    break
                case DistributionType.LOGNORMAL_MU_SIGMA:
                    distribution.distribution = new LognormalDist((double) parameters["mu"], (double) parameters["sigma"])
                    break
                case DistributionType.POISSON:
                    distribution.distribution = new PoissonDist((double) (parameters.containsKey("lambda") ? parameters["lambda"] : 0))
                    break
                case DistributionType.EXPONENTIAL:
                    distribution.distribution = new ExponentialDist((double) (parameters.containsKey("lambda") ? parameters["lambda"] : 1))
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
                    distribution.distribution = getDiscreteEmpiricalDistribution(asDouble(parameters["discreteEmpiricalValues"].getColumnByName("observations")),
                            asDouble(parameters["discreteEmpiricalValues"].getColumnByName("probabilities")))
                    break
                case DistributionType.DISCRETEEMPIRICALCUMULATIVE:
                    distribution.distribution = getDiscreteEmpiricalCumulativeDistribution(asDouble(parameters["discreteEmpiricalCumulativeValues"].getColumnByName("observations")),
                            asDouble(parameters["discreteEmpiricalCumulativeValues"].getColumnByName("cumulative probabilities")))
                    break
                case DistributionType.PIECEWISELINEAREMPIRICAL:
                    distribution.distribution = new PiecewiseLinearEmpiricalDist((double[]) asDouble(parameters["observations"].getColumnByName("observations")))
                    break
                case DistributionType.PIECEWISELINEAR:
                    distribution.distribution = new PiecewiseLinearDistribution(asDouble(parameters["supportPoints"].getColumnByName("values")),
                            asDouble(parameters["supportPoints"].getColumnByName("cumulative probabilities")))
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
                case DistributionType.GAMMA:
                    distribution.distribution = new GammaDist((double) parameters["alpha"], (double) parameters["lambda"])
                    break
                case DistributionType.GUMBEL:
                    distribution.distribution = new GumbelDist((double) parameters["beta"], (double) parameters["delta"])
                    break
                case DistributionType.LOGLOGISTIC:
                    distribution.distribution = new LoglogisticDist((double) parameters["alpha"], (double) parameters["beta"])
                    break
                case DistributionType.GPD:
                    distribution.distribution = new GeneralizedParetoDistribution((double) parameters["xi"],
                            (double) parameters["beta"], (double) parameters["tau"])
                    break
                case DistributionType.SHIFTEDPARETOII:
                    distribution.distribution = new TypeIIParetoDistribution((double) parameters["alpha"],
                            (double) parameters["beta"], (double) parameters["lambda"])
                    break
                case DistributionType.PARETOII:
                    distribution.distribution = new TypeIIParetoDistribution((double) parameters["alpha"],
                            (double) parameters["lambda"])
                    break
                case DistributionType.LOGNORMALPARETO:
                    distribution.distribution = new LognormalParetoDistribution((double) parameters["sigma"],
                            (double) parameters["alpha"], (double) parameters["beta"], (double) parameters["mu"])
                    break
                case DistributionType.LOGNORMALTYPEIIPARETO:
                    distribution.distribution = new LognormalTypeIIParetoDistribution((double) parameters["sigma"],
                            (double) parameters["alpha"], (double) parameters["beta"], (double) parameters["lambda"], (double) parameters["mu"])
                    break
                case DistributionType.LOGNORMALPARETO_SMOOTH:
                    distribution.distribution = new LognormalParetoDistribution((double) parameters["sigma"],
                            (double) parameters["alpha"], (double) parameters["beta"])
                    break
                case DistributionType.LOGNORMALTYPEIIPARETO_SMOOTH:
                    distribution.distribution = new LognormalTypeIIParetoDistribution((double) parameters["sigma"],
                            (double) parameters["alpha"], (double) parameters["beta"], (double) parameters["lambda"])
                    break
                default:
                    throw new InvalidParameterException("DistributionType $type not implemented")
            }
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidParameterException(ex.message, ex)
        }

        return distribution
    }
}
