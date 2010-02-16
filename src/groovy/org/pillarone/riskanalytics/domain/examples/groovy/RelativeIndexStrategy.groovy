package org.pillarone.riskanalytics.domain.examples.groovy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

class RelativeIndexStrategy implements IIndex {

    double changeIndex

    Object getType() {
        IndexType.RELATIVEPRIORPERIOD
    }

    Map getParameters() {
        return ['changeIndex': changeIndex]
    }

    double getIndex() {
        return changeIndex
    }
}