package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LobFrechetUpperBoundCopulaStrategy extends FrechetUpperBoundCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.FRECHETUPPERBOUND;

    public Object getType() {
        return type;
    }
}