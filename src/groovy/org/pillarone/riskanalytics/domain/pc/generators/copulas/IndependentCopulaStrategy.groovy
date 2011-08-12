package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class IndependentCopulaStrategy extends AbstractCopulaStrategy {

    AbstractMultiDimensionalParameter targets;

    public List<Number> getRandomVector() {
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getUniformGenerator();
        List<Number> randomVector = new ArrayList();
        for (int i = 0; i < getTargetNames().size(); i++) {
            randomVector[i] = (double) generator.nextValue();
        }
        return randomVector;
    }

    public Map getParameters() {
        ["targets": targets]
    }

    public List<String> getTargetNames() {
        return targets.values[0];
    }

    public List<Component> getTargetComponents() {
        return targets.getValuesAsObjects(0, false)
    }
}