package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.randvar.RandomVariateGen
import umontreal.iro.lecuyer.rng.RandomStream

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ShiftedVariateGen extends RandomVariateGen {
    double shift
    RandomVariateGen generator

    public ShiftedVariateGen(RandomStream randomStream, Distribution distribution, double shift) {
        this.shift = shift
        generator = new RandomVariateGen(randomStream, distribution)
    }

    double nextDouble() {
        generator.nextDouble() + shift
    }
}