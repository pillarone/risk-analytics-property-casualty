package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class PerilTCopulaStrategy extends TCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.T;

    public IParameterObjectClassifier getType() {
        return type;
    }
}