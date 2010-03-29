package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class LobGumbelCopulaStrategy extends GumbelCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.GUMBEL

    public IParameterObjectClassifier getType() {
        type
    }
}