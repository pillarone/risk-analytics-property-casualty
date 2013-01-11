package org.pillarone.riskanalytics.domain.utils;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class GeneralizedParetoDistribution extends ContinuousDistribution {
    /** shape parameter */
    private double xi;
    /** threshold parameter */
    private double beta;
    /** positive scaling parameter */
    private double tau;


    public GeneralizedParetoDistribution(double xi, double beta, double tau) {
        setParams(xi, beta, tau);
    }

    public double density(double x) {
        return density(xi, beta, tau, x);
    }

    public double cdf(double x) {
        return cdf(xi, beta, tau, x);
    }

    public double barF(double x) {
        return barF(xi, beta, tau, x);
    }

    public double inverseF(double y) {
        return inverseF(xi, beta, tau, y);
    }

    /**
     * Computes the density function.
     * @param xi
     * @param beta
     * @param tau
     * @param x
     * @return
     * */
    public static double density(double xi, double beta, double tau, double x) {
        if (tau <= 0.0)
            throw new IllegalArgumentException("tau <= 0");
        if (xi == 0) {
            return x < beta ? 0 : 1 / tau * Math.exp(-1 / tau * (x - beta));
        }
        if (xi < 0) {
            return (x < beta || x >= beta - tau / xi) ? 0 : 1 / tau * Math.pow(1 +xi / tau * (x - beta), -1 / xi - 1);
        }
        return x < beta ? 0 : 1 / tau * Math.pow(1 + xi / tau * (x - beta), -1 / xi - 1);
    }


    /**
     * Computes the distribution function.
     * @param xi
     * @param beta
     * @param tau
     * @param x
     * @return
     * */
    public static double cdf(double xi, double beta, double tau, double x) {
        if (tau <= 0.0)
            throw new IllegalArgumentException("tau <= 0");
        if (x <= beta)
            return 0.0;
        if (xi == 0) {
            return 1.0 - Math.exp(-1 / tau * (x - beta));
        }
        if (xi < 0 && x >= beta - tau / xi) {
            return 1.0;
        }
        return 1.0 - Math.pow(1 + xi / tau * (x - beta), -1 / xi);
    }


    /**
     * Computes the complementary distribution function.
     * @param xi
     * @param beta
     * @param tau
     * @param x
     * @return
     * */
    public static double barF(double xi, double beta, double tau, double x) {
        if (tau <= 0.0)
            throw new IllegalArgumentException("tau <= 0");
        if (x <= beta)
            return 1.0;
        return 1.0 - cdf(xi, beta, tau, x);
    }


    /**
     * Computes the inverse of the distribution function.
     * @param xi
     * @param beta
     * @param tau
     * @param y
     * @return
     * */
    public static double inverseF(double xi, double beta, double tau, double y) {
        if (tau <= 0.0)
            throw new IllegalArgumentException("tau <= 0");
        if (y < 0.0 || y > 1.0)
            throw new IllegalArgumentException("y not in [0,1]");
        if (y <= 0.0)
            return beta;
        if (y >= 1.0 && xi >= 0) {
            return Double.POSITIVE_INFINITY;
        }
        if (y >= 1.0 && xi < 0) {
            return beta - tau / xi;
        }
        if (xi == 0) {
            return beta - tau * Math.log1p(-y);
        }
        return beta + tau / xi * (-1 + Math.pow(1 - y, -xi));
    }


    public void setParams(double xi, double beta, double tau) {
        if (tau <= 0.0)
            throw new IllegalArgumentException("tau <= 0");
        this.xi=xi;
        this.beta=beta;
        this.tau = tau;
        supportA = beta;
        if (xi < 0) {
            supportB = beta - tau / xi;
        }
    }

    public double[] getParams() {
        double[] retour = {xi, beta, tau};
        return retour;
    }

    public String toString() {
        return getClass().getSimpleName() + " : xi = " + xi + ", beta = " + beta + ", tau = " + tau;
    }

}


