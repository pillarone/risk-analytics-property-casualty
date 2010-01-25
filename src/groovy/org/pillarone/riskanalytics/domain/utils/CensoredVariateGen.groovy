package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.randvar.RandomVariateGen
import umontreal.iro.lecuyer.rng.RandomStream

/**
 * @author ali.majidi (at) munichre (dot) com
 */
class CensoredVariateGen extends RandomVariateGen {
    double lowerBound, upperBound, shift
    RandomVariateGen generator

    public CensoredVariateGen(RandomStream randomStream, Distribution distribution, double min, double max) {
        lowerBound = min
        upperBound = max
        generator = new RandomVariateGen(randomStream, distribution)
    }

    public CensoredVariateGen(RandomStream randomStream, Distribution distribution, double min, double max, double shift) {
        lowerBound = min
        upperBound = max
        this.shift = shift
        generator = new RandomVariateGen(randomStream, distribution)
    }

    double nextDouble() {
        return Math.max(lowerBound, Math.min(upperBound, generator.nextDouble())) + shift
    }
}