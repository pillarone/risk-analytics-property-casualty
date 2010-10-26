package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.probdist.ContinuousDistribution

/**
 * Implements the interface of SSJ (external university of Montreal) package
 * in order to be used in the same way as the other predefined distributions.
 *
 * Date: 31.07.2008,  10:25:17
 * @author stefan.zumsteg@intuitive-collaboration.com
 */
class PiecewiseLinearDistribution extends ContinuousDistribution {

    // todo(jwa): validator to check if values strongly monotonically increasing
    List<Double> val                //sortedValues contains a series of monotonically increasing values
    List<Double> cdft                //cdft[i] is probability that value of random variable is <=sortedValues[i]
    //cdft[0]=0, , cdft[last]= 1
    private int last
    private double mean
    private double stdDev
    private double variance
    List<Double> probabilities

    /** Constructor checks validity of arguments:
     *  - matching list length
     *  - monotonically increasing values
     *  - monotonically increasing  cdft with first value=0, last=1
     *
     * sets all internal variables
     */
    PiecewiseLinearDistribution(double[] values, double[] cumulProb) {
        last = values.size() - 1;
        val = []
        cdft = []
        probabilities = []
        if (values.size() != cumulProb.size())
            throw new IllegalArgumentException("PiecewiseLinearDistribution.invalidNumberOfArguments");
        if (cumulProb[0] != 0) throw new IllegalArgumentException("PiecewiseLinearDistribution.invalidFirstFunctionValue");
        val.add(values[0]); cdft.add(cumulProb[0]);
        probabilities.add(cumulProb[0])
        if (cumulProb[last] != 1) throw new IllegalArgumentException("PiecewiseLinearDistribution.invalidLastFunctionValue");
        for (int i = 1; i <= last; i++) {
            if (values[i] <= values[i - 1])
                throw new IllegalArgumentException("PiecewiseLinearDistribution.nonincreasingArguments");
            if (cumulProb[i] <= cumulProb[i - 1]) {
                throw new IllegalArgumentException("PiecewiseLinearDistribution.nonincreasingFunctionValues");
            }
            val.add(values[i]);
            cdft.add(cumulProb[i]);
            probabilities.add(cumulProb[i]-cumulProb[i-1])

        }
        computeDescriptiveStatVars()
    }

    void computeDescriptiveStatVars() {
        mean = 0
        variance = 0
        double meanSquare = 0
        for (int i = 1; i <= last; i++) {
            double pieceProb = cdft[i] - cdft[i - 1]
            mean += pieceProb * (val[i] + val[i - 1]) / 2

            meanSquare += pieceProb *  //expected value of x^2 for values between sortedValues[i-1] and sortedValues[i]
                    (Math.pow(val[i], 3) - Math.pow(val[i - 1], 3)) /
                    (3 * (val[i] - val[i - 1])
                    )
        }
        variance = meanSquare - mean * mean
        stdDev = Math.sqrt(variance)
    }

    /*This implementation is not efficient
     * for improved performance with sets having a large number of support points a binary search should be implemented
    */

    double inverseF(double u) {
        if ((u < 0) || (u > 1)) throw new IllegalArgumentException("PiecewiseLinearDistribution.invalidArgumentsInverse");
        int i = 1;                                     //first piece of cdft containing u
        while (u > cdft[i]) {i++}                       //now we have cdft[i-1]<=u<=cdft[i]    (even cdft[i-1]<u for u>0)
        double pos = (u - cdft[i - 1]) / (cdft[i] - cdft[i - 1])    //pos is relative location of u in that interval
        return (1 - pos) * val[i - 1] + pos * val[i]
    }

    double cdf(double v) {
        if (v <= val[0]) return 0.0;
        if (v >= val[last]) return 1.0
        int i = 1;                                     //first piece of sortedValues containing v
        while (v > val[i]) {i++}                       //now we have sortedValues[i-1]<v<=sortedValues[i]
        double pos = (v - val[i - 1]) / (val[i] - val[i - 1])    //pos is relative location of v in that interval
        return (1 - pos) * cdft[i - 1] + pos * cdft[i]
    }


    double barF(double v) {return 1 - cdf(v)}

    double density(double v){
        if (v< val[0]) return 0.0
        if (v >= val[last]) return 0.0
        int i=1;
        while (v > val[i]) {i++}
        return probabilities[i-1] 
    }

    double getMean() {return mean}

    double getVariance() {return variance}

    double getStandardDeviation() {return stdDev}

    double[] getParams() {
        throw new IllegalArgumentException("PiecewiseLinearDistribution.invalidMethodgetParams");
        // return []    //that's another lazy alternative implementation
    }

    ;
    //Returns the parameters of the distribution function in the same order as in the constructors.
}