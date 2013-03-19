package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.randvar.RandomVariateGen
import umontreal.iro.lecuyer.randvar.RandomVariateGenInt
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

@Deprecated
class RandomNumberGenerator extends AbstractParameterObject implements IRandomNumberGenerator {

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
