package org.pillarone.riskanalytics.domain.utils;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.LognormalDist;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.ParetoDist;

/**
 *  Composite distribution in C^1 (probability density f and f' are continuous) with three freely chosen parameters
 *  sigma, alpha, beta. Alternatively, composite distribution in C (probability density f is continuous) with four
 *  degrees of freedom. Up to beta > 0 the distribution is given by a lognormal distribution with parameters mu free or
 *  mu = mu(sigma, alpha, beta) and sigma that is right-truncated at beta. From beta the distribution is given by a
 *  Pareto density with parameters alpha and beta. Appropriate weightings of the truncated lognormal and pareto
 *  densities are given by a dependent parameter r = r(sigma, alpha, beta, mu).
 *
 *  @author jessika.walter (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class LognormalParetoDistribution extends ContinuousDistribution {
    /**
     * scaling parameter lognormal > 0
     */
    private double sigma;
    /**
     * shape parameter pareto > 0
     */
    private double alpha;
    /**
     * threshold parameter > 0
     */
    private double beta;
    /**
     * mean lognormal
     */
    private double mu;


    public LognormalParetoDistribution(double sigma, double alpha, double beta) {
        setParams(sigma, alpha, beta);
    }

    public LognormalParetoDistribution(double sigma, double alpha, double beta, double mu) {
        setParams(sigma, alpha, beta, mu);
    }


    //private static double getR(double sigma, double alpha, double beta) {
    //    double term = Math.sqrt(2d * Math.PI) * alpha * sigma * NormalDist.cdf(0.0, 1.0, alpha * sigma) * Math.exp(1 / 2d * Math.pow(alpha * sigma, 2.0));
    //    return term / (1.0 + term);
   // }

    private static double getR(double sigma, double alpha, double beta, double mu) {
        double term = Math.sqrt(2d * Math.PI) * alpha * sigma * NormalDist.cdf(mu, sigma, Math.log(beta)) * Math.exp(1 / 2d * Math.pow((Math.log(beta) - mu) / sigma, 2.0));
        return term / (1.0 + term);
    }

    public double density(double x) {
        return density(sigma, alpha, beta, mu, x);
    }

    public double cdf(double x) {
        
        return cdf(sigma, alpha, beta, mu, x);
    }

    public double barF(double x) {
        return barF(sigma, alpha, beta, mu, x);
    }

    public double inverseF(double y) {
        return inverseF(sigma, alpha, beta, mu, y);
    }

    /**
     * Computes the density function.
     * @param sigma
     * @param alpha
     * @param beta
     * @param x
     * @return
     */
    public static double density(double sigma, double alpha, double beta, double x) {
        double mu = Math.log(beta) - alpha * Math.pow(sigma, 2.0);
        return density(sigma, alpha, beta, mu, x);
    }

    /**
     * Computes the density function.
     * @param sigma
     * @param alpha
     * @param beta
     * @param mu
     * @param x
     * @return
     */
    public static double density(double sigma, double alpha, double beta, double mu, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (x <= 0) {
            return 0;
        }
        double r = getR(sigma, alpha, beta, mu);
        if (x <= beta) {
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.density(mu, sigma, x);
        }
        return (1.0 - r) * ParetoDist.density(alpha, beta, x);
    }

    /**
     * Computes the distribution function.
     * @param sigma
     * @param alpha
     * @param beta
     * @param x
     * @return
     */
    public static double cdf(double sigma, double alpha, double beta, double x) {
        double mu = Math.log(beta) - alpha * Math.pow(sigma, 2.0);
        return cdf(sigma, alpha, beta, mu, x);
    }

    /**
     * Computes the distribution function.
     * @param sigma
     * @param alpha
     * @param beta
     * @param mu
     * @param x
     * @return
     */
    public static double cdf(double sigma, double alpha, double beta, double mu, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (x <= 0) {
            return 0.0;
        }
        double r = getR(sigma, alpha, beta, mu);
        if (x <= beta) {
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.cdf(mu, sigma, x);
        }
        return r + (1.0 - r) * ParetoDist.cdf(alpha, beta, x);
    }

    /**
     * Computes the complementary distribution function.
     * @param sigma
     * @param alpha
     * @param beta
     * @param x
     * @return
     */
    public static double barF(double sigma, double alpha, double beta, double x) {
        double mu = Math.log(beta) - alpha * Math.pow(sigma, 2.0);
        return barF(sigma, alpha, beta, mu, x);
    }

    /**
     * Computes the complementary distribution function.
     * @param sigma
     * @param alpha
     * @param beta
     * @param mu
     * @param x
     * @return
     */
    public static double barF(double sigma, double alpha, double beta, double mu, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (x <= 0) {
            return 1.0;
        }
        return 1.0 - cdf(sigma, alpha, beta, mu, x);
    }

    /**
     * Computes the inverse of the distribution function.
     * @param sigma
     * @param alpha
     * @param beta
     * @param y
     * @return
     */
    public static double inverseF(double sigma, double alpha, double beta, double y) {
        double mu = Math.log(beta) - alpha * Math.pow(sigma, 2.0);
        return inverseF(sigma, alpha, beta, mu, y);
    }

    /**
     * Computes the inverse of the distribution function.
     *
     * @param sigma
     * @param alpha
     * @param beta
     * @param mu
     * @param y
     * @return
     */
    public static double inverseF(double sigma, double alpha, double beta, double mu,double y) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (y < 0.0 || y > 1.0)
            throw new IllegalArgumentException("y not in [0,1]");
        if (y <= 0.0)
            return 0.0;
        if (y >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }
        double r = getR(sigma, alpha, beta, mu);
        if (y <= r) {
            return LognormalDist.inverseF(mu, sigma, y / r * NormalDist.cdf(mu, sigma, Math.log(beta)));
        }
        return ParetoDist.inverseF(alpha, beta, (y - r) / (1.0 - r));
    }


    public double getBeta() {
        return beta;
    }

    public void setParams(double sigma, double alpha, double beta) {
        double mu = Math.log(beta) - alpha * Math.pow(sigma, 2.0);
        setParams(sigma, alpha, beta, mu);
    }

    public void setParams(double sigma, double alpha, double beta, double mu) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        this.sigma = sigma;
        this.alpha = alpha;
        this.beta = beta;
        this.mu = mu;
        supportA = 0.0;
    }

    public double[] getParams() {
        return new double[]{sigma, alpha, beta, mu};
    }

    public String toString() {
        return getClass().getSimpleName() + " : sigma = " + sigma + ", alpha = " + alpha + ", beta = " + beta + ", mu = " + mu;
    }

}


