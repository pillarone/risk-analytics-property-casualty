package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

class RelativeIndexStrategy extends AbstractParameterObject implements IIndex {

    double changeIndex

    IParameterObjectClassifier getType() {
        IndexType.RELATIVEPRIORPERIOD
    }

    Map getParameters() {
        return ['changeIndex': changeIndex]
    }

    double getIndex() {
        return changeIndex
    }
}
