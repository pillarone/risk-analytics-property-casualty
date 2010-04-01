package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.randvar.RandomVariateGen

/**
 * @author: ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class ConstantsVariateGenerator extends RandomVariateGen {

    List<Double> constants

    private int length
    private int state

    public ConstantsVariateGenerator(List<Double> constants) {
        this.constants = constants
        length = this.constants.size()
        if (length == 0) throw new IllegalArgumentException("constants variate generator requires a nonempty list of constants")
        resetStartStream()
        this.dist = new ConstantsDistribution(constants as double[])
    }

    public void resetStartStream() {
        state = 0
    }

    public void resetStartSubStream() {
        resetStartStream()
    }

    public void resetNextSubStream() {
        resetStartStream()
    }

    java.lang.String toString() {
        state
    }

    double nextDouble() {
        constants[state++ % length]
    }

    void nextArrayOfDouble(double[] doubles, int start, int duration) {
        while (duration-- > 0) {
            doubles[start++] = nextDouble()
        }
    }

}