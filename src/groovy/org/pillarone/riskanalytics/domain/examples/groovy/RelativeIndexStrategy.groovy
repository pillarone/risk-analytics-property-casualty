package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

class RelativeIndexStrategy implements IIndex {

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