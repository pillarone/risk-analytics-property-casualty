package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.BinomialDist
import umontreal.iro.lecuyer.probdist.ChiSquareDist
import umontreal.iro.lecuyer.probdist.DiscreteDistribution
import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.probdist.InverseGaussianDist
import umontreal.iro.lecuyer.probdist.LognormalDist
import umontreal.iro.lecuyer.probdist.NegativeBinomialDist
import umontreal.iro.lecuyer.probdist.NormalDist
import umontreal.iro.lecuyer.probdist.ParetoDist
import umontreal.iro.lecuyer.probdist.PiecewiseLinearEmpiricalDist
import umontreal.iro.lecuyer.probdist.PoissonDist
import umontreal.iro.lecuyer.probdist.StudentDist
import umontreal.iro.lecuyer.probdist.TriangularDist
import umontreal.iro.lecuyer.probdist.UniformDist

import static org.pillarone.riskanalytics.core.util.GroovyUtils.asDouble

class RandomDistributionFactory {

    private static Distribution getNormalDistribution(double mean, double stDev) {
        return new NormalDist(mean, stDev)
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
        assert obs.length == prob.length, "DiscreteEmpiricalGenerator requires lists of same length but got $obs.length and $prob.length"
        prob.each {cell -> probSum += cell}
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
        return getDistribution(DistributionType.UNIFORM, ['a': 0, 'b': 1])
    }

    /**
     * @return new RandomDistribution with distribution==null if parameters are invalid. Use ValidationService for better error messages.
     *
     * */
    static RandomDistribution getDistribution(DistributionType type, Map parameters) {
        RandomDistribution distribution = new RandomDistribution(type: type, parameters: parameters)
        //TODO msp move initialization to RD.getDistribution()
        try {
            switch (type) {
                case DistributionType.NORMAL:
                    if (parameters.isEmpty()) {
                        distribution.distribution = new NormalDist(0, 1)
                    }
                    else {
                        distribution.distribution = new NormalDist(parameters["mean"], parameters["stDev"])
                    }
                    break
                case DistributionType.LOGNORMAL:
                    distribution.distribution = getLognormalDistribution(parameters["mean"], parameters["stDev"])
                    break
                case DistributionType.LOGNORMAL_MU_SIGMA:
                    distribution.distribution = new LognormalDist(parameters["mu"], parameters["sigma"])
                    break
                case DistributionType.POISSON:
                    if (parameters.isEmpty()) {
                        distribution.distribution = new PoissonDist(0)
                    }
                    else {
                        distribution.distribution = new PoissonDist(parameters["lambda"])
                    }
                    break
                case DistributionType.NEGATIVEBINOMIAL:
                    distribution.distribution = new NegativeBinomialDist(parameters["gamma"], parameters["p"])
                    break
                case DistributionType.PARETO:
                    distribution.distribution = new ParetoDist(parameters["alpha"], parameters["beta"])
                    break
                case DistributionType.UNIFORM:
                    distribution.distribution = new UniformDist(parameters["a"], parameters["b"])
                    break
                case DistributionType.CONSTANT:
                    distribution.distribution = new ConstantDistribution(parameters["constant"])
                    break
                case DistributionType.DISCRETEEMPIRICAL:
                    distribution.distribution = getDiscreteEmpiricalDistribution(asDouble(parameters["discreteEmpiricalValues"].getColumnByName("observations")), asDouble(parameters["discreteEmpiricalValues"].getColumnByName("probabilities")))
                    break
                case DistributionType.DISCRETEEMPIRICALCUMULATIVE:
                    distribution.distribution = getDiscreteEmpiricalCumulativeDistribution(asDouble(parameters["discreteEmpiricalCumulativeValues"].getColumnByName("observations")), asDouble(parameters["discreteEmpiricalCumulativeValues"].getColumnByName("cumulative probabilities")) )
                    break
                case DistributionType.PIECEWISELINEAREMPIRICAL:
                    distribution.distribution = new PiecewiseLinearEmpiricalDist(asDouble(parameters["observations"].getColumnByName("observations")))
                    break
                case DistributionType.PIECEWISELINEAR:
                    distribution.distribution = new PiecewiseLinearDistribution(asDouble(parameters["supportPoints"].getColumnByName("values")), asDouble(parameters["supportPoints"].getColumnByName("cummulative probabilities")))
                    break
                case DistributionType.TRIANGULARDIST:
                    distribution.distribution = new TriangularDist(parameters["a"], parameters["b"], parameters["m"])
                    break
                case DistributionType.CHISQUAREDIST:
                    distribution.distribution = new ChiSquareDist(parameters["n"])
                    break
                case DistributionType.STUDENTDIST:
                    distribution.distribution = new StudentDist(parameters["n"])
                    break
                case DistributionType.BINOMIALDIST:
                    distribution.distribution = new BinomialDist(parameters["n"], parameters["p"])
                    break
                case DistributionType.INVERSEGAUSSIANDIST:
                    distribution.distribution = new InverseGaussianDist(parameters["mu"], parameters["lambda"])
                    break
            }
        } catch (IllegalArgumentException e) { // distribution stays null and validation will handle this
        }
        return distribution
    }
}