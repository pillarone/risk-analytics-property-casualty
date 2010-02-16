package org.pillarone.riskanalytics.domain.examples.groovy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

class RelativeIndex implements IIndex {

    double changeIndex

    Object getType() {
        IndexType.RELATIVEPRIORPERIOD
    }

    def Map getParameters() {
        return ['changeIndex': changeIndex]
    }

    double getIndex() {
        return changeIndex
    }
}