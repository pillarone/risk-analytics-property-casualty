package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author Michael-Noe (at) Web (dot) de
 */
@Deprecated
class LobTCopulaStrategy extends TCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.T;

    public IParameterObjectClassifier getType() {
        return type;
    }
}