package org.pillarone.riskanalytics.domain.utils;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.LognormalDist;
import umontreal.iro.lecuyer.probdist.NormalDist;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 *         <p/>
 *         Composite distribution in C^1 (f and f' are continuous) with three freely chosen parameters sigma, alpha, beta, lambda.
 *         Up to beta >0 the distribution is given by a lognormal distribution wiht parameters mu = mu(sigma,alpha,beta, lambda)
 *         and sigma that is right-truncated at beta. From beta the distribution is given by a generalized Pareto density
 *         in the form of the type II pareto density (a.k.a Lomax density) with parameters alpha, beta and lambda.
 *         Appropriate weightings of the truncated lognormal and type II pareto densities are given by a dependent parameter r = r(sigma,alpha,beta,lambda).*
 */
public class LognormalTypeIIParetoDistribution extends ContinuousDistribution {
    /**
     * caling parameter lognormal > 0
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

    public LognormalTypeIIParetoDistribution(double sigma, double alpha, double beta, double lambda) {
        setParams(sigma, alpha, beta, lambda);
    }

    private static double getMu(double sigma, double alpha, double beta, double lambda) {
        return Math.log(beta) - ((alpha * beta - lambda) / (lambda + beta)) * Math.pow(sigma, 2);
    }

    private static double getR(double sigma, double alpha, double beta, double lambda) {
        double mu = getMu(sigma, alpha, beta, lambda);
        double term = Math.sqrt(2d * Math.PI) * alpha * beta * sigma *
                NormalDist.cdf(mu, sigma, Math.log(beta)) * Math.exp(1 / 2d * Math.pow((Math.log(beta) - mu) / sigma, 2));
        return term / (lambda + beta + term);
    }

    public double density(double x) {
        return density(sigma, alpha, beta, lambda, x);
    }

    public double cdf(double x) {
        return cdf(sigma, alpha, beta, lambda, x);
    }

    public double barF(double x) {
        return barF(sigma, alpha, beta, lambda, x);
    }

    public double inverseF(double y) {
        return inverseF(sigma, alpha, beta, lambda, y);
    }

    /**
     * Computes the density function.
     */
    public static double density(double sigma, double alpha, double beta, double lambda, double x) {
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
        double r = getR(sigma, alpha, beta, lambda);
        if (x <= beta) {
            double mu = getMu(sigma, alpha, beta, lambda);
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.density(mu, sigma, x);
        }
        return (1.0 - r) * TypeIIParetoDistribution.density(alpha, beta, lambda, x);
    }

    /**
     * Computes the distribution function.
     */
    public static double cdf(double sigma, double alpha, double beta, double lambda, double x) {
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
        double r = getR(sigma, alpha, beta, lambda);
        if (x <= beta) {
            double mu = getMu(sigma, alpha, beta, lambda);
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.cdf(mu, sigma, x);
        }
        return r + (1.0 - r) * TypeIIParetoDistribution.cdf(alpha, beta, lambda, x);
    }

    /**
     * Computes the complementary distribution function.
     */
    public static double barF(double sigma, double alpha, double beta, double lambda, double x) {
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
        return 1.0 - cdf(sigma, alpha, beta, lambda, x);
    }

    /**
     * Computes the inverse of the distribution function.
     */
    public static double inverseF(double sigma, double alpha, double beta, double lambda, double y) {
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
        double r = getR(sigma, alpha, beta, lambda);
        if (y <= r) {
            double mu = getMu(sigma, alpha, beta, lambda);
            return LognormalDist.inverseF(mu, sigma, y / r * NormalDist.cdf(mu, sigma, Math.log(beta)));
        }
        return TypeIIParetoDistribution.inverseF(alpha, beta, lambda, (y - r) / (1.0 - r));
    }


    public double getBeta() {
        return beta;
    }

    public void setParams(double sigma, double alpha, double beta, double lambda) {
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
        supportA = 0.0;
    }

    public double[] getParams() {
        double[] retour = {sigma, alpha, beta, lambda};
        return retour;
    }

    public String toString() {
        return getClass().getSimpleName() + " : sigma = " + sigma + ", alpha = " + alpha + ", beta = " + beta + ", lambda = " + lambda;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }
}


