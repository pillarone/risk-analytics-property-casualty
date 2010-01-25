package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PerilFrechetUpperBoundCopulaStrategy extends FrechetUpperBoundCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.FRECHETUPPERBOUND;

    public Object getType() {
        return type;
    }
}