package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class CopulaType extends AbstractParameterObjectClassifier {

    protected CopulaType(String typeName, Map parameters) {
        super(typeName, parameters)
    }

    protected CopulaType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }
}