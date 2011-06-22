package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.DiscreteDistribution

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ConstantDistribution extends DiscreteDistribution {

    double constant

    ConstantDistribution(double constant) {
        this.constant = constant
    }

    public double cdf(double x) {
        if (x < constant) {
            return 0
        }
        else {
            return 1
        }
    }

    public double barF(double x) {
        return 1-cdf(x)
    }

    public double inverseF(double x) {
        return constant
    }

    public double getMean() {
        return constant
    }

    public double getVariance() {
        return 0
    }

    public double getStandardDeviation() {
        return 0
    }

    public double[] getParams() {
        return (double[]) [1,constant,1.0]
    }

}