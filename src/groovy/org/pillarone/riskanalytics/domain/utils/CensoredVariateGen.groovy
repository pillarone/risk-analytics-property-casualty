package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.randvar.RandomVariateGen
import umontreal.iro.lecuyer.rng.RandomStream

/**
 * Given lower (left) and upper (right) bounds, min & max, CensoredVariateGen "wraps" the nextDouble method
 * of a RandomVariateGen to supply censored random variates, that is, random values whose measurements are
 * restrained to the censored range. No change of the distribution is necessary to generate the variates,
 * however the censored distribution will contain jumps at the censor limits (point masses on the pdf),
 * thus turning a continuous distribution into a mixed distribution whenever there is support outside
 * the censor limits.
 *
 * @author ali.majidi (at) munichre (dot) com
 */
@Deprecated
class CensoredVariateGen extends RandomVariateGen {
    double lowerBound, upperBound, shift
    RandomVariateGen generator

    public CensoredVariateGen(RandomStream randomStream, Distribution distribution, double min, double max, double shift = 0) {
        lowerBound = min
        upperBound = max
        this.shift = shift
        generator = new RandomVariateGen(randomStream, distribution)
    }

    double nextDouble() {
        return Math.max(lowerBound, Math.min(upperBound, generator.nextDouble())) + shift
    }
}