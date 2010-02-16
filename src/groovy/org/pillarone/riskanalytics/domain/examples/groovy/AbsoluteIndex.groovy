package org.pillarone.riskanalytics.domain.examples.groovy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AbsoluteIndex implements IIndex {

    double index = 1d

    Object getType() {
        IndexType.ABSOLUTE
    }

    def Map getParameters() {
        return ['index': index]
    }
}
