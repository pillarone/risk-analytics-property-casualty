package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class FrechetUpperBoundCopulaStrategy extends AbstractCopulaStrategy {

    AbstractMultiDimensionalParameter targets = new SimpleMultiDimensionalParameter([])

    public List<Number> getRandomVector() {
        private IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getUniformGenerator()
        double severity = (double) generator.nextValue()
        return ([severity] * getTargetNames().size())
    }

    public Map getParameters() {
        ["targets": targets]
    }

    public List<String> getTargetNames() {
        targets.values
    }
}