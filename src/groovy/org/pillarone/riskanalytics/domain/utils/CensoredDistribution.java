package org.pillarone.riskanalytics.domain.utils;

import org.apache.commons.lang.NotImplementedException;
import umontreal.iro.lecuyer.probdist.Distribution;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class CensoredDistribution implements Distribution {

    Distribution distribution;
    double min;
    double max;

    public CensoredDistribution(Distribution distribution, double min, double max) {
        this.distribution = distribution;
        this.min = min;
        this.max = max;
    }

    public double cdf(double v) {
        if (v < min) {
            return 0;
        }
        else if (v >= max) {
            return 1;
        }
        else {
            return distribution.cdf(v);
        }
    }

    public double barF(double v) {
        return 1d - cdf(v);
    }

    public double inverseF(double v) {
        double minU = cdf(min);
        double maxU = distribution.cdf(max);
        if (v <= minU) {
            return min;
        }
        else if (v >= maxU) {
            return max;
        }
        else {
            return distribution.inverseF(v);
        }
    }

    public double getMean() {
        throw new NotImplementedException("CensoredDistribution.noImplementationOfGetMean");
    }

    public double getVariance() {
        throw new NotImplementedException("CensoredDistribution.noImplementationOfGetVariance");
    }

    public double getStandardDeviation() {
        throw new NotImplementedException("CensoredDistribution.noImplementationOfGetStandardDeviation");
    }

    public double[] getParams() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
