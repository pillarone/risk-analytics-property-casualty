package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LobNormalCopulaStrategy extends NormalCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.NORMAL;

    public IParameterObjectClassifier getType() {
        return type;
    }
}