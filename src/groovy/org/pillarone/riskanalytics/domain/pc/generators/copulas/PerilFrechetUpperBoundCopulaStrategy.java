package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class PerilFrechetUpperBoundCopulaStrategy extends FrechetUpperBoundCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.FRECHETUPPERBOUND;

    public IParameterObjectClassifier getType() {
        return type;
    }
}