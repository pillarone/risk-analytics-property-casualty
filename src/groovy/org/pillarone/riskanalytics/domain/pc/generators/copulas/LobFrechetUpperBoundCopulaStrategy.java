package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LobFrechetUpperBoundCopulaStrategy extends FrechetUpperBoundCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.FRECHETUPPERBOUND;

    public IParameterObjectClassifier getType() {
        return type;
    }
}