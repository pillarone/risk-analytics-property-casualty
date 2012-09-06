package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.util.MathUtils
import umontreal.iro.lecuyer.probdist.BinomialDist
import umontreal.iro.lecuyer.probdist.ContinuousDistribution
import umontreal.iro.lecuyer.probdist.UniformDist
import umontreal.iro.lecuyer.randvar.BinomialGen
import umontreal.iro.lecuyer.randvar.RandomVariateGen
import umontreal.iro.lecuyer.randvar.UniformGen
import umontreal.iro.lecuyer.rng.RandomStream
import umontreal.iro.lecuyer.rng.RandomStreamBase
import umontreal.iro.lecuyer.probdist.DiscreteDistribution
import umontreal.iro.lecuyer.probdist.DiscreteDistributionInt
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * Enables different streams for generators, and parametrization of streams.
 */
// todo: think! reuse generators with same stream and parameter ??
class RandomNumberGeneratorFactory {


    static IRandomNumberGenerator getUniformGenerator() {
        IRandomNumberGenerator uniformGenerator
        UniformGen generator = new UniformGen(MathUtils.getRandomStreamBase(), new UniformDist(0, 1))
        uniformGenerator = new RandomNumberGenerator(generator: generator)
        return uniformGenerator
    }

    static IRandomNumberGenerator getUniformGenerator(RandomStream stream) {
        IRandomNumberGenerator uniformGenerator
        UniformGen generator = new UniformGen(stream, new UniformDist(0, 1))
        uniformGenerator = new RandomNumberGenerator(generator: generator)
        return uniformGenerator
    }

    static IRandomNumberGenerator getBinomialGenerator() {
        IRandomNumberGenerator binomialGenerator
        BinomialGen generator = new BinomialGen(MathUtils.getRandomStreamBase(), new BinomialDist(1, 1))
        binomialGenerator = new RandomNumberGenerator(generator: generator)
        return binomialGenerator
    }

    static IRandomNumberGenerator getGenerator(RandomDistribution distribution) {
        RandomVariateGen generator = new RandomVariateGen(MathUtils.getRandomStreamBase(), distribution.distribution)
        return new RandomNumberGenerator(generator: generator, type: distribution.type, parameters: distribution.parameters)
    }


    static IRandomNumberGenerator getGenerator(RandomDistribution distribution, RandomStream stream) {
        RandomVariateGen generator = new RandomVariateGen(stream, distribution.distribution)
        return new RandomNumberGenerator(generator: generator, type: distribution.type, parameters: distribution.parameters)
    }

    static IRandomNumberGenerator getGenerator(RandomDistribution distribution, DistributionModified modifier) {
        return getGenerator(distribution, modifier, MathUtils.getRandomStreamBase())
    }

    static IRandomNumberGenerator getGenerator(RandomDistribution distribution, DistributionModified modifier, RandomStreamBase randomStream) {
        if (modifier) {
            IRandomNumberGenerator generator
            switch (modifier.type) {
                case DistributionModifier.NONE:
                    generator = new RandomNumberGenerator(
                            generator: new RandomVariateGen(randomStream, distribution.distribution))
                    break
            // for simple truncation or censoring, we access the parameters directly
                case DistributionModifier.CENSORED:
                    generator = new RandomNumberGenerator(
                            generator: new CensoredVariateGen(randomStream, distribution.distribution,
                                    (double) modifier.parameters["min"],
                                    (double) modifier.parameters["max"]))
                    break
                case DistributionModifier.CENSOREDSHIFT:
                    generator = new RandomNumberGenerator(
                            generator: new CensoredVariateGen(randomStream, distribution.distribution,
                                    (double) modifier.parameters["min"],
                                    (double) modifier.parameters["max"],
                                    (double) modifier.parameters["shift"]))
                    break
                case DistributionModifier.SHIFT:
                    generator = new RandomNumberGenerator(
                            generator: new ShiftedVariateGen(randomStream, distribution.distribution,
                                    (double) modifier.parameters["shift"]))
                    break
                case DistributionModifier.TRUNCATED:
                    generator = new RandomNumberGenerator(
                            generator: new RandomVariateGen(randomStream,
                                    new TruncatedDist(distribution.distribution,
                                            (double) modifier.parameters["min"],
                                            (double) modifier.parameters["max"])))

                    break
                case DistributionModifier.TRUNCATEDSHIFT:
                    generator = new RandomNumberGenerator(
                            generator: new ShiftedVariateGen(randomStream,
                                    new TruncatedDist(distribution.distribution,
                                            (double) modifier.parameters["min"],
                                            (double) modifier.parameters["max"]),
                                    (double) modifier.parameters["shift"]))
                    break
                case DistributionModifier.LEFTTRUNCATEDRIGHTCENSOREDSHIFT:
                    generator = new RandomNumberGenerator(
                            generator: new CensoredVariateGen(randomStream,
                                    new TruncatedDist(distribution.distribution,
                                            (double) modifier.parameters["min"],
                                            (double) Double.POSITIVE_INFINITY),
                                    (double) Double.NEGATIVE_INFINITY,
                                    (double) modifier.parameters["max"],
                                    (double) modifier.parameters["shift"]))
                    break
                default:
                    throw new InvalidParameterException("DistributionModifier $modifier.type not implemented")

            }
            generator.modifier = modifier.type
            generator.type = distribution.type
            generator.parameters = distribution.parameters
            return generator
        }
        else {
            return getGenerator(distribution)
        }
    }
}