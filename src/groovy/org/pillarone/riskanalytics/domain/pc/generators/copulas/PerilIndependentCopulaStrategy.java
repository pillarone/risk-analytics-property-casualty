package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PerilIndependentCopulaStrategy extends IndependentCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.INDEPENDENT;

    public IParameterObjectClassifier getType() {
        return type;
    }
}