package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class PerilNormalCopulaStrategy extends NormalCopulaStrategy {

    static final PerilCopulaType type = PerilCopulaType.NORMAL;

    public IParameterObjectClassifier getType() {
        return type;
    }
}