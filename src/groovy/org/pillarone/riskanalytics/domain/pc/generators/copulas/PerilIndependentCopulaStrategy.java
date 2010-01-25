package org.pillarone.riskanalytics.domain.pc.generators.copulas;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PerilIndependentCopulaStrategy extends IndependentCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.INDEPENDENT;

    public Object getType() {
        return type;
    }
}