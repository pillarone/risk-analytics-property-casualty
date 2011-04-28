package org.pillarone.riskanalytics.domain.utils;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.LognormalDist;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.ParetoDist;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 *         <p/>
 *         Composite distribution in C^1 (f and f' are continuous) with three freely chosen parameters sigma, alpha, beta.
 *         Up to beta >0 the distribution is given by a lognormal distribution wiht parameters mu = mu(sigma,alpha,beta)
 *         and sigma that is right-truncated at beta. From beta the distribution is given by a Preto denisty with parameters alpha and beta.
 *         Appropriate weightings of the truncated lognormal and pareto densities are given by a dependent parameter r = r(sigma,alpha,beta).
 */
public class LognormalParetoDistribution extends ContinuousDistribution {
    /**
     * caling parameter lognormal > 0
     */
    private double sigma;
    /**
     * shape parameter pareto >0
     */
    private double alpha;
    /**
     * threshold parameter > 0
     */
    private double beta;


    public LognormalParetoDistribution(double sigma, double alpha, double beta) {
        setParams(sigma, alpha, beta);
    }

    private static double getMu(double sigma, double alpha, double beta) {
        return Math.log(beta) - alpha * Math.pow(sigma, 2);
    }

    private static double getR(double sigma, double alpha, double beta) {
        double term = Math.sqrt(2d * Math.PI) * alpha * sigma * NormalDist.cdf(0.0, 1.0, alpha * sigma) * Math.exp(1 / 2d * Math.pow(alpha * sigma, 2));
        return term / (1.0 + term);
    }

    public double density(double x) {
        return density(sigma, alpha, beta, x);
    }

    public double cdf(double x) {
        return cdf(sigma, alpha, beta, x);
    }

    public double barF(double x) {
        return barF(sigma, alpha, beta, x);
    }

    public double inverseF(double y) {
        return inverseF(sigma, alpha, beta, y);
    }

    /**
     * Computes the density function.
     */
    public static double density(double sigma, double alpha, double beta, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (x <= 0) {
            return 0;
        }
        double r = getR(sigma, alpha, beta);
        if (x <= beta) {
            double mu = getMu(sigma, alpha, beta);
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.density(mu, sigma, x);
        }
        return (1.0 - r) * ParetoDist.density(alpha, beta, x);
    }

    /**
     * Computes the distribution function.
     */
    public static double cdf(double sigma, double alpha, double beta, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (x <= 0) {
            return 0.0;
        }
        double r = getR(sigma, alpha, beta);
        if (x <= beta) {
            double mu = getMu(sigma, alpha, beta);
            return r / NormalDist.cdf(mu, sigma, Math.log(beta)) * LognormalDist.cdf(mu, sigma, x);
        }
        return r + (1.0 - r) * ParetoDist.cdf(alpha, beta, x);
    }

    /**
     * Computes the complementary distribution function.
     */
    public static double barF(double sigma, double alpha, double beta, double x) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (x <= 0) {
            return 1.0;
        }
        return 1.0 - cdf(sigma, alpha, beta, x);
    }

    /**
     * Computes the inverse of the distribution function.
     */
    public static double inverseF(double sigma, double alpha, double beta, double y) {
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
        double r = getR(sigma, alpha, beta);
        if (y <= r) {
            double mu = getMu(sigma, alpha, beta);
            return LognormalDist.inverseF(mu, sigma, y / r * NormalDist.cdf(mu, sigma, Math.log(beta)));
        }
        return ParetoDist.inverseF(alpha, beta, (y - r) / (1.0 - r));
    }


    public double getBeta() {
        return beta;
    }

    public void setParams(double sigma, double alpha, double beta) {
        if (sigma <= 0.0)
            throw new IllegalArgumentException("sigma <= 0");
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        this.sigma = sigma;
        this.alpha = alpha;
        this.beta = beta;
        supportA = 0.0;
    }

    public double[] getParams() {
        double[] retour = {sigma, alpha, beta};
        return retour;
    }

    public String toString() {
        return getClass().getSimpleName() + " : sigma = " + sigma + ", alpha = " + alpha + ", beta = " + beta;
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
}


