package org.pillarone.riskanalytics.domain.utils
/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
interface IRandomVariateDistribution {
    /** u - value in the interval (0, 1)    */
    double inverseDistributionFunction(double u)

    /** computes and returns the distribution function F(x)   */
    double cdf(double x)
}