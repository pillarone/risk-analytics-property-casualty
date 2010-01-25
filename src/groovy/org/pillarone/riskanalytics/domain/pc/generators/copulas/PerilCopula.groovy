package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PerilCopula extends Copula {

    ICopulaStrategy parmCopulaStrategy = CopulaStrategyFactory.getCopulaStrategy(
            PerilCopulaType.INDEPENDENT,
            ["targets": new SimpleMultiDimensionalParameter([''])]);

    protected List<Number> getRandomVector() {
        return parmCopulaStrategy.getRandomVector();
    }

    protected List<String> getTargetNames() {
        return parmCopulaStrategy.getTargetNames();
    }
}