package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.util.MathUtils
import umontreal.iro.lecuyer.probdist.BinomialDist
import umontreal.iro.lecuyer.probdist.ContinuousDistribution
import umontreal.iro.lecuyer.probdist.TruncatedDist
import umontreal.iro.lecuyer.probdist.UniformDist
import umontreal.iro.lecuyer.randvar.BinomialGen
import umontreal.iro.lecuyer.randvar.RandomVariateGen
import umontreal.iro.lecuyer.randvar.UniformGen

// todo: think! reuse generators with same stream and parameter ??
// enable different streams for generators
// enable parameterization of stream
class RandomNumberGeneratorFactory {


    static IRandomNumberGenerator getUniformGenerator() {
        IRandomNumberGenerator uniformGenerator
        UniformGen generator = new UniformGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE, new UniformDist(0, 1))
        uniformGenerator = new RandomNumberGenerator(generator: generator)
        return uniformGenerator
    }

    static IRandomNumberGenerator getBinomialGenerator() {
        IRandomNumberGenerator binomialGenerator
        BinomialGen generator = new BinomialGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE, new BinomialDist(1, 1))
        binomialGenerator = new RandomNumberGenerator(generator: generator)
        return binomialGenerator
    }

    static IRandomNumberGenerator getGenerator(RandomDistribution distribution) {
        RandomVariateGen generator = new RandomVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE, distribution.distribution)
        return new RandomNumberGenerator(generator: generator, type: distribution.type, parameters: distribution.parameters)
    }

    static IRandomNumberGenerator getGenerator(RandomDistribution distribution, DistributionModified modifier) {
        if (modifier) {
            IRandomNumberGenerator generator
            switch (modifier.type) {
                case DistributionModifier.NONE:
                    generator = new RandomNumberGenerator(
                        generator: new RandomVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE, distribution.distribution),
                        type: distribution.type,
                        parameters: distribution.parameters)
                    break
                case DistributionModifier.CENSORED:
                    generator = new RandomNumberGenerator(
                        generator: new CensoredVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE, distribution.distribution, modifier.parameters["min"], modifier.parameters["max"]),
                        type: distribution.type,
                        parameters: distribution.parameters)
                    break
                case DistributionModifier.CENSOREDSHIFT:
                    generator = new RandomNumberGenerator(
                        generator: new CensoredVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE,
                            distribution.distribution,
                            modifier.parameters["min"],
                            modifier.parameters["max"],
                            modifier.parameters["shift"]),
                        type: distribution.type,
                        parameters: distribution.parameters)
                    break
                case DistributionModifier.SHIFT:
                    generator = new RandomNumberGenerator(
                        generator: new ShiftedVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE, distribution.distribution, modifier.parameters["shift"]),
                        type: distribution.type,
                        parameters: distribution.parameters)
                    break
                case DistributionModifier.TRUNCATED:
                    if (distribution.distribution instanceof ContinuousDistribution) {
                        generator = new RandomNumberGenerator(
                            generator: new RandomVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE,
                                new TruncatedDist(distribution.distribution, modifier.parameters["min"], modifier.parameters["max"])),
                            type: distribution.type,
                            parameters: distribution.parameters)
                    }
                    else {
                        throw new IllegalArgumentException("The distribution ${distribution.distribution} is not a ContinuousDistribution! Truncation option is therefore not available.")
                    }
                    break
                case DistributionModifier.TRUNCATEDSHIFT:
                    if (distribution.distribution instanceof ContinuousDistribution) {
                        generator = new RandomNumberGenerator(
                            generator: new ShiftedVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE,
                                new TruncatedDist(distribution.distribution, modifier.parameters["min"], modifier.parameters["max"]),
                                modifier.parameters["shift"]),
                            type: distribution.type,
                            parameters: distribution.parameters)
                    }
                    else {
                        throw new IllegalArgumentException("The distribution ${distribution.distribution} is not a ContinuousDistribution! Truncation option is therefore not available.")
                    }
                    break
                case DistributionModifier.LEFTTRUNCATEDRIGHTCENSORED:
                    if (distribution.distribution instanceof ContinuousDistribution) {
                        generator = new RandomNumberGenerator(
                            generator: new CensoredVariateGen(MathUtils.RANDOM_NUMBER_GENERATOR_INSTANCE,
                                new TruncatedDist(distribution.distribution, modifier.parameters["min"], Double.POSITIVE_INFINITY),
                                Double.NEGATIVE_INFINITY,
                                modifier.parameters["max"]),
                            type: distribution.type,
                            parameters: distribution.parameters)
                    }
                    else {
                        throw new IllegalArgumentException("The distribution ${distribution.distribution} is not a ContinuousDistribution! Truncation option is therefore not available.")
                    }
                    break

            }
            generator.modifier = modifier.type
            return generator
        }
        else {
            return getGenerator(distribution)
        }
    }
}