package org.pillarone.riskanalytics.domain.pc.generators.copulas;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PerilNormalCopulaStrategy extends NormalCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.NORMAL;

    public Object getType() {
        return type;
    }
}