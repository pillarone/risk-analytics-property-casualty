package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class PerilGumbelCopulaStrategy extends GumbelCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.GUMBEL;

    public Object getType() {
        return type;
    }
}