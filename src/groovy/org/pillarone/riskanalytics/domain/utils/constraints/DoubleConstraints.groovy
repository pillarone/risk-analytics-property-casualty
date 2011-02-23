package org.pillarone.riskanalytics.domain.utils.constraints

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

/**
 * @author shartmann (at) munichre (dot) com
 */
class DoubleConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "DOUBLE"

    boolean matches(int row, int column, Object value) {
        value instanceof Number
    }

    String getName() {
        IDENTIFIER
    }

    Class getColumnType(int column) {
        Double
    }

    Integer getColumnIndex(Class marker) {
        null
    }
}
