package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class PerilTCopulaStrategy extends TCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.T;

    public Object getType() {
        return type;
    }
}