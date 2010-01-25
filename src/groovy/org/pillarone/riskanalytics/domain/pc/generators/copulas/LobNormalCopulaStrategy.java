package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LobNormalCopulaStrategy extends NormalCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.NORMAL;

    public Object getType() {
        return type;
    }
}