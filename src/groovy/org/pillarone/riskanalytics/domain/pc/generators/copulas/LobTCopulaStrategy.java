package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class LobTCopulaStrategy extends TCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.T;

    public Object getType() {
        return type;
    }
}