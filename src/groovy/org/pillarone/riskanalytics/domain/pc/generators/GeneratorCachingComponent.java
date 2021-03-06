package org.pillarone.riskanalytics.domain.pc.generators;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.domain.utils.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class GeneratorCachingComponent extends Component {

    private Map<String, IRandomNumberGenerator> generators = new HashMap<String, IRandomNumberGenerator>();

    protected IRandomNumberGenerator getCachedGenerator(RandomDistribution distribution, DistributionModified modifier) {
        String key = key(distribution, modifier);
        IRandomNumberGenerator generator;
//        if (modifier.getType().equals(DistributionModifier.TRUNCATED)) {
//            double quantileOfMax = distribution.getDistribution().cdf((Double) modifier.getParameters().get("max"));
//            double quantileOfMin = distribution.getDistribution().cdf((Double) modifier.getParameters().get("min"));
//            if (quantileOfMax - quantileOfMin < 1E-4) {
//                throw new IllegalArgumentException("GeneratorCachingComponent.insufficientSupport");
//            }
//        }
        if (generators.containsKey(key)) {
            generator = generators.get(key);
        } else {
            try {
                generator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
            }
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("['GeneratorCachingComponent.illegalDistributionDistributionModifier','"
                        +this.getNormalizedName()+"','"+ex.getLocalizedMessage()+"']");
            }
            generators.put(key, generator);
        }
        return generator;
    }

    private String key(RandomDistribution distribution, DistributionModified modifier) {
        return String.valueOf(distribution.hashCode()) + String.valueOf(modifier.hashCode());
    }

}
