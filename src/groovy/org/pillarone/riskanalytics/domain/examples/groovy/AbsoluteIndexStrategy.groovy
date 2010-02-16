package org.pillarone.riskanalytics.domain.examples.groovy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AbsoluteIndexStrategy implements IIndex {

    double index = 1d

    Object getType() {
        IndexType.ABSOLUTE
    }

    Map getParameters() {
        return ['index': index]
    }
}
