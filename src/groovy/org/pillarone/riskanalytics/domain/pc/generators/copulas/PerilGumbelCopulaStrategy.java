package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author Michael-Noe (at) Web (dot) de
 */
@Deprecated
class PerilGumbelCopulaStrategy extends GumbelCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.GUMBEL;

    public IParameterObjectClassifier getType() {
        return type;
    }
}