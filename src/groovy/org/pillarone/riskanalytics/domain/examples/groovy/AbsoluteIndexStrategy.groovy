package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AbsoluteIndexStrategy extends AbstractParameterObject implements IIndex {

    double index = 1d

    IParameterObjectClassifier getType() {
        IndexType.ABSOLUTE
    }

    Map getParameters() {
        return ['index': index]
    }
}
