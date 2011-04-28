package org.pillarone.riskanalytics.domain.utils;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class TypeIIParetoDistribution extends ContinuousDistribution {
    // special case of generalized pareto
    private double alpha;  // shape parameter >0; alpha = 1/k
    private double beta; // threshold parameter >0
    private double lambda; // scaling parameter lambda = alpha*zeta - beta >-beta


    public TypeIIParetoDistribution(double alpha, double beta, double lambda) {
        setParams(alpha, beta, lambda);
    }

    public double density(double x) {
        return density(alpha, beta, lambda, x);
    }

    public double cdf(double x) {
        return cdf(alpha, beta, lambda, x);
    }

    public double barF(double x) {
        return barF(alpha, beta, lambda, x);
    }

    public double inverseF(double y) {
        return inverseF(alpha, beta, lambda, y);
    }

    /**
     * Computes the density function.
     */
    public static double density(double alpha, double beta, double lambda, double x) {
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        return GeneralizedParetoDistribution.density(1/alpha,beta,1/alpha*(lambda+beta),x);
    }


    /**
     * Computes the distribution function.
     */
    public static double cdf(double alpha, double beta, double lambda, double x) {
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        return GeneralizedParetoDistribution.cdf(1/alpha,beta,1/alpha*(lambda+beta),x);
    }


    /**
     * Computes the complementary distribution function.
     */
    public static double barF(double alpha, double beta, double lambda, double x) {
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        if (x <= beta)
            return 1.0;
        return 1.0 - cdf(alpha, beta, lambda, x);
    }


    /**
     * Computes the inverse of the distribution function.
     */
    public static double inverseF(double alpha, double beta, double lambda, double y) {
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        return GeneralizedParetoDistribution.inverseF(1/alpha,beta,1/alpha*(lambda+beta),y);
    }


    public double getBeta() {
        return beta;
    }

    public void setParams(double alpha, double beta, double lambda) {
        if (alpha <= 0.0)
            throw new IllegalArgumentException("alpha <= 0");
        if (beta <= 0.0)
            throw new IllegalArgumentException("beta <= 0");
        if (lambda <= -beta)
            throw new IllegalArgumentException("lambda <= -beta");
        this.alpha=alpha;
        this.setBeta(beta);
        this.lambda = lambda;
        supportA = beta;
    }

    public double[] getParams() {
        double[] retour = {alpha, beta, lambda};
        return retour;
    }

    public String toString() {
        return getClass().getSimpleName() + " : alpha = " + alpha + ", beta = " +  beta + ", lambda = " + lambda;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}


