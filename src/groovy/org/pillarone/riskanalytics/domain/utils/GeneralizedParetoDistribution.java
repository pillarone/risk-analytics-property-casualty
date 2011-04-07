package org.pillarone.riskanalytics.domain.utils;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class GeneralizedParetoDistribution extends ContinuousDistribution {
    /** shape parameter */
    private double k;
    /** threshold parameter */
    private double beta;
    /** positive scaling parameter */
    private double zeta; 


    public GeneralizedParetoDistribution(double k, double beta, double zeta) {
        setParams(k, beta, zeta);
    }

    public double density(double x) {
        return density(k, getBeta(), zeta, x);
    }

    public double cdf(double x) {
        return cdf(k, getBeta(), zeta, x);
    }

    public double barF(double x) {
        return barF(k, getBeta(), zeta, x);
    }

    public double inverseF(double y) {
        return inverseF(k, getBeta(), zeta, y);
    }

    /**
     * Computes the density function.
     */
    public static double density(double k, double beta, double zeta, double x) {
        if (zeta <= 0.0)
            throw new IllegalArgumentException("zeta <= 0");
        if (k == 0) {
            return x < beta ? 0 : 1 / zeta * Math.exp(-1 / zeta * (x - beta));
        }
        if (k < 0) {
            return (x < beta || x >= beta - zeta / k) ? 0 : 1 / zeta * Math.pow(1 + k / zeta * (x - beta), -1 / k - 1);
        }
        return x < beta ? 0 : 1 / zeta * Math.pow(1 + k / zeta * (x - beta), -1 / k - 1);
    }


    /**
     * Computes the distribution function.
     */
    public static double cdf(double k, double beta, double zeta, double x) {
        if (zeta <= 0.0)
            throw new IllegalArgumentException("zeta <= 0");
        if (x <= beta)
            return 0.0;
        if (k == 0) {
            return 1.0 - Math.exp(-1 / zeta * (x - beta));
        }
        if (k < 0 && x >= beta - zeta / k) {
            return 1.0;
        }
        return 1.0 - Math.pow(1 + k / zeta * (x - beta), -1 / k);
    }


    /**
     * Computes the complementary distribution function.
     */
    public static double barF(double k, double beta, double zeta, double x) {
        if (zeta <= 0.0)
            throw new IllegalArgumentException("zeta <= 0");
        if (x <= beta)
            return 1.0;
        return 1.0 - cdf(k, beta, zeta, x);
    }


    /**
     * Computes the inverse of the distribution function.
     */
    public static double inverseF(double k, double beta, double zeta, double y) {
        if (zeta <= 0.0)
            throw new IllegalArgumentException("zeta <= 0");
        if (y < 0.0 || y > 1.0)
            throw new IllegalArgumentException("y not in [0,1]");
        if (y <= 0.0)
            return beta;
        if (y >= 1.0 && k >= 0) {
            return Double.POSITIVE_INFINITY;
        }
        if (y >= 1.0 && k < 0) {
            return beta - zeta / k;
        }
        if (k == 0) {
            return beta - zeta * Math.log1p(-y);
        }
        return beta + zeta / k * (-1 + Math.pow(1 - y, -k));
    }


    public double getBeta() {
        return beta;
    }

    public void setParams(double k, double beta, double zeta) {
        if (zeta <= 0.0)
            throw new IllegalArgumentException("zeta <= 0");
        this.setK(k);
        this.setBeta(beta);
        this.zeta = zeta;
        supportA = beta;
        if (k < 0) {
            supportB = beta - zeta / k;
        }
    }

    public double[] getParams() {
        double[] retour = {getK(), getBeta(), zeta};
        return retour;
    }

    public String toString() {
        return getClass().getSimpleName() + " : k = " + getK() + ", beta = " + getBeta() + ", zeta = " + zeta;
    }

    public double getZeta() {
        return zeta;
    }

    public void setZeta(double zeta) {
        this.zeta = zeta;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}


