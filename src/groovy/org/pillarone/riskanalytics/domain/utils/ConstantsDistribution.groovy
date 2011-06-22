package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.DiscreteDistribution

/**
 * This may actually not differ at all from a discrete (or discrete empirical) distribution; however,
 * you can create an instance of this from any sequence of values (array of doubles), even one with duplicates.
 * This distribution counts and sorts unique values from the sequence of values it is given, and uses these to
 * efficiently compute cdf & inverseF calculations. 
 *
 * Uses a rudimentary "binary search" for cdf & inverseF calculations; should work for reasonably sized arrays.
 *
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class ConstantsDistribution extends DiscreteDistribution {

    List<Double> values // ordered list of values
    List<Double> sortedValues
    List<Double> cdft // cdft[0]=(multiplicity of least distinct value)/(total number of values), cdft[last]=1
    Map<Double, Double> valueCount // counts of each distinct value
    private int length
    private int distinctValueCount
    private int leadBit // greatest power of two <= distinctValueCount; used for "binary search" in cdf & inverseF
    private double mean
    private double stdDev
    private double variance
    private double eventProbability
    private List<Double> probabilities

    ConstantsDistribution(double[] constants) {
        length = constants.size();
        valueCount = new LinkedHashMap<Double, Double>(length)
        values = new ArrayList<Double>(length)
        for (int i = 0; i < length; i++) {
            double value = constants[i]
            values.add(value)
            if (valueCount.containsKey(value)) {
                valueCount.put(value, valueCount.get(value) + 1)
            }
            else {
                valueCount.put(value, 1)
            }
        }

        computeDescriptiveStatistics()
        computeOrdinalStatistics()
        computeDistinctValueCount()
    }

    private void computeDescriptiveStatistics() {
        mean = 0
        double meanSquare = 0
        for (double value: values) {
            mean += value
            meanSquare += value * value
        }
        eventProbability = length > 0 ? 1d / length : 1d
        mean *= eventProbability
        meanSquare *= eventProbability
        variance = meanSquare - mean * mean
        stdDev = Math.sqrt(variance)
    }

    private void computeOrdinalStatistics() {
        sortedValues = valueCount.keySet().sort()
        cdft = []
        probabilities = []
        int cumCount = 0
        for (double value: sortedValues) {
            cumCount += valueCount.get(value)
            cdft.add(eventProbability * cumCount)
            probabilities.add(eventProbability*valueCount.get(value))
        }
    }

    private void computeDistinctValueCount() {
        distinctValueCount = leadBit = sortedValues.size()
        // to compute leadBit, start with distinctValueCount and take out the lower bits one by one
        // note that this algorithm will only work up to the sign bit, i.e. up to 2^31-1 or 2^63-1
        for (int bit = 1; bit < leadBit; bit <<= 1) {
            int xor = bit ^ leadBit // if this is smaller than leadBit, but still positive, use it
            if (xor > 0 && leadBit > xor) leadBit = xor
        }
    }

    double inverseF(double u) {
        if ((u < 0) || (u > 1)) throw new IllegalArgumentException("ConstantsDistribution.invalidArguments");
        if (u <= cdft[0]) return sortedValues[0]
        if (u >= cdft[distinctValueCount - 1]) return sortedValues[distinctValueCount - 1]
        // find the least i so that u <= cdft[i]
        // reformulate the problem: negate the test (test not <=u, but >u), invert the direction (take greatest, not least, i)
        int i = 0 // find the greatest cdft[i] < u; try all binary numbers i, looping on the bits from most to least significant
        for (int bit = leadBit; bit > 0 && cdft[i] < u; bit >>= 1) {
            int j = i + bit
            if (j < length && cdft[j] < u) i = j
        }
        i++ // add one to get the least i having cdft[i] >= u
        return sortedValues[i]
    }

    double cdf(double v) {
        if (v < sortedValues[0]) return 0.0;
        if (v >= sortedValues[length - 1]) return 1.0
        // find the least i so that v <= sortedValues[i]
        int i = 0
        for (int bit = leadBit; bit > 0 && sortedValues[i] < v; bit >>= 1) {
            int j = i + bit
            if (j < length && sortedValues[j] <= v) i = j
        }
        return cdft[i]
    }

    double barF(double v) {return 1 - cdf(v)}

    double getMean() {return mean}

    double getVariance() {return variance}

    double getStandardDeviation() {return stdDev}

    double[] getParams() {
        return (double[]) [sortedValues.size(), sortedValues, probabilities]
    }

     /**
    * Returns a <TT>String</TT> containing information about the constants distribution.
    *
    */
   public String toString() {
      System.out.println(values);
       StringBuilder sb = new StringBuilder();
      Formatter formatter = new Formatter(sb, Locale.US);
      formatter.format("%s%n", getClass().getSimpleName());
      formatter.format("%s%n", "constants");
      int i;
      for(i = 0; i<values.size(); i++) {
         formatter.format("%f%n", values[i]);
      }
      return sb.toString();
   }

}