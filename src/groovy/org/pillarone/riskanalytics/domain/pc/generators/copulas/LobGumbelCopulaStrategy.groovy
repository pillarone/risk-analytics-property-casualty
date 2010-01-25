package org.pillarone.riskanalytics.domain.pc.generators.copulas

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class LobGumbelCopulaStrategy extends GumbelCopulaStrategy {

    static final LobCopulaType type = LobCopulaType.GUMBEL

    public Object getType() {
        type
    }
}