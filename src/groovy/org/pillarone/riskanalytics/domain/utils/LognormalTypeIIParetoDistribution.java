package org.pillarone.riskanalytics.domain.utils;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.LognormalDist;
import umontreal.iro.lecuyer.probdist.NormalDist;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 *         <p/>
 *         Composite distribution in C^1 (probability density f and f' are continuous) with three freely chosen parameters sigma, alpha, beta, lambda.
 *         Alternatively, composite distribution in C (probability density f ist continuous) with five degrees of freedom.
 *         Up to beta >0 the distribution is given by a lognormal distribution with parameters mu free or mu = mu(sigma,alpha,beta, lambda)
 *         and sigma that is right-truncated at beta. From beta the distribution is given by a generalized Pareto density
 *         in the form of the type II pareto density (a.k.a Lomax density) with parameters alpha, beta and lambda.
 *         Appropriate weightings of the truncated lognormal and type II pareto densities are given by a dependent parameter r = r(sigma,alpha,beta,lambda, mu).*
 */
public class LognormalTypeIIParetoDistribution extends ContinuousDistribution {
    /**
     * calling parameter lognormal > 0
     */
    private double sigma;
    /**
     * shape parameter type II pareto >0
     */
    private double alpha;
    /**
     * threshold parameter > 0
     */
    private double beta;
    /**
     * scaling parameter type II pareto >-beta
     */
    private double lambda;
    /**
     * mean parameter lognormal
     */
    private double mu;

    public LognormalTypeIIParetoDistribution(double sigma, double alpha, double beta, double lambda) {
        setParams(sigma, alpha, beta, lambda);
    }

    public LognormalTypeIIParetoDistribution(double sigma, double alpha, double beta, double lambda, double mu) {
        setParams(sigma, alpha, beta, lambda, mu);
    }

    private static double getR(double sigma, double alpha, double beta, double lambda, double mu) {
        double term = Math.sqrt(2d * Math.PI) * alpha * beta * sigma *
                NormalDist.cdf(mu, sigma, Math.log(beta)) * Math.exp(1 / 2d * Math.pow((Math.log(beta) - mu) / sigma, 2));
        return term / (lambda + beta + term);
    }

    public double density(double x) {
        return density(sigma, alpha, beta, lambda, mu, x);
    }

    public double cdf(double x) {
        return cdf(sigma, alpha, beta, lambda, mu, x);
    }

    public double barF(double x) {
        return barF(sigma, alpha, beta, lambda, mu, x);
    }

    public double inverseF(double y) {
        return inverseF(sigma, alpha, beta, lambda, mu, y);
    }

    /**
     * Computes the density function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param x
     * @return
     */
    public static double density(double sigma, double alpha, double beta, double lambda, double x) {
        double mu = Math.log(beta) - ((alpha * beta - lambda) / (lambda + beta)) * Math.pow(sigma, 2);
        return density(sigma, alpha, beta, lambda, mu, x);
    }

    /**
     * Computes the density function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param mu
     * @param x
     * @return
     */
    public static double density(double sigma, double alpha, double beta, double lambda, double mu, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        if (x <= 0) {
            return 0;
        }
        double r = getR(sigma, alpha, beta, lambda, mu);
        if (x <= beta) {
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.density(mu, sigma, x);
        }
        return (1.0 - r) * TypeIIParetoDistribution.density(alpha, beta, lambda, x);
    }

    /**
     * Computes the distribution function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param x
     * @return
     */
    public static double cdf(double sigma, double alpha, double beta, double lambda, double x) {
        double mu = Math.log(beta) - ((alpha * beta - lambda) / (lambda + beta)) * Math.pow(sigma, 2);
        return cdf(sigma, alpha, beta, lambda, mu, x);
    }

    /**
     * Computes the distribution function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param mu
     * @param x
     * @return
     */
    public static double cdf(double sigma, double alpha, double beta, double lambda, double mu, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        if (x <= 0) {
            return 0.0;
        }
        double r = getR(sigma, alpha, beta, lambda, mu);
        if (x <= beta) {
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.cdf(mu, sigma, x);
        }
        return r + (1.0 - r) * TypeIIParetoDistribution.cdf(alpha, beta, lambda, x);
    }

    /**
     * Computes the complementary distribution function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param x
     * @return
     */
    public static double barF(double sigma, double alpha, double beta, double lambda, double x) {
        double mu = Math.log(beta) - ((alpha * beta - lambda) / (lambda + beta)) * Math.pow(sigma, 2);
        return barF(sigma, alpha, beta, lambda, mu, x);
    }

    /**
     * Computes the complementary distribution function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param mu
     * @param x
     * @return
     */
    public static double barF(double sigma, double alpha, double beta, double lambda, double mu, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        if (x <= 0) {
            return 1.0;
        }
        return 1.0 - cdf(sigma, alpha, beta, lambda, mu, x);
    }

    /**
     * Computes the inverse of the distribution function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param y
     * @return
     */
    public static double inverseF(double sigma, double alpha, double beta, double lambda, double y) {
        double mu = Math.log(beta) - ((alpha * beta - lambda) / (lambda + beta)) * Math.pow(sigma, 2);
        return inverseF(sigma, alpha, beta, lambda, mu, y);
    }

    /**
     * Computes the inverse of the distribution function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param lambda
     * @param mu
     * @param y
     * @return
     */
    public static double inverseF(double sigma, double alpha, double beta, double lambda, double mu, double y) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        if (y < 0.0 || y > 1.0)
            throw new IllegalArgumentException("y not in [0,1]");
        if (y <= 0.0)
            return 0.0;
        if (y >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }
        double r = getR(sigma, alpha, beta, lambda, mu);
        if (y <= r) {
            return LognormalDist.inverseF(mu, sigma, y / r * NormalDist.cdf(mu, sigma, Math.log(beta)));
        }
        return TypeIIParetoDistribution.inverseF(alpha, beta, lambda, (y - r) / (1.0 - r));
    }

    public void setParams(double sigma, double alpha, double beta, double lambda) {
        double mu = Math.log(beta) - ((alpha * beta - lambda) / (lambda + beta)) * Math.pow(sigma, 2);
        setParams(sigma, alpha, beta, lambda, mu);
    }

    public void setParams(double sigma, double alpha, double beta, double lambda, double mu) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        this.sigma = sigma;
        this.alpha = alpha;
        this.beta = beta;
        this.lambda = lambda;
        this.mu = mu;
        supportA = 0.0;
    }


    public double[] getParams() {
        double[] retour = {sigma, alpha, beta, lambda, mu};
        return retour;
    }

    public String toString() {
        return getClass().getSimpleName() + " : sigma = " + sigma + ", alpha = " + alpha + ", beta = " + beta + ", lambda = " + lambda;
    }

}


