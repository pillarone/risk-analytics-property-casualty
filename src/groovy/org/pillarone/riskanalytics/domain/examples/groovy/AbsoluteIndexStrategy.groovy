package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AbsoluteIndexStrategy implements IIndex {

    double index = 1d

    IParameterObjectClassifier getType() {
        IndexType.ABSOLUTE
    }

    Map getParameters() {
        return ['index': index]
    }
}
