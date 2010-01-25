package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.randvar.RandomVariateGen
import umontreal.iro.lecuyer.randvar.RandomVariateGenInt

class RandomNumberGenerator implements IRandomNumberGenerator, IParameterObject {

    protected RandomVariateGen generator
    DistributionType type
    DistributionModifier modifier
    Map parameters

    DistributionType getType() {
        type
    }

    public Number nextValue() {
        assert generator != null
        if (generator instanceof RandomVariateGenInt) {
            return generator.nextInt()
        }
        return generator.nextDouble()
    }

    public Distribution getDistribution() {
        generator.distribution
    }
}