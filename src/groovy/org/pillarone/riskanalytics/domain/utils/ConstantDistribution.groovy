package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution

/**
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ConstantDistribution implements Distribution {

    double constant

    ConstantDistribution(double constant) {
        this.constant = constant
    }

    public double cdf(double v) {
        return constant
    }

    public double barF(double v) {
        return constant
    }

    public double inverseF(double v) {
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
        return [constant]
    }

}