package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LobIndependentCopulaStrategy extends IndependentCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.INDEPENDENT;

    public Object getType() {
        return type;
    }
}