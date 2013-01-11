package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class LobIndependentCopulaStrategy extends IndependentCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.INDEPENDENT;

    public IParameterObjectClassifier getType() {
        return type;
    }
}